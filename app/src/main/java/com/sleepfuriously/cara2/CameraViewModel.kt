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


    //-----------------
    //  public functions
    //-----------------

//    fun saveUserName(newName : String) {
//        mNameLiveData.value = newName
//        Log.d(TAG, "name changed to ${mNameLiveData.value.toString()}, I am $this")
//    }

//    fun getUserName() : String {
//        Log.d(TAG, "returning name ${mNameLiveData.value.toString()}, I am $this")
//        return mNameLiveData.value.toString()
//    }



    override fun onCleared() {
        Log.d(TAG, "onCleared(), I am $this")
        super.onCleared()
    }

    //-----------------
    //  private functions
    //-----------------


}


