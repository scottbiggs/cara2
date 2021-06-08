package com.sleepfuriously.cara2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    private lateinit var mImageProxy: ImageProxy

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
        // And put that image into the imageview
        val photoIv = view.findViewById<ImageView>(R.id.pic_iv)
        val bitmap = convertImageProxyToBitmap(mImageProxy)
        photoIv.setImageBitmap(bitmap)
        photoIv.rotation = getRotation(mImageProxy)
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



//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment PictureTakenFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PictureTakenFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }

}