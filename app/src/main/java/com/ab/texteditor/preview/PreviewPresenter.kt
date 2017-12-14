package com.ab.texteditor.preview

import com.ab.texteditor.model.ModelBase
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.vicpin.krealmextensions.queryAll
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by: anirban on 18/11/17.
 */

class PreviewPresenter : MvpBasePresenter<Preview.View>(), Preview.Presenter {
    override fun loadData() {
        Observable.just(true)
                .subscribeOn(Schedulers.io())
                .map {
                    ModelBase().queryAll()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showContent()
                    view?.setData(ArrayList<ModelBase>().apply { addAll(it) })
                }, {
                    view?.showError(it, false)
                })
    }

}
