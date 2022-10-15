package com.hamalawey.autoadapter_annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AdapterModel(val layoutId: Int)