package com.hamalawey.autoadapter_processor.codegen

import com.hamalawey.autoadapter_processor.ModelData
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class AdapterCodeBuilder(
    private val adapterName: String,
    private val data: ModelData
) {
    private val viewHolderName = "ViewHolder"
    private val viewHolderClassName = ClassName(data.packageName, viewHolderName)
    private val viewHolderQualifiedClassName = ClassName(data.packageName,
        adapterName + ".$viewHolderName")
    private val modelClassName = ClassName(data.packageName, data.modelName)
    private val itemsListClassName = ClassName("kotlin.collections", "List")
        .parameterizedBy(modelClassName)
    private val textViewClassName = ClassName("android.widget", "TextView")

    fun build(): TypeSpec = TypeSpec.classBuilder(adapterName) // 1
        .primaryConstructor(
            FunSpec.constructorBuilder() // 2
            .addParameter("items", itemsListClassName)
            .build()
        )
        // 3
        .superclass(ClassName("androidx.recyclerview.widget.RecyclerView", "Adapter")
            .parameterizedBy(viewHolderQualifiedClassName)
        )
        .addProperty(
            PropertySpec.builder("items", itemsListClassName) // 4
            .addModifiers(KModifier.PRIVATE)
            .initializer("items")
            .build()
        )
        .addBaseMethods()
        .addViewHolderType()
        .build()

    private fun TypeSpec.Builder.addBaseMethods(): TypeSpec.Builder = apply { // 1
        addFunction(FunSpec.builder("getItemCount") // 2
            .addModifiers(KModifier.OVERRIDE) // 3
            .returns(INT) // 4
            .addStatement("return items.size") // 5
            .build()
        )
        addFunction(FunSpec.builder("onCreateViewHolder")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("parent", ClassName("android.view", "ViewGroup")) // 1
            .addParameter("viewType", INT)
            .returns(viewHolderQualifiedClassName)
            .addStatement("val view = " +
                    "android.view.LayoutInflater.from(parent.context).inflate(%L, " +
                    "parent, false)", data.layoutId) // 2
            .addStatement("return $viewHolderName(view)")
            .build()
        )

        addFunction(FunSpec.builder("onBindViewHolder")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("viewHolder", viewHolderQualifiedClassName)
            .addParameter("position", INT)
            .addStatement("viewHolder.bind(items[position])")
            .build()
        )
    }
    private fun TypeSpec.Builder.addViewHolderType(): TypeSpec.Builder = addType(
        TypeSpec.classBuilder(viewHolderClassName)
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter("itemView", ClassName("android.view", "View"))
                .build()
            )
            .superclass(ClassName(
                "androidx.recyclerview.widget.RecyclerView",
                "ViewHolder")
            )
            .addSuperclassConstructorParameter("itemView")
            .addBindingMethod()
            .build()
    )

    private fun TypeSpec.Builder.addBindingMethod(): TypeSpec.Builder = addFunction(
        FunSpec.builder("bind") // 1
            .addParameter("item", modelClassName)
            .apply {
                data.viewHolderBindingData.forEach { // 2
                    addStatement("itemView.findViewById<%T>(%L).text = item.%L",
                        textViewClassName, it.viewId, it.fieldName) // 3
                }
            }
            .build()
    )
}