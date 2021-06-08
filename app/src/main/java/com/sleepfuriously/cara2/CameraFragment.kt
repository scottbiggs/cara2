package com.sleepfuriously.cara2

import android.media.MediaActionSound
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
//import kotlinx.android.synthetic.main.fragment_camera_layout.*
import java.io.File
import java.util.concurrent.ExecutorService



//---------------------------
//  external constants
//---------------------------

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {

    //---------------------------
    //  constants
    //---------------------------

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }


    //---------------------------
    //  data
    //---------------------------

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mCameraViewModel : CameraViewModel

    lateinit var mNavController : NavController

    /** Will display the viewfinder for taking pictures */
    private lateinit var mViewFinder : PreviewView

    //
    //  camera stuff
    //
    private var mImageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private val mSound = MediaActionSound()


    //---------------------------
    //  overridden functions
    //---------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            displayExitMessage()
        }

        mNavController = findNavController()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_layout, container, false)
    }


    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        Log.v(TAG, "onViewCreated()")

        // set the title
        requireActivity().title = getString(R.string.camera_frag_title)

        // get a copy of the CameraViewModel
        mCameraViewModel = ViewModelProvider(requireActivity()).get(CameraViewModel::class.java)

        // get the camera view going
        startCamera()

        // Set up the listener for take photo button
        val cameraButt = v.findViewById<Button>(R.id.camera_capture_button)
        cameraButt.setOnClickListener {
            takePhoto()
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        mViewFinder = v.findViewById(R.id.viewFinder)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (cameraExecutor.isShutdown == false) {
            cameraExecutor.shutdown()
        }
    }


    //---------------------------
    //  private functions
    //---------------------------

    private fun displayExitMessage() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.exit_app_msg)
            .setCancelable(false)
            .setPositiveButton(R.string.exit_app_confirm) { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton(R.string.exit_app_cancel) { dialog, _ ->
                dialog.cancel()
            }

        val alert = builder.create()
        alert.setTitle(R.string.exit_app_title)
        alert.show()
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case.
        // Exit if the user has tapped on the button before setup is finished (it's running
        // in another thread, remember?)
        val imageCapture = mImageCapture ?: return

        // play some sound
        mSound.play(MediaActionSound.SHUTTER_CLICK)

        // Set up image capture listener, which is triggered after photo has
        // been taken.
        // This version waits until the picture is captured to memory
        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
                                 object : ImageCapture.OnImageCapturedCallback() {

                                     override fun onCaptureSuccess(image: ImageProxy) {
                                         super.onCaptureSuccess(image)
                                         Log.v(TAG, "image successfully captured")

                                         // pass the image along to the view model
                                         mCameraViewModel.mImageProxyLiveData.value = image

                                         // and take us to the next fragment
                                         findNavController().navigate(R.id.action_cameraFragment_to_pictureTakenFragment)
                                     }

                                     override fun onError(e: ImageCaptureException) {
                                         Log.e(TAG, "Failed to capture image!\ne = ${e.message}")
                                         super.onError(e)
                                     }
                                 })

/*
        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata.
        // This will hold the photo (if successful).
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()


        // this version waits until the picture is saved
        imageCapture.takePicture(outputOptions,
                                 ContextCompat.getMainExecutor(requireContext()),
                                 object : ImageCapture.OnImageSavedCallback {
                // Fail!
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                // Success
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(requireActivity().baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    // pass the image along to the view model
                    mViewModel.mImageProxyLiveData.value = imageCapture.

                    // and take us to the next fragment
                    findNavController().navigate(R.id.action_cameraFragment_to_pictureTakenFragment)
                }
            })
*/
    }


    /**
     * Sets up the Camera system
     */
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner, eliminating the
            // need to worry about starting/stopping the camera (not that that was a big deal anyway)
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mViewFinder.surfaceProvider)
                }

            // prepare the place to hold the photo
            mImageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, mImageCapture)

            } catch(e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }

        return if ((mediaDir != null) && mediaDir.exists()) {
            mediaDir
        } else {
            requireActivity().filesDir
        }
    }

}