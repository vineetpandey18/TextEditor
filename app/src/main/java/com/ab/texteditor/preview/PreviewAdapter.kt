package com.ab.texteditor.preview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ab.texteditor.R
import com.ab.texteditor.constants.S3_LINK
import com.ab.texteditor.model.ModelBase
import com.ab.texteditor.model.TYPE_IMAGE_MODEL
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_text_preview.view.*

/**
 * Created by: anirban on 19/11/17.
 */
class PreviewAdapter(val list: ArrayList<ModelBase>, val context: Context) : RecyclerView.Adapter<PreviewAdapter.BaseViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int) {
        holder!!.bind(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            when (viewType) {
                TYPE_IMAGE_MODEL -> ImageViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_image, parent, false))
                else -> TextViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.item_text_preview, parent, false))
            }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    abstract class BaseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(model: ModelBase)
    }

    inner class TextViewHolder(val baseView: View) : BaseViewHolder(baseView) {
        override fun bind(model: ModelBase) {
            view.text.text = model.text
        }
    }

    inner class ImageViewHolder(val baseView: View) : BaseViewHolder(baseView) {
        override fun bind(model: ModelBase) {
            baseView.setBackgroundColor(ContextCompat.getColor(baseView.context, R.color.white))
            Picasso.with(context)
                    .load(S3_LINK + model.fileName)
                    .into(view.image)
        }

    }

    fun addAll(data: ArrayList<ModelBase>?) {
        data?.let { list.addAll(it) }
        notifyDataSetChanged()
    }


}
