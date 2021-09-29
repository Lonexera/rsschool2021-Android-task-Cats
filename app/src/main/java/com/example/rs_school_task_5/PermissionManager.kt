package com.example.rs_school_task_5

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionManager {

    var LAST_PERMISSION_CODE = 784
    private set(code: Int) {
        field = code
    } 

    private val permissions: HashMap<String, Boolean> = hashMapOf()

    fun checkIfPermissionIsGranted(permission: String): Boolean {
        return if (permissions.isNotEmpty())
            permissions.get(permission) ?: false
        else false
    }
    
    fun requestPermission(activity: Activity, permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                permission
            ),
            ++LAST_PERMISSION_CODE
        )
    }
    
    fun onRequestResult(permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            grantResults.forEachIndexed { index, grantResult -> 
                this.permissions.put(
                    permissions[index],
                    grantResult == PackageManager.PERMISSION_GRANTED
                )
            }
        }
    }
}

