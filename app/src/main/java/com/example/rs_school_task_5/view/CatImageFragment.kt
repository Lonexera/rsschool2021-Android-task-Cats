package com.example.rs_school_task_5.view

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.rs_school_task_5.FragmentListener
import com.example.rs_school_task_5.PermissionManager
import com.example.rs_school_task_5.R
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.databinding.CatImageFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CatImageFragment private constructor() : Fragment() {

    private var _binding: CatImageFragmentBinding? = null
    private val binding get() = _binding!!

    private var writePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            requireArguments().putString(CAT_ID, savedInstanceState.getString(CAT_ID))
            requireArguments().putString(IMAGE_URL, savedInstanceState.getString(IMAGE_URL))
            requireArguments().putInt(IMAGE_WIDTH, savedInstanceState.getInt(IMAGE_WIDTH))
            requireArguments().putInt(IMAGE_HEIGHT, savedInstanceState.getInt(IMAGE_HEIGHT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CatImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = activity as FragmentListener

        with(binding) {
            val catIdStr = arguments?.getString(CAT_ID) ?: return

            val imageWidth = arguments?.getInt(IMAGE_WIDTH) ?: return
            val imageHeight = arguments?.getInt(IMAGE_HEIGHT) ?: return
            imageResolution.text = getString(R.string.image_resolution_text, imageWidth, imageHeight)

            val imageUrl = arguments?.getString(IMAGE_URL) ?: return
            Glide.with(binding.root).load(imageUrl).into(catImage)

            imageToolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_save -> {
                        lifecycleScope.launch {
                            saveImageToGallery(catIdStr, createBitmap())
                        }
                        true
                    }
                    else -> true
                }
            }
            imageToolbar.setNavigationOnClickListener {
                listener.onBack()
            }

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    listener.onBack()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(CAT_ID, requireArguments().getString(CAT_ID))
        outState.putString(IMAGE_URL, requireArguments().getString(IMAGE_URL))
        outState.putInt(IMAGE_WIDTH, requireArguments().getInt(IMAGE_WIDTH))
        outState.putInt(IMAGE_HEIGHT, requireArguments().getInt(IMAGE_HEIGHT))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun createBitmap(): Bitmap {
        val bitmapDrawable = binding.catImage.drawable as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    private suspend fun saveImageToGallery(displayName: String, image: Bitmap) {
        val contentResolver = activity?.contentResolver ?: return

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
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Image is saved!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("SAVING IMAGE", "Failed to save image..")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to save..", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
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
            requireActivity(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    companion object {
        private const val CAT_ID = "CAT ID"
        private const val IMAGE_URL = "IMAGE URL"
        private const val IMAGE_WIDTH = "IMAGE WIDTH"
        private const val IMAGE_HEIGHT = "IMAGE HEIGHT"

        @JvmStatic
        fun newInstance(cat: Cat): CatImageFragment {
            val fragment = CatImageFragment()
            val args = bundleOf()
            args.putString(CAT_ID, cat.id)
            args.putString(IMAGE_URL, cat.imageUrl)
            args.putInt(IMAGE_WIDTH, cat.imageWidth)
            args.putInt(IMAGE_HEIGHT, cat.imageHeight)
            fragment.arguments = args

            return fragment
        }
    }
}
