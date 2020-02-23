package com.psplog.linememo.ui.addeditmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.CONTENT_TYPE
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.psplog.linememo.R
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage
import com.psplog.linememo.ui.dialog.AddPhotoDialog
import com.psplog.linememo.ui.dialog.SavingDialog
import com.psplog.linememo.utils.PhotoUtils
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.content_content.*
import java.io.File


class AddEditMemoActivity : AppCompatActivity(), AddEditMemoContract.View {
    override lateinit var presenter: AddEditMemoContract.Presenter
    private lateinit var imageTemp: File
    private var isContentEdited = false
    private var isEditingMode = true

    private var menuView: Menu? = null
    private var currentMemoId: Int = 0

    /**
     *  Activity 종료시 저장여부 확인 리스너
     */
    private var savingDialogListener: SavingDialog.SavingDialogListener =
        object : SavingDialog.SavingDialogListener {
            override fun onDialogSaveClick() {
                presenter.addMemo(getCurrentMemo())
                finish()
            }

            override fun onDialogExit() {
                finish()
            }
        }

    /**
     *  사진추가 Dialog (카메라, 갤러리, 링크) 클릭 리스너
     */
    private var addPhotoDialogListener: AddPhotoDialog.AddPhotoDialogListener =
        object : AddPhotoDialog.AddPhotoDialogListener {
            override fun onDialogCameraClick() {
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

            override fun onDialogGalleryClick() {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = CONTENT_TYPE
                startActivityForResult(intent, SELECT_GALLERY)
            }

            override fun onDialogLinkClick(link: String) {
                if (link.isNotBlank()) {
                    if (PhotoUtils.isHttpString(link)) {
                        presenter.addMemoImageInQueue("$link")
                    } else {
                        presenter.addMemoImageInQueue("http://$link")
                    }
                    isContentEdited = true
                    PhotoUtils.addPhotoView(window.decorView, link, deleteImageListener)
                }
            }
        }

    private var deleteImageListener = object : PhotoUtils.Companion.DeletableImageView.
    OnDeletableImageClick {
        override fun onDeletableImageClick(fileName: String) {
            presenter.deleteMemoImageInQueue(fileName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        initView()

        currentMemoId = intent.getIntExtra("memo_id", DEFAULT_MEMO_ID)
        presenter = AddEditMemoPresenter(this, currentMemoId, this)
        presenter.start()

        if (!isNewMemo()) {
            setViewEditable(false)
            setCurrentEditingState(false)
        }
    }

    private fun isNewMemo() = currentMemoId == DEFAULT_MEMO_ID

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        val textChangeListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isContentEdited = true
            }
        }

        et_content_title.addTextChangedListener(textChangeListener)
        et_content_content.addTextChangedListener(textChangeListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when {
            requestCode == SELECT_CAMERA -> {
                PhotoUtils.addPhotoView(window.decorView, imageTemp, deleteImageListener)
                val fileName = imageTemp.toString().split("/").last()
                presenter.addMemoImageInQueue(fileName)
                isContentEdited = true
            }

            requestCode == SELECT_GALLERY && null != data -> {
                val uri = data.data ?: return
                val fileName = PhotoUtils.createUUID() + ".png"
                imageTemp = File(filesDir, fileName)
                PhotoUtils.copyImageUriToFile(applicationContext, uri, imageTemp)
                PhotoUtils.addPhotoView(window.decorView, imageTemp, deleteImageListener)
                presenter.addMemoImageInQueue(fileName)
                isContentEdited = true
            }
        }
    }

    override fun onBackPressed() {
        if (isContentEdited) {
            showSavingDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.addeditmemo_fragment_menu, menu)
        menuView = menu
        changeEditingState(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG_ADD_EDIT, "${item.itemId}  ${R.id.home} ${android.R.id.home}")
        when (item.itemId) {
            R.id.menu_addedit_save -> {
                if (isContentEdited) {
                    presenter.addMemo(getCurrentMemo())
                    setCurrentEditingState(false)
                }
            }

            R.id.menu_addedit_delete -> {
                presenter.deleteMemo(Memo(memoId = currentMemoId))
                finish()
            }

            R.id.menu_addedit_edit ->
                isEditingMode = true

            R.id.menu_addedit_add_photo -> {
                if (checkPermission()) {
                    showAddPhotoDialog()
                    setCurrentEditingState(true)
                }
            }

            android.R.id.home -> {
                onBackPressed()
            }
        }
        changeEditingState(menuView)
        return true
    }

    private fun setCurrentEditingState(isEditing: Boolean) {
        isEditingMode = isEditing
        isContentEdited = isEditing
    }

    private fun changeEditingState(menu: Menu?) {
        setViewEditable(isEditingMode)
        if (isEditingMode) {
            menu?.getItem(0)?.isVisible = true
            menu?.getItem(1)?.isVisible = false
            PhotoUtils.setVisibilityDeleteButton(View.VISIBLE)
        } else {
            menu?.getItem(0)?.isVisible = false
            menu?.getItem(1)?.isVisible = true
            PhotoUtils.setVisibilityDeleteButton(View.GONE)
        }
    }

    private fun setViewEditable(isEnabled: Boolean) {
        et_content_title.isEnabled = isEnabled
        et_content_content.isEnabled = isEnabled
    }

    private fun getCurrentMemo(): Memo =
        Memo(
            et_content_title.text.toString(),
            et_content_content.text.toString(),
            "",
            currentMemoId
        )

    override fun showMemoContent(memoContent: Memo) {
        et_content_title.setText(memoContent.memoTitle)
        et_content_content.setText(memoContent.memoContent)
        isContentEdited = false
    }

    override fun showMemoContentImage(memoContentImageList: List<MemoImage>) {
        for (item in memoContentImageList) {
            if (PhotoUtils.isHttpString(item.memoUri)) {
                PhotoUtils.addPhotoView(window.decorView, item.memoUri, deleteImageListener)
            } else {
                imageTemp = File(filesDir, item.memoUri)
                PhotoUtils.addPhotoView(window.decorView, imageTemp, deleteImageListener)
            }
        }
    }

    override fun showAddPhotoDialog() {
        val dialog = AddPhotoDialog()
        dialog.setOnClickListener(addPhotoDialogListener)
        dialog.show(supportFragmentManager, "AddPhotoDialog")
    }

    private fun showSavingDialog() {
        val dialog = SavingDialog()
        dialog.setOnClickListener(savingDialogListener)
        dialog.show(supportFragmentManager, "SavingDialog")
    }

    private fun showSettingActivity() {
        val appDetail = Intent(
            ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        )
        appDetail.addCategory(Intent.CATEGORY_DEFAULT)
        appDetail.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(appDetail)
    }

    private var firstPermissionRequest = true
    private fun checkPermission(): Boolean {
        if (hasReadStoragePermission()) {
            return true
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            requestPermission()
        } else {
            if (firstPermissionRequest)
                requestPermission()
            else {
                showSettingActivity()
            }
        }
        firstPermissionRequest = false
        return false
    }

    private fun hasReadStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showAddPhotoDialog()
                } else {
                    toastMessage(R.string.permission_denied_msg)
                }
                return
            }
        }
    }

    private fun toastMessage(resId: Int) {
        Toast.makeText(
            applicationContext,
            resId,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val TAG_ADD_EDIT = "ADD_EDIT_ACT"
        private const val SELECT_CAMERA = 0
        private const val SELECT_GALLERY = 1
        private const val REQUEST_READ_STORAGE = 2
        private const val DEFAULT_MEMO_ID = 0
    }
}
