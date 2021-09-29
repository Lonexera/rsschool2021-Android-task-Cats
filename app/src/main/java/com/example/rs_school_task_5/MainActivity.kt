package com.example.rs_school_task_5

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rs_school_task_5.adapter.CatAdapter
import com.example.rs_school_task_5.adapter.CatLoaderStateAdapter
import com.example.rs_school_task_5.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var writePermissionGranted = false

    private val catsViewModel by viewModels<CatViewModel> { CatViewModel.Factory() }
    private val catAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CatAdapter { displayName, bitmap ->
            lifecycleScope.launch { saveImageToGallery(displayName, bitmap) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = catAdapter.withLoadStateHeaderAndFooter(
                header = CatLoaderStateAdapter(),
                footer = CatLoaderStateAdapter()
            )
        }

        catAdapter.addLoadStateListener { state ->
            with(binding) {
                recyclerView.isVisible = state.refresh != LoadState.Loading
                progressBar.isVisible = state.refresh == LoadState.Loading
            }
        }

        lifecycleScope.launch {
            catsViewModel.cats.collectLatest {
                catAdapter.submitData(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PermissionManager.LAST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                PermissionManager.onRequestResult(permissions, grantResults)
                writePermissionGranted = PermissionManager.checkIfPermissionIsGranted(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } else
                showPermissionDeniedToast()
        }
    }

    private fun checkForPermission() {
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val hasWritePermission = PermissionManager.checkIfPermissionIsGranted(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        writePermissionGranted = hasWritePermission || minSdk29
    }

    private fun requestPermission() {
        PermissionManager.requestPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(this, "Permission to write was denied!", Toast.LENGTH_SHORT)
            .show()
    }

    private suspend fun saveImageToGallery(displayName: String, image: Bitmap) {

        checkForPermission()
        if (!writePermissionGranted)
            requestPermission()

        if (writePermissionGranted) {
            withContext(Dispatchers.IO) {
                val imageCollection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.WIDTH, image.width)
                    put(MediaStore.Images.Media.HEIGHT, image.height)
                }

                try {
                    contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                        contentResolver.openOutputStream(uri).use { outputStream ->
                            if (!image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream))
                                throw IOException("Couldn't save bitmap")
                        }
                    } ?: throw IOException("Couldn't create MediaStore entry")
                    Log.i("SAVING IMAGE", "Image is saved successfully!")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("SAVING IMAGE", "Failed to save image..")
                }
            }
        }
    }
}
