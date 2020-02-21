package com.psplog.linememo.ui.addeditmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.psplog.linememo.R
import com.psplog.linememo.database.local.Memo
import com.psplog.linememo.database.local.MemoImage
import com.psplog.linememo.ui.dialog.AddPhotoDialog
import com.psplog.linememo.ui.dialog.SavingDialog
import com.psplog.linememo.utils.PhotoUtils
import kotlinx.android.synthetic.main.activity_content.*
import kotlinx.android.synthetic.main.content_content.*
import java.io.File

class AddEditMemoActivity : AddEditMemoView(), AddEditContract.View {

    override lateinit var presenter: AddEditContract.Presenter
    private lateinit var imageTemp: File
    private var isContentEdited = false
    private var isEditingMode = false

    private var menuView: Menu? = null
    private var currentMemoId: Int = 0

    //TODO: 이미지삭제
    //TODO: loadMemo 여러번호출되는이유를 알고싶다
    //
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

            override fun onDialogLinkClick(dialog: DialogFragment, link: String) {
                PhotoUtils.addPhotoView(window.decorView, link)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        initView()

        Log.d(TAG_ADD_EDIT, "oncreate")
        currentMemoId = intent.getIntExtra("memo_id", 0)

        if (currentMemoId == 0) {   // New Memo Mode
            setViewEditable(true)
        }else{                      // Memo Edit Or Viewing
            setViewEditable(false)
            isContentEdited
        }
        presenter = AddEditPresenter(this, currentMemoId, this)
        presenter.start()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        iv_content_add_photo.setOnClickListener {
            if (checkPermission()) {
                showAddPhotoDialog()
            }
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
                Log.d(TAG_ADD_EDIT, "카메라")
                PhotoUtils.addPhotoView(window.decorView, imageTemp)
            }
            requestCode == SELECT_GALLERY && null != data -> {
                val uri = data.data ?: return
                Log.d(TAG_ADD_EDIT, "갤러리$uri")

                val fileName = PhotoUtils.createUUID() + ".png"
                imageTemp = File(filesDir, fileName)
                PhotoUtils.copyImageUriToFile(applicationContext, uri, imageTemp)
                PhotoUtils.addPhotoView(window.decorView, imageTemp)
                presenter.addPhoto(fileName)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.addeditmemo_fragment_menu, menu)
        changeEditingState(menu)
        menuView = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_addedit_save -> {
                presenter.addMemo(getCurrentMemo())
                isEditingMode = false
                isContentEdited = false
            }

            R.id.menu_addedit_delete -> {
                presenter.deleteMemo(Memo(memoId = currentMemoId))
                finish()
            }

            R.id.menu_addedit_edit -> isEditingMode = true

        }
        changeEditingState(menuView)
        return true
    }

    private fun changeEditingState(menu: Menu?) {
        setViewEditable(isEditingMode)

        if (isEditingMode) {
            menu?.getItem(0)?.isVisible = true
            menu?.getItem(1)?.isVisible = false
        } else {
            menu?.getItem(0)?.isVisible = false
            menu?.getItem(1)?.isVisible = true
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
    }

    override fun showMemoContentImage(memoContentImageList: List<MemoImage>) {
        Log.d(TAG_ADD_EDIT, "showMemoContentImage")
        for (link in memoContentImageList) {
            imageTemp = File(filesDir, link.memoUri)
            PhotoUtils.addPhotoView(window.decorView, imageTemp)
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
                )
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
        permissions: Array<String>, grantResults: IntArray
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
                    )
                }
                return
            }
        }
    }

    companion object {
        const val TAG_ADD_EDIT = "add_edit"
        const val SELECT_CAMERA = 0
        const val SELECT_GALLERY = 1
        const val REQUEST_READ_STORAGE = 2
    }
}
