package com.example.darfe.galleryapp.util

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.toast
import java.io.File
import android.provider.OpenableColumns

object PdfUtil {
    val processedPdf: PublishSubject<Triple<String, String, Uri>> = PublishSubject.create()
    private lateinit var filePdf: File

    fun selectPdf(activity: AppCompatActivity): Observable<File> = RxPermissions(activity)
            .request(Manifest.permission.READ_EXTERNAL_STORAGE)
            .flatMap { granted ->
                Observable.create<File> {
                    if (granted) {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)

                        intent.type = "application/pdf"
                        activity.startActivityForResult(intent, 159)
                    } else {
                        activity.toast("Permisos denegados")
                    }
                }
            }

    fun processPdf(context: Context, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri? = data?.data
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    val displayName: String = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                    val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                    val size: String = if (!it.isNull(sizeIndex)) {
                        it.getString(sizeIndex)
                    } else {
                        "Unknown"
                    }

                    processedPdf.onNext(Triple(displayName, size, uri!!))
                }
            }

        }
    }



}