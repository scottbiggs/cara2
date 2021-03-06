package com.sleepfuriously.cara2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.volley.Request

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/** you can view this toilet at https://ptsv2.com/t/9k0vk-1623901532 */
const val TOILET_URL = "https://ptsv2.com/t/9k0vk-1623901532/post"

/**
 * A simple [Fragment] subclass.
 * Use the [SendDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SendDataFragment : Fragment() {

    //-----------------------------
    //  data
    //-----------------------------

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /** reference to the view model */
    private lateinit var mCameraViewModel : CameraViewModel


    //-----------------------------
    //  functions
    //-----------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // get reference to viewmodel
        mCameraViewModel = ViewModelProvider(requireActivity()).get(CameraViewModel::class.java)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_data_layout, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        requireActivity().title = getString(R.string.camera_frag_title)

        val cancelButt : Button = v.findViewById(R.id.cancel_send_butt)
        cancelButt.setOnClickListener {
            cancelSendImage()
        }

        // start the send process immediately (that's why we're here!)
        uploadVersion2()
    }


    /**
     * Cancels a send in progress.  Returns the user to [PictureTakenFragment].
     */
    private fun cancelSendImage() {
        // todo: cancel the send !!!!!

        // navigate to previous fragment
        findNavController().navigateUp()    // don't have to fuck with the stack this way
    }


    private fun uploadVersion2() {
        // call to upload this image
        Log.d(TAG, "attempting multi-part upload...")
        val multiPartUpload = MultipartWebservice(requireContext())
        multiPartUpload.sendMultipartRequest(
            Request.Method.POST,
            TOILET_URL,
            mCameraViewModel.mCurrentImageEncoded.toByteArray(),    // fixme: redudant--should convert the image to ByteArray first thing
            "foo.tmp",
            { response ->
                Log.d(TAG, "Success!  response -> ${response.data}")
                Toast.makeText(requireContext(), R.string.successful_send, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()    // return
            },
            { error ->
                // todo implement error listener
                Log.e(TAG, "error -> $error")
            }
        )
    }



    companion object {

        const val TAG = "SendDataFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SendDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SendDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}