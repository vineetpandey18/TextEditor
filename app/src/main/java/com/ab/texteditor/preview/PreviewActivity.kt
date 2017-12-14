package com.ab.texteditor.preview

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.ab.texteditor.R
import com.ab.texteditor.base.BaseActivity
import com.ab.texteditor.model.ModelBase
import kotlinx.android.synthetic.main.activity_main.*



/**
 * Created by: anirban on 18/11/17.
 */

open class PreviewActivity : BaseActivity<RecyclerView, ArrayList<ModelBase>, Preview.View, Preview.Presenter>(), Preview.View {

    private var adapter: PreviewAdapter = PreviewAdapter(arrayListOf(), this)

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadData()
    }

    override fun createPresenter(): Preview.Presenter {
        return PreviewPresenter()
    }

    override fun setData(data: ArrayList<ModelBase>?) {
        adapter.addAll(data)
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return e?.message ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData(false)
        addImg.visibility = View.GONE




        contentView.apply {
            val marginLayoutParams = this.getLayoutParams() as ViewGroup.MarginLayoutParams
            marginLayoutParams.setMargins(35, 20, 35, 20)
            this.setLayoutParams(marginLayoutParams)
            layoutManager = LinearLayoutManager(this@PreviewActivity)
            this.adapter = this@PreviewActivity.adapter
        }
    }

}
