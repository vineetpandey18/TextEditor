package com.ab.texteditor.preview

import com.ab.texteditor.model.ModelBase
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView

/**
 * Created by: anirban on 18/11/17.
 */
class Preview{
    interface View: MvpLceView<ArrayList<ModelBase>>{

    }

    interface Presenter: MvpPresenter<View>{
        fun loadData()

    }
}