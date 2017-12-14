package com.ab.texteditor.base

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.hannesdorfmann.mosby.mvp.lce.MvpLceActivity
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView
import java.util.*

/**
 * Created by: anirban on 18/11/17.
 */
abstract class BaseActivity<CV : View, M,
        V : MvpLceView<M>, P : MvpPresenter<V>> : MvpLceActivity<CV, M, V, P>(),
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionListener {

    companion object {
        const val WRITE_EXTERNAL_STORAGE = 100
    }


    private var permissionListener: PermissionListener? = null
    private var isPermissionDialogShowing: Boolean = false


    fun UserPermission(permissionCode: Int, permissionListener: PermissionListener) {

        this.permissionListener = permissionListener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCode == WRITE_EXTERNAL_STORAGE) {
                app_CheckAllCriticalPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            /*
              If pre Marshmallow Just go ahead without permissions
             */
            permissionListener.getPermissionResult(true)
        }
    }

    private fun app_CheckAllCriticalPermission(perm: String) {

        val res = ContextCompat.checkSelfPermission(this, perm)
        if (res != PermissionChecker.PERMISSION_GRANTED) {
            requestPermission(perm)
        } else {
            permissionListener?.getPermissionResult(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserPermission(WRITE_EXTERNAL_STORAGE, this)

    }

    private fun requestPermission(perm: String) {
        if (!isPermissionDialogShowing) {
            isPermissionDialogShowing = true

            val permission = ArrayList<String>()
            permission.add(perm)
            val permissions = arrayOfNulls<String>(permission.size)
            for (j in permission.indices) {
                permissions[j] = permission[j]
            }
            ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var perm = ""
        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
        postCheckPermission(perm)
    }

    override fun getPermissionResult(value: Boolean) {
        Log.e("TAG","Permission error")
    }

    private fun postCheckPermission(perm: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val res = ContextCompat.checkSelfPermission(this, perm)
            if (res != PermissionChecker.PERMISSION_GRANTED) {
                permissionListener?.getPermissionResult(false)
                isPermissionDialogShowing = false
                val message = if (perm == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
//                    this.getString(R.string.permission_missing)
                    "Permission missing"
                } else {
                    "This permission is missing"
//                    this.getString(R.string.phone_state_camera_permission_missing)
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                finish()

            } else {
                permissionListener?.getPermissionResult(true)
            }
        }
    }

}