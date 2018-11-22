package com.example.darfe.galleryapp.ui.gallery


import android.graphics.drawable.Animatable
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.darfe.galleryapp.R
import com.example.darfe.galleryapp.ui.gallery.adapter.PhotoItem
import com.squareup.picasso.Callback
import kotlinx.android.parcel.Parcelize
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Environment
import android.util.Log
import com.squareup.picasso.Picasso
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_pdf_creator.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.concurrent.thread
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.image.ImageInfo


class PdfCreatorFragment : DialogFragment(), Callback {

    val data: List<PhotoItem> by lazy { arguments?.getParcelable<PhotoList>(ARG_ITEMS)?.items ?: emptyList() }
    var index = 0
    lateinit var pdf: PdfDocument
    lateinit var file:File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pdf_creator, container, false)
    }

    override fun onResume() {
        super.onResume()
        pdf = PdfDocument()
        preparePage()
    }

    private fun preparePage() {
        Picasso.get()
            .load(File(data[index].path))
            .resize(800,800)
            .centerInside()
            .into(pdfImg, this)

    }

    private fun createPage() {
        val pageInfo = PageInfo.Builder(595, 842, index).create()
        val page = pdf.startPage(pageInfo)
        pdfImg.draw(page.canvas)
        pdf.finishPage(page)

        if(index + 1 == data.size) finishPDF()
        else {
            index += 1
            preparePage()
        }
    }

    private fun finishPDF() {
        file = File("${Environment.getExternalStorageDirectory().absolutePath}/${Date().time}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()
        pdfCreated.onNext(file)
        dismiss()
    }

    override fun onSuccess() {
        thread {
            Thread.sleep(700)
            activity!!.runOnUiThread {
                createPage()
            }
        }

    }

    override fun onError(e: Exception?) {}

    companion object {

        private const val ARG_ITEMS = "items"
        val pdfCreated:PublishSubject<File> = PublishSubject.create()

        fun instance(items: List<PhotoItem>): PdfCreatorFragment = PdfCreatorFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ITEMS, PhotoList(items))
            }
        }
    }

}

@Parcelize
class PhotoList(val items: List<PhotoItem>) : Parcelable