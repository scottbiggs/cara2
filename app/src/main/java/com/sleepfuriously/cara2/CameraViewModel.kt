package com.sleepfuriously.cara2

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for passing camera data around.  So this is pretty much
 * just a holder for the data--doesn't do much.
 */
class CameraViewModel : ViewModel() {

    //-----------------
    //  constants
    //-----------------

    private val TAG = "CameraViewModel"

    //-----------------
    //  data
    //-----------------

    /** livedata accessor to the imageCapture */
    val mImageProxyLiveData = MutableLiveData<ImageProxy>()

    /** livedata accessor to user name */
    val mNameLiveData = MutableLiveData<String>()

    /** livedata accessor to msg */
    val mMsgLiveData = MutableLiveData<String>()

    /** livedata accessor to user gps coords */
    val mGpsLiveData = MutableLiveData<String>()

    /** A String version of the latest photo that was taken */
    var mCurrentImageEncoded : String = ""


    //-----------------
    //  public functions
    //-----------------

    override fun onCleared() {
        Log.d(TAG, "onCleared(), I am $this")
        super.onCleared()
    }

    //-----------------
    //  private functions
    //-----------------


}


