package com.example.darfe.galleryapp.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.darfe.galleryapp.R
import com.example.darfe.galleryapp.ui.gallery.GalleryActivity
import com.example.darfe.galleryapp.util.LifeDisposable
import com.example.darfe.galleryapp.util.PhotoUtil
import com.jakewharton.rxbinding3.view.clicks
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.File

class MainActivity : AppCompatActivity() {

    private val dis:LifeDisposable = LifeDisposable(this)
    private val requestFile = 201

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        dis add btnTake.clicks()
            .flatMap { PhotoUtil.captureImage(this) }
            .subscribe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if(requestCode == requestFile){
            if(resultCode == Activity.RESULT_OK){
                val (type, file) = GalleryActivity.processData(result!!)
                proccessFile(type, file)
            }
        }else{
            PhotoUtil.getPath(resultCode)?.run {
                startActivityForResult<GalleryActivity>(requestFile, GalleryActivity.EXTRA_PATH to this)
            }
        }

    }

    fun proccessFile(type:Int, file: File){
        val name = file.name
        val size = file.length()
        val isPdf = type == GalleryActivity.TYPE_PDF

        // Aqui ya tienes los datos para ponerlos en la lista
    }
}
