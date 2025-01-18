package com.photosi.assignment.section.upload

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val UploadImagesScreenModule = module {
    viewModelOf(::UploadImagesViewModel)
}