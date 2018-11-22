package com.example.darfe.galleryapp.di

import com.example.darfe.galleryapp.ui.gallery.GalleryViewModel
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

val appModule = module {
    viewModel<GalleryViewModel>()
}