package com.example.darfe.galleryapp.util

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.thread

object PhotoUtil {

    val processedImg: PublishSubject<File> = PublishSubject.create()
    private lateinit var fileImage: File

    fun captureImage(activity: AppCompatActivity): Observable<File> = RxPermissions(activity)
        .request(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .flatMap { granted ->
            Observable.create<File> {
                if (granted) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    fileImage = File(activity.filesDir, "temp${Date().time}.jpg")

                    val imageUri: Uri = FileProvider.getUriForFile(
                        activity,
                        activity.applicationContext.packageName + ".provider",
                        fileImage
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    activity.startActivityForResult(intent, 135)

                    it.onNext(fileImage)
                } else {
                    activity.toast("Permisos denegados")
                }
                it.onComplete()
            }
        }

    fun getPath(resultCode: Int): String? = if (resultCode == AppCompatActivity.RESULT_OK) {
        fileImage.absolutePath
    } else null

    fun processBitmap(context: Context, path:String, width: Int, height: Int){
        thread {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(fileImage.absolutePath, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            val scaleFactor = Math.min(photoW / width, photoH / height)

            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true

            val bitmap = BitmapFactory.decodeFile(fileImage.absolutePath, bmOptions)
            fileImage.delete()

            val file = File(context.filesDir, "${Date().time}.jpg")
            val outStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
            outStream.flush()
            outStream.close()
            context.runOnUiThread {
                processedImg.onNext(file)
            }

        }
    }

    fun processImage(context: Context, resultCode: Int, width: Int, height: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            thread {
                val bmOptions = BitmapFactory.Options()
                bmOptions.inJustDecodeBounds = true
                BitmapFactory.decodeFile(fileImage.absolutePath, bmOptions)
                val photoW = bmOptions.outWidth
                val photoH = bmOptions.outHeight

                val scaleFactor = Math.min(photoW / width, photoH / height)

                bmOptions.inJustDecodeBounds = false
                bmOptions.inSampleSize = scaleFactor
                bmOptions.inPurgeable = true

                val bitmap = BitmapFactory.decodeFile(fileImage.absolutePath, bmOptions)
                fileImage.delete()

                val file = File(context.filesDir, "${Date().time}.jpg")
                val outStream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
                outStream.flush()
                outStream.close()
                context.runOnUiThread {
                    processedImg.onNext(file)
                }

            }
        }
    }


}