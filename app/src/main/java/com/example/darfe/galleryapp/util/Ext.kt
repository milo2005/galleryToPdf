package com.example.darfe.galleryapp.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun String.toUri(context: Context) : Uri = FileProvider.getUriForFile(
    context,
    context.applicationContext.packageName + ".provider",
    File(this)
)