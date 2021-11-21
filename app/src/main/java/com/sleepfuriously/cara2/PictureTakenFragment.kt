package com.sleepfuriously.cara2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.camera.core.ImageProxy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


/**
 * Fragment that lands immediately after the user takes a picture.
 *
 * The point here is to show the user the picture, ask if he wants to add
 * any info/text to the picture, and provide a way to send off the data.
 */
class PictureTakenFragment : Fragment() {

    //-------------------------------
    //  constants
    //-------------------------------

    private val TAG = "PictureTakenFragment"

    /** max size any dimension of the bitmap can take */
    private val MAX_BITMAP_SIZE = 800


    //-------------------------------
    //  data
    //-------------------------------

    private lateinit var mCameraViewModel : CameraViewModel

    private lateinit var mImageProxy : ImageProxy

    private lateinit var mBitmap : Bitmap

    //-------------------------------
    //  widgets
    //-------------------------------

    /** displays the picture */
    private lateinit var mPhotoIv : ImageView

    /** holds the user's description of the picture */
    private lateinit var mDescEt : EditText

    /** button to send this info */
    private lateinit var mSendButt : Button

    /** sends the user back to take another photo */
    private lateinit var mRetakeButt : Button

    //-------------------------------
    //  functions
    //-------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup the viewmodel
        mCameraViewModel = ViewModelProvider(requireActivity()).get(CameraViewModel::class.java)

        // setup the bitmap
        mImageProxy = mCameraViewModel.mImageProxyLiveData.value!!

        // And put that image into the imageview
        mBitmap = convertImageProxyToBitmap(mImageProxy)

        // while we're at it, scale the image down to a reasonable size
        val scale = findBitMapScale(mBitmap, MAX_BITMAP_SIZE)
        if (scale < 1) {
            // only bother if we need to scale down.  Why make the image bigger?
            mBitmap = scaleBitMap(mBitmap, scale)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture_taken_layout, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        requireActivity().title = getString(R.string.picture_taken_frag_title)

        // set username textview
//        val nameTv = view.findViewById<TextView>(R.id.username_tv)
//        nameTv.text = mCameraViewModel.mNameLiveData.value.toString()

        mPhotoIv = v.findViewById(R.id.pic_iv)
        mPhotoIv.setImageBitmap(mBitmap)
        mPhotoIv.rotation = getRotation(mImageProxy)

        mDescEt = v.findViewById(R.id.photo_desc_et)

        mSendButt = v.findViewById(R.id.send_butt)
        mSendButt.setOnClickListener {
            // save some data to the view model
            mCameraViewModel.mMsgLiveData.value = mDescEt.text.toString()
            mCameraViewModel.mCurrentImageEncoded = getImageData(mPhotoIv, 800) // todo: remove the char limit

            // and switch to the next Fragment
            findNavController().navigate(R.id.action_pictureTakenFragment_to_sendDataFragment)
        }

        mRetakeButt = v.findViewById(R.id.photo_butt)
        mRetakeButt.setOnClickListener {
            findNavController().navigate(R.id.action_pictureTakenFragment_to_cameraFragment)
        }

        // open soft keyboard  (nah, kind of gets in the way at first)
//        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(mDescEt, InputMethodManager.SHOW_IMPLICIT)

    }


    /**
     * From https://stackoverflow.com/a/61910885/624814
     */
    private fun convertImageProxyToBitmap(imageProxy : ImageProxy) : Bitmap {
        val buffer : ByteBuffer = imageProxy.planes[0].buffer
        buffer.rewind()
        val byteArray = ByteArray(buffer.capacity())
        buffer.get(byteArray)
        val clonedBytes : ByteArray = byteArray.clone()
        return BitmapFactory.decodeByteArray(clonedBytes, 0, clonedBytes.size)
    }


    private fun getRotation(imageProxy : ImageProxy) : Float {
        Log.d(TAG, "getRotation = ${imageProxy.imageInfo.rotationDegrees}")
        return imageProxy.imageInfo.rotationDegrees.toFloat()
    }


    /**
     * Finds the amount to scale the given bitmap so that it's maximum dimension
     * fits within the given max value.
     */
    private fun findBitMapScale(origBmp: Bitmap, maxDimen : Int) : Float {
        if (origBmp.width > origBmp.height) {
            return maxDimen.toFloat() / origBmp.width.toFloat()
        }
        else {
            return maxDimen.toFloat() / origBmp.height.toFloat()
        }
    }


    /**
     * Scales the given bitmap by the given amount.
     *
     * @param   origBmp     The original bitmap. will be unchanged.
     *
     * @param   scale       The number to scale the bitmap by.  1 will make it unchanged.
     */
    private fun scaleBitMap(origBmp : Bitmap, scale : Float) : Bitmap {
        Log.d(TAG, "scaling bitmap by $scale amount")

        // find the dimensions and multiply the scale
        val width = (origBmp.width * scale).toInt()
        val height = (origBmp.height * scale).toInt()
        return Bitmap.createScaledBitmap(origBmp, width, height, false)
    }


    /**
     * Grabs the image data from the current imageView and returns it as a Base64 String.
     *
     * @param   imageView   The ImageView that is currently displaying an image.
     *
     * @param   charLimit   The max number of chars to return.  Data will be truncated.
     *                      0 means no limit (default).  Note this is an Int, so that
     *                      limits the file size to 2 gigabytes (approx).
     *
     * @return  The image data converted to Base64 encoded String.
     */
    private fun getImageData(imageView: ImageView, charLimit : Int = 0) : String {

        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        return encodeString(byteArrayOutputStream.toByteArray(), charLimit)
    }

    /**
     * Coroutine function that encodes a byteArray into a String.  Because of the large sizes, this
     * could potentially take a while, thus the coroutine.
     *
     * @param   byteArray       An array of bytes that needs to be converted to a String.
     *                          Quite likely this will be an image.
     *
     * @param   charLimit       The maximum length of the string to be created.
     */
    private fun encodeString (byteArray : ByteArray, charLimit : Int = 0) : String = runBlocking {
        var str = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // truncate
        if ((charLimit != 0) && (str.length > charLimit)) {
            str = str.subSequence(0, charLimit - 1) as String
        }
        str     // this value is returned
    }

}