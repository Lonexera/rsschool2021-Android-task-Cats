package com.example.rs_school_task_5

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rs_school_task_5.data.Cat
import com.example.rs_school_task_5.databinding.ActivityMainBinding
import com.example.rs_school_task_5.managers.PermissionManager
import com.example.rs_school_task_5.view.CatImageFragment
import com.example.rs_school_task_5.view.CatListFragment
import com.example.rs_school_task_5.view.FragmentListener

class MainActivity : AppCompatActivity(), FragmentListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openListFragment()
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
            } else
                showPermissionDeniedToast()
        }
    }

    private fun showPermissionDeniedToast() {
        Toast.makeText(this, "Permission to write was denied!", Toast.LENGTH_SHORT)
            .show()
    }

    override fun openListFragment() {
        val listFragment = CatListFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.fragmentContainer.id, listFragment)
        transaction.addToBackStack("list")
        transaction.commit()
    }

    override fun openImageFragment(cat: Cat) {
        val imageFragment = CatImageFragment.newInstance(cat)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.animator.flip_right_enter,
            R.animator.flip_right_exit,
            R.animator.flip_left_enter,
            R.animator.flip_left_exit
        )
        transaction.replace(binding.fragmentContainer.id, imageFragment)
        transaction.addToBackStack("image")
        transaction.commit()
    }

    override fun onBack() {
        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else finish()
    }
}
