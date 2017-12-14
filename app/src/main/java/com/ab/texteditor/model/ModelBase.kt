package com.ab.texteditor.model

import com.vicpin.krealmextensions.queryAll
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by: anirban on 18/11/17.
 */

const val TYPE_TEXT_MODEL = 1
const val TYPE_IMAGE_MODEL = 2

@RealmClass
open class ModelBase : RealmObject(), Comparable<ModelBase> {
    override fun compareTo(other: ModelBase): Int {
        return if (this.position ?: 0 > other.position ?: 0) 1 else if (this.position ?: 0 < other.position ?: 0) -1 else 0
    }

    open var text: String? = null
    @PrimaryKey
    open var position: Int? = null
    open var imageUrl: String? = null
        set(value) {
            field = value
            fileName = field?.substring(field?.lastIndexOf('/')?.plus(1) ?: 0)
        }
    open var isUploaded: Boolean = false
    open var fileName: String? = null
    open var type: Int = TYPE_TEXT_MODEL

    companion object {
         fun hasData() = ModelBase().queryAll().isNotEmpty()
    }
}