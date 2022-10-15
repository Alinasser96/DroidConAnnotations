package com.hamalawey.autoadapter_annotations

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewHolderBinding(val viewId: Int)
