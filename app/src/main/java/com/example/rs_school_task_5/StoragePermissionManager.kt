package com.example.rs_school_task_5

import android.app.Activity
import android.os.Build

object StoragePermissionManager {

    private var writePermissionGranted: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun checkForPermission(): Boolean {
        return if (writePermissionGranted)
            writePermissionGranted
        else
            PermissionManager.checkIfPermissionIsGranted(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun requestPermission(activity: Activity) {
        PermissionManager.requestPermission(
            activity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
