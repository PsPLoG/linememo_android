package com.psplog.linememo.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.psplog.linememo.R
import com.psplog.linememo.ui.dialog.AddPhotoDialog
import com.psplog.linememo.utils.PhotoUtils
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.content_content.*
import java.io.File

class ContentActivity : AppCompatActivity(), AddPhotoDialog.AddPhotoDialogListener {
    val SELECT_CAMERA = 0
    val SELECT_GALLERY = 1
    val REQUEST_READ_STORAGE = 2
    private lateinit var imageTemp: File

    override fun onBackPressed() {
        super.onBackPressed()

    }

    //TODO : 이미지 삭제가능하게
    private fun showAddPhotoDialog() {
        var dialog = AddPhotoDialog()
        dialog.show(supportFragmentManager, "AddPhotoDialog")
    }

    override fun onDialogCameraClick(dialog: DialogFragment) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageTemp = File(filesDir, PhotoUtils.createUUID() + ".png")
        val uri = FileProvider.getUriForFile(
            applicationContext,
            "com.psplog.linememo.fileprovider",
            imageTemp
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, SELECT_CAMERA)
    }

    override fun onDialogGalleryClick(dialog: DialogFragment) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, SELECT_GALLERY)
    }

    override fun onDialogLinkClick(dialog: DialogFragment, uri: String) {
        PhotoUtils.addPhotoView(window.decorView, uri)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        initView()

        iv_content_add_photo.setOnClickListener {
            if(checkPermission()){
                showAddPhotoDialog()
            }
        }
    }

    private fun checkPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                Toast.makeText(applicationContext,R.string.permission_denied_msg,Toast.LENGTH_SHORT)
                return false
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE
                )
            }

        } else {
            return true
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == SELECT_CAMERA -> {
                Log.d("CONTENT", "카메라")
                PhotoUtils.addPhotoView(window.decorView, imageTemp)
            }
            requestCode == SELECT_GALLERY && null != data -> {
                val uri = data.data
                Log.d("CONTENT", "갤러리$uri")
                if (data.data != null) {

                    imageTemp = File(filesDir, PhotoUtils.createUUID() + ".png")
                    PhotoUtils.copyImageFile(applicationContext, uri, imageTemp)
                    PhotoUtils.addPhotoView(window.decorView, imageTemp)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showAddPhotoDialog()
                } else {
                    Toast.makeText(applicationContext,R.string.permission_denied_msg,Toast.LENGTH_SHORT)
                }
                return
            }
        }
    }

}
