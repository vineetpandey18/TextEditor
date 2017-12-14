package com.ab.texteditor.edit

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ab.texteditor.R
import com.ab.texteditor.RxNetworkHelper
import com.ab.texteditor.constants.S3_LINK
import com.ab.texteditor.model.ModelBase
import com.ab.texteditor.model.TYPE_IMAGE_MODEL
import com.ab.texteditor.model.TYPE_TEXT_MODEL
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_text.view.*
import java.io.File

/**
 * Created by: anirban on 16/11/17.
 */

class MainAdapter(val items: ArrayList<ModelBase>, val context: Activity) : RecyclerView.Adapter<MainAdapter.BaseViewHolder>() {


    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int) {
        holder?.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_IMAGE_MODEL -> ImageViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_image, parent, false))
            else -> TextViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.item_text, parent, false), CustomTextWatcher())
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(position: Int)
    }

    inner class TextViewHolder(val view: View, private val customTextWatcher: CustomTextWatcher) : BaseViewHolder(view) {
        override fun bind(position: Int) {
            customTextWatcher.updatePosition = position
            view.text.addTextChangedListener(customTextWatcher)
            items[position].text?.apply {
                view.text.setText(this, TextView.BufferType.EDITABLE)
            }
        }
    }

    inner class ImageViewHolder(val view: View) : BaseViewHolder(view) {
        override fun bind(position: Int) {
            val file = if (items[position].imageUrl == null) S3_LINK + items[position].fileName else null
            file?.let {
                Picasso.with(view.context)
                        .load(it)
                        .into(view.image)
            } ?: Picasso.with(view.context)
                    .load(File((items[position]).imageUrl))
                    .into(view.image)
        }
    }

    inner class CustomTextWatcher : TextWatcher {

        var updatePosition: Int = 0


        override fun afterTextChanged(p0: Editable?) {
            p0?.toString()?.apply {
                (items[updatePosition]).text = this
                RxNetworkHelper.syncSubscriber(items.toList(), context)
            }

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }

    fun add(list: ArrayList<ModelBase>, isNormalUpload: Boolean) {
        list.forEach { textOrImage ->
            if (textOrImage.type == TYPE_IMAGE_MODEL) {
                items.apply {
                    add(textOrImage.apply image@ {
                        this@image.position = items.size
                        this@image.type = TYPE_IMAGE_MODEL
                    })
                    if (isNormalUpload)
                        add(ModelBase().apply text@ {
                            this@text.position = items.size
                            this@text.text = ""
                        })
                    RxNetworkHelper.syncSubscriber(this, context)
                }
            } else {
                items.add(textOrImage.apply {
                    type = TYPE_TEXT_MODEL
                    position = items.size
                })
            }
            RxNetworkHelper.saveModel(textOrImage)
        }

        notifyDataSetChanged()
    }
}