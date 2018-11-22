package com.example.darfe.galleryapp.ui.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.darfe.galleryapp.R
import com.example.darfe.galleryapp.ui.gallery.adapter.PhotoAdapter
import com.example.darfe.galleryapp.ui.gallery.adapter.PhotoItem
import com.example.darfe.galleryapp.util.LifeDisposable
import com.example.darfe.galleryapp.util.PhotoUtil
import com.example.darfe.galleryapp.util.toUri
import com.jakewharton.rxbinding3.view.clicks
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class GalleryActivity : AppCompatActivity() {

    private val dis: LifeDisposable = LifeDisposable(this)
    private val adapter: PhotoAdapter = PhotoAdapter()
    private val vm: GalleryViewModel by viewModel()

    var selected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gallery)
        intent.extras?.getString(EXTRA_PATH)?.run {
            adapter.add(
                PhotoItem(
                    this,
                    this.toUri(this@GalleryActivity)
                )
            )
        }
        list.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //region Finish Gallery
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_gallery, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                vm.removeFiles(adapter.data)
                finish()
            }

            R.id.action_ok ->  makeDocument()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        vm.removeFiles(adapter.data)
        super.onBackPressed()

    }
    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                vm.switchFile(adapter.data[selected], result.uri)
                adapter.notifyDataSetChanged()
            }
        } else {
            PhotoUtil.getPath(resultCode)?.run {
                adapter.add(PhotoItem(this, this.toUri(this@GalleryActivity)))
            }
        }

    }


    override fun onResume() {
        super.onResume()

        dis add btnAdd.clicks()
            .flatMap { PhotoUtil.captureImage(this) }
            .subscribe()

        dis add adapter.onRemovePhoto()
            .flatMapSingle { vm.removeFile(it.path) }
            .subscribe()

        dis add adapter.onClickPhoto()
            .subscribe { (pos, item) ->
                selected = pos
                CropImage.activity(item.uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowFlipping(false)
                    .setInitialCropWindowPaddingRatio(0.0f)
                    .setCropMenuCropButtonTitle("Aceptar")
                    .start(this)
            }

        dis add PdfCreatorFragment.pdfCreated
            .subscribe {
                vm.removeFiles(adapter.data)
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(DATA_TYPE, TYPE_PDF)
                    putExtra(DATA_PATH, it.absolutePath)
                })
                finish()
            }
    }

    fun makeDocument(){
        if(adapter.data.size == 1){
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(DATA_TYPE, TYPE_IMG)
                putExtra(DATA_PATH, adapter.data[0].path)
            })
            finish()
        }else{
            val ft = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            PdfCreatorFragment.instance(adapter.data)
                .show(ft, "dialog")
        }
    }

    companion object {
        const val EXTRA_PATH = "path"
        private const val DATA_PATH = "path"
        private const val DATA_TYPE = "type"

        const val TYPE_PDF = 0
        const val TYPE_IMG = 1

        fun processData(intent:Intent):Pair<Int, File>{
            val extras = intent.extras!!
            val path = extras.getString(DATA_PATH)
            val type = extras.getInt(DATA_TYPE)
            return type to File(path)
        }
    }

}
