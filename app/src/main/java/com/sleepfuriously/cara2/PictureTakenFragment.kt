package com.sleepfuriously.cara2

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.ImageProxy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = getString(R.string.picture_taken_frag_title)

        // set username textview
//        val nameTv = view.findViewById<TextView>(R.id.username_tv)
//        nameTv.text = mCameraViewModel.mNameLiveData.value.toString()

        mPhotoIv = view.findViewById(R.id.pic_iv)
        mPhotoIv.setImageBitmap(mBitmap)
        mPhotoIv.rotation = getRotation(mImageProxy)

        mDescEt = view.findViewById(R.id.photo_desc_et)

        mSendButt = view.findViewById(R.id.send_butt)
        mSendButt.setOnClickListener {
            // message data
            mCameraViewModel.mMsgLiveData.value = mDescEt.text.toString()
            sendAllData()
        }

        mRetakeButt = view.findViewById(R.id.photo_butt)
        mRetakeButt.setOnClickListener {
            findNavController().navigate(R.id.action_pictureTakenFragment_to_cameraFragment)
        }

        // open soft keyboard
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        mDescEt.requestFocus()  // redundant--already has focus in layout file
        imm.showSoftInput(mDescEt, InputMethodManager.SHOW_IMPLICIT)

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
     * Sends all our collected data to wherever it needs to go.
     * Right now the data is:
     *      username
     *      gps todo
     *      message for picture
     *      picture data
     */
    private fun sendAllData() {
        // todo
        Toast.makeText(requireContext(), "need implement sendAllData()", Toast.LENGTH_SHORT).show()
    }



}