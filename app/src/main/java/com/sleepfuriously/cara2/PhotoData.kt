package com.sleepfuriously.cara2

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy

/**
 * Holds all the data that is attached to a photo, including
 * the image itself.
 */
data class PhotoData(
    /** name of the user */
    var user : String = "",
    /** message to send along with th e photo */
    var msg : String = "",
    /** coords of the photo */
    var gpsCoords : String = "",
    /** the image itself */
    var image : ImageProxy?
    ) {

    init {
        image = null
    }

    override fun toString(): String {
        return "[user = $user], [msg = $msg], [gpsCoords = $gpsCoords], [image = $image]"
    }


    fun newPhoto(newImage: ImageProxy) {
        image = newImage
    }


}
