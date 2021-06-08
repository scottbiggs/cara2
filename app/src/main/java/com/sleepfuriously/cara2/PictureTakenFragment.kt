package com.sleepfuriously.cara2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.camera.core.ImageProxy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    //-------------------------------
    //  data
    //-------------------------------

    private lateinit var mCameraViewModel : CameraViewModel

    private lateinit var mImageProxy : ImageProxy

    private lateinit var mPhotoIv : ImageView

    //-------------------------------
    //  functions
    //-------------------------------

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

        mCameraViewModel = ViewModelProvider(requireActivity()).get(CameraViewModel::class.java)

        // set username textview
//        val nameTv = view.findViewById<TextView>(R.id.username_tv)
//        nameTv.text = mCameraViewModel.mNameLiveData.value.toString()

        // set image
        mImageProxy = mCameraViewModel.mImageProxyLiveData.value!!
        mPhotoIv = view.findViewById(R.id.pic_iv)

        // And put that image into the imageview
        val bitmap = convertImageProxyToBitmap(mImageProxy)
        mPhotoIv.setImageBitmap(bitmap)
        mPhotoIv.rotation = getRotation(mImageProxy)
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


}