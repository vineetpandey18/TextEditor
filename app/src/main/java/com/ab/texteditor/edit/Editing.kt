package com.ab.texteditor.edit

import android.content.Context
import com.ab.texteditor.model.ModelBase
import com.hannesdorfmann.mosby.mvp.MvpPresenter
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView

/**
 * Created by: anirban on 16/11/17.
 */
class Editing {
    interface View: MvpLceView<ArrayList<ModelBase>>{

    }

    interface Presenter:MvpPresenter<View>{
        fun initialSetup(context: Context)

    }
}