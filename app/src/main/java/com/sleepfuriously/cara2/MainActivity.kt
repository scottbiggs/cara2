package com.sleepfuriously.cara2

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * MainActivity for Cara's project.
 *
 * The project allows the user to:
 *  1. login
 *  2. take a picture
 *  3. record a description of the picture
 *  4. send the picture, description, and geolocation to a
 *     restful endpoint
 *  5. Repeat as desired
 */
class MainActivity : AppCompatActivity() {

    //------------------------
    //  constants
    //------------------------

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val TAG = "MainActivity"

    //------------------------
    //  data
    //------------------------

    //------------------------
    //  functions
    //------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v(TAG, "onCreate()")

        // uncomment this for a functional toobar (title bar, action bar, whatever)
//        val toolbar : Toolbar = findViewById(R.id.main_toolbar)
//        setSupportActionBar(toolbar)

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, R.string.permissions_not_granted, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    fun setupCameraPermissions() {
        // Request camera permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
        }
    }


    /**
     * Checks to see if all permissions have been granted.
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}