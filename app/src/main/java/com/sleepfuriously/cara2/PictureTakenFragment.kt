package com.sleepfuriously.cara2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    /** the uri to send our mail to.  Right now it's my email address */
    private val EMAIL_DATA = "mailto:"

    /** data type for the email Intent */
    private val EMAIL_TYPE = "text/plain"

    /** recipients of the emails */
    private val EMAIL_RECIPIENTS : Array<String> = arrayOf("scottmorganbiggs@gmail.com")

    /** subject of the emails */
    private val EMAIL_SUBJECT = "photo-msg for cara"

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
     *      picture data
     *      message for picture
     *      gps todo
     */
    private fun sendAllData() {

        // part 1, save the user's name as a file

        // part 2, save the accompanying message as a file
        //  (files can be saved using the Storage Access Framework)
        // todo

        // part 3, save the bitmap as a file.  This way the mail program will know how to find it.
        //  (try using MediaStore api)
        // todo

        // part 4, save the gps as a file.
        // todo

        // part 5, tell a mail program to send off this data
        launchEmail()
    }


    /**
     * Use an Intent to launch am email action
     *
     * code taken from:
     *      https://www.tutorialspoint.com/android/android_sending_email.htm
     */
    private fun launchEmail() {

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse(EMAIL_DATA)

        emailIntent.putExtra(Intent.EXTRA_EMAIL, EMAIL_RECIPIENTS)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT)

        // compose the message
        val msg =
                getString(R.string.picture_taken_email_name_prefix) +
                (mCameraViewModel.mNameLiveData.value ?: getString(R.string.picture_taken_email_no_name)) +
                "\n\n" +
                getString(R.string.picture_taken_email_msg_prefix) +
                (mCameraViewModel.mMsgLiveData.value ?: getString(R.string.picture_taken_email_no_msg))
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg)

        try {
            // todo: find a way to know when the email has been sent and then go back to the CameraFragment
            //  right now, I'm just going back to the picture automatically--potentially losing some data.
            startActivity(Intent.createChooser(emailIntent, "sending..."))
            findNavController().navigate(R.id.action_pictureTakenFragment_to_cameraFragment)
        }
        catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Unable to send email--but why? Try reading the following:")
            e.printStackTrace()
            Toast.makeText(requireActivity(), getString(R.string.picture_taken_unable_to_send_email), Toast.LENGTH_LONG)
                .show()
        }
    }

}