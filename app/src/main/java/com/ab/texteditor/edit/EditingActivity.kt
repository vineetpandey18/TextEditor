package com.ab.texteditor.edit

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.ab.texteditor.R
import com.ab.texteditor.RxNetworkHelper
import com.ab.texteditor.base.BaseActivity
import com.ab.texteditor.model.ModelBase
import com.ab.texteditor.model.TYPE_IMAGE_MODEL
import com.ab.texteditor.preview.PreviewActivity
import kotlinx.android.synthetic.main.activity_main.*

class EditingActivity : BaseActivity<RecyclerView, ArrayList<ModelBase>, Editing.View, Editing.Presenter>(), Editing.View {

    private val adapter = MainAdapter(arrayListOf(), this)

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean) = e?.message ?: ""

    override fun createPresenter() = EditingPresenter()

    override fun loadData(pullToRefresh: Boolean) {

//        presenter.initialSetup(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.preview, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.preview -> startActivity(Intent(this, PreviewActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setData(data: ArrayList<ModelBase>) {
        adapter.add(data, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentView.apply {
            adapter = this@EditingActivity.adapter
            layoutManager = LinearLayoutManager(this@EditingActivity)
        }

        addImg.setOnClickListener {
            Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                startActivityForResult(this, 101)
            }
        }
        loadData(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            adapter.add(ArrayList<ModelBase>().apply {
                add(
                        ModelBase().apply {
                            type = TYPE_IMAGE_MODEL
                            imageUrl = data?.data?.let { getPath(this@EditingActivity, it) }
                            imageUrl?.let { RxNetworkHelper.uploadFile(application, it) }
                        })

            }, true)

        }
    }

    override fun getPermissionResult(value: Boolean) {
        super.getPermissionResult(value)
        presenter.initialSetup(this)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String? {


        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            //
            /*if (isLocalStorageDocument(uri)) {
                // The path is the id

            }
            // ExternalStorageProvider
            else*/
            /*if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory()).toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                return getDataColumn(context, contentUri, null, null)
            } else*/
//            if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(context, contentUri, selection, selectionArgs)
        }// MediaProvider
        // DownloadsProvider
        /*}else if ("content".equals(uri.scheme, ignoreCase = true)) {

               // Return the remote address
               return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

           }*/ /*else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }*/// File
        // MediaStore (and general)

        return DocumentsContract.getDocumentId(uri)
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {

                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }


}
