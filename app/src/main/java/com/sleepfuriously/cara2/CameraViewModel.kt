package com.sleepfuriously.cara2

import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for passing camera data around.  So this is pretty much
 * just a holder for the data--doesn't do much.
 */
class CameraViewModel : ViewModel() {

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


    //-----------------
    //  public functions
    //-----------------


    //-----------------
    //  private functions
    //-----------------


}


