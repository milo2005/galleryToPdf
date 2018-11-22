package com.example.darfe.galleryapp.ui.gallery.adapter

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PhotoItem(var path:String, var uri:Uri) : Parcelable