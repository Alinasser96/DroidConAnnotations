package com.hamalawey.autoadapter_processor

import com.hamalawey.autoadapter_annotations.AdapterModel
import com.hamalawey.autoadapter_annotations.ViewHolderBinding
import com.hamalawey.autoadapter_processor.codegen.AdapterCodeBuilder
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
class Processor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes() =
        mutableSetOf(AdapterModel::class.java.canonicalName) // 3

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        val kaptKotlinGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
                ?: return false
        roundEnv.getElementsAnnotatedWith(AdapterModel::class.java) // 1
            .forEach {
                val modelData = getModelData(it)
                val fileName = "${modelData.modelName}Adapter"
                FileSpec.builder(modelData.packageName, fileName)
                    .addType(AdapterCodeBuilder(fileName, modelData).build())
                    .build()
                    .writeTo(File(kaptKotlinGeneratedDir))
            }
        return true
    }

    private fun getModelData(elem: Element): ModelData {
        val packageName = processingEnv.elementUtils.getPackageOf(elem).toString() // 1
        val modelName = elem.simpleName.toString() // 2
        val annotation = elem.getAnnotation(AdapterModel::class.java) // 3
        val layoutId = annotation.layoutId // 4
        val viewHolderBindingData = elem.enclosedElements.mapNotNull { // 5
            val viewHolderBinding = it.getAnnotation(ViewHolderBinding::class.java) // 6
            if (viewHolderBinding == null) {
                null // 7
            } else {
                val elementName = it.simpleName.toString()
                val fieldName = elementName.substring(3, elementName.indexOf('$'))
                    .decapitalize(Locale.getDefault())
                ViewHolderBindingData(fieldName, viewHolderBinding.viewId) // 8
            }
        }
        return ModelData(packageName, modelName, layoutId, viewHolderBindingData) // 9
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

}