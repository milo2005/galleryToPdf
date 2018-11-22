package com.example.darfe.galleryapp.ui.gallery.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.darfe.galleryapp.R
import com.example.darfe.galleryapp.databinding.TemplatePhotoBinding
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    val data: MutableList<PhotoItem> = mutableListOf()
    private val onRemove: PublishSubject<Int> = PublishSubject.create()
    private val onClick: PublishSubject<Int> = PublishSubject.create()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.template_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(data[position], onClick, onRemove, position)
    }

    fun onRemovePhoto(): Observable<PhotoItem> = onRemove
        .map { data.removeAt(it) }
        .doOnNext { notifyDataSetChanged() }

    fun onClickPhoto():Observable<Pair<Int,PhotoItem>> = onClick
        .map { it to data[it] }

    fun add(item: PhotoItem): Int {
        data.add(item)
        notifyDataSetChanged()
        return data.size
    }

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: TemplatePhotoBinding = TemplatePhotoBinding.bind(view)
        fun bind(photo: PhotoItem, click: PublishSubject<Int>, remove: PublishSubject<Int>, pos: Int) = binding.run {
            onRemove = remove
            item = photo
            position = pos
            onClick = click
            img.setImageURI(photo.uri, null)

        }
    }

}