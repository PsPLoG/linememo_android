package com.psplog.linememo.ui.addeditmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.CONTENT_TYPE
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

    //TODO: url 첨부시구분
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
                PhotoUtils.addPhotoView(window.decorView, link, deleteImageListener)
            }
        }

    private var deleteImageListener = object : PhotoUtils.Companion.DeletableImageView.
    OnDeletableImageClick {
        override fun OnDeletableImageClick(fileName: String) {
            val deleteFile = File(filesDir, fileName)
            if (deleteFile.delete()) {
                presenter.deleteMemoImageInQueue(fileName)
            } else {
                Toast.makeText(applicationContext, "파일삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        initView()

        currentMemoId = intent.getIntExtra("memo_id", DEFAULT_MEMO_ID)
        presenter = AddEditMemoPresenter(this, currentMemoId, this)
        presenter.start()

        // Memo Edit Or Viewing
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
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

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
                Log.d(TAG_ADD_EDIT, "카메라$fileName")
                presenter.addMemoImageInQueue(fileName)
            }

            requestCode == SELECT_GALLERY && null != data -> {
                val uri = data.data ?: return
                val fileName = PhotoUtils.createUUID() + ".png"
                Log.d(TAG_ADD_EDIT, "갤러리$uri")
                imageTemp = File(filesDir, fileName)
                PhotoUtils.copyImageUriToFile(applicationContext, uri, imageTemp)
                PhotoUtils.addPhotoView(window.decorView, imageTemp, deleteImageListener)
                presenter.addMemoImageInQueue(fileName)
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
        Log.d(TAG_ADD_EDIT,"${item.itemId}  ${R.id.home}")
        when (item.itemId) {
            R.id.menu_addedit_save -> {
                presenter.addMemo(getCurrentMemo())
                setCurrentEditingState(false)
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

            R.id.home -> {
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
            "", currentMemoId
        )

    override fun showMemoContent(memoContent: Memo) {
        et_content_title.setText(memoContent.memoTitle)
        et_content_content.setText(memoContent.memoContent)
        isContentEdited = false
    }

    override fun showMemoContentImage(memoContentImageList: List<MemoImage>) {
        for (link in memoContentImageList) {
            imageTemp = File(filesDir, link.memoUri)
            PhotoUtils.addPhotoView(window.decorView, imageTemp, deleteImageListener)
        }
    }

    override fun showAddPhotoDialog() {
        var dialog = AddPhotoDialog()
        dialog.setOnClickListener(addPhotoDialogListener)
        dialog.show(supportFragmentManager, "AddPhotoDialog")
    }

    private fun showSavingDialog() {
        var dialog = SavingDialog()
        dialog.setOnClickListener(savingDialogListener)
        dialog.show(supportFragmentManager, "SavingDialog")
    }

    // TODO if 절 이름 바꿔주기
    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(
                    applicationContext,
                    R.string.permission_denied_msg,
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_STORAGE
                )
            }
        }
        return true
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
                    Toast.makeText(
                        applicationContext,
                        R.string.permission_denied_msg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    companion object {
        private const val TAG_ADD_EDIT = "ADD_EDIT_ACT"

        private const val SELECT_CAMERA = 0
        private const val SELECT_GALLERY = 1

        private const val REQUEST_READ_STORAGE = 2

        private const val DEFAULT_MEMO_ID = 0
    }
}
