package com.example.rs_school_task_5.managers

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

object PermissionManager {

    var LAST_PERMISSION_CODE = 784
        private set

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
                Log.d("TAG", permissions[index])
                if (PermissionManager.permissions.containsKey(permissions[index])) {
                    PermissionManager.permissions.replace(
                        permissions[index],
                        grantResult == PackageManager.PERMISSION_GRANTED
                    )
                } else {
                    PermissionManager.permissions.put(
                        permissions[index],
                        grantResult == PackageManager.PERMISSION_GRANTED
                    )
                }
            }
        }
    }
}
