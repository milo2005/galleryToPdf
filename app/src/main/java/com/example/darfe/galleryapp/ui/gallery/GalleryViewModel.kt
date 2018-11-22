package com.example.darfe.galleryapp.ui.gallery

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import com.example.darfe.galleryapp.ui.gallery.adapter.PhotoItem
import com.example.darfe.galleryapp.util.applySchedulers
import io.reactivex.Single
import java.io.File


class GalleryViewModel : ViewModel() {

    fun removeFile(path: String): Single<Boolean> = Single.fromCallable {
        File(path).delete()
    }.applySchedulers()

    fun removeFiles(items: MutableList<PhotoItem>) {
        items.forEach { File(it.path).delete() }
    }

    fun switchFile(item: PhotoItem, uri: Uri) {
        File(item.path).delete()
        item.uri = uri
        item.path = item.uri.toFile().absolutePath
    }

}