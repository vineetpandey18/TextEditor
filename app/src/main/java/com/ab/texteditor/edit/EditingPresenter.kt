package com.ab.texteditor.edit

import android.content.Context
import android.util.Log
import com.ab.texteditor.RxNetworkHelper
import com.ab.texteditor.Utils
import com.ab.texteditor.constants.INTERNAL_STORAGE_PATH
import com.ab.texteditor.model.ModelBase
import com.ab.texteditor.model.TYPE_IMAGE_MODEL
import com.ab.texteditor.model.TYPE_TEXT_MODEL
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.saveAll
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.util.Collections.sort


/**
 * Created by: anirban on 16/11/17.
 */
class EditingPresenter : MvpBasePresenter<Editing.View>(), Editing.Presenter {
    override fun initialSetup(context: Context) {

        Observable.just(true)
                .subscribeOn(Schedulers.io())
                .map { ModelBase.hasData() }
                .concatMap { hasData ->
                    if (hasData)
                        return@concatMap Observable.just(ModelBase().queryAll().apply {
                            sort(this)
                        })

                    val s3 = AmazonS3Client(RxNetworkHelper.credentialsProvider)
                    val file = File(INTERNAL_STORAGE_PATH, Utils.getDeviceIdForPushNotification(context) + ".txt")
                    if (!file.exists()) {
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    file.setReadable(true)
                    file.setWritable(true)
                    val tempFile = File(INTERNAL_STORAGE_PATH, Utils.getDeviceIdForPushNotification(context) + "-temp.txt")
                    if (!tempFile.exists()) {
                        tempFile.parentFile.mkdirs()
                        tempFile.createNewFile()
                    }
                    Log.e("TAG", file.exists().toString())
                    val objectData = s3.getObject("textediting/deviceContent", Utils.getDeviceIdForPushNotification(context) + ".txt")
                    TransferUtility(s3, context).download("textediting/deviceContent", Utils.getDeviceIdForPushNotification(context) + ".txt",
                            tempFile)

                    val reader = BufferedReader(InputStreamReader(
                            objectData.getObjectContent()))
                    val writer = OutputStreamWriter(FileOutputStream(file))

                    while (true) {
                        val line = reader.readLine() ?: break
                        writer.write(line + "\n")
                    }
                    writer.flush()
                    writer.close()
                    reader.close()

                    Log.e("TAG", file.readText())
                    return@concatMap convertFileToDataList(context, file)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.showContent()
                    view?.setData(ArrayList<ModelBase>().apply { addAll(it) })
                }, {
                    Log.e("TAG", it.message)
                    view?.showContent()
                    view?.setData(ArrayList<ModelBase>().apply { add(ModelBase().apply { type = TYPE_TEXT_MODEL }) })

                })

        /*view?.showContent()
        view?.setData(ModelBase().apply {
            position = 0
            type = TYPE_TEXT_MODEL
        })*/

    }

    private fun convertFileToDataList(context: Context, file: File): Observable<ArrayList<ModelBase>> {
        Log.e("TAG", file.path + "/t" + file.exists())
        return Observable.just(file)
                .concatMap {
                    val text = it.readText()
                    return@concatMap Observable.just(text)
                }.concatMap {
            val list = arrayListOf<ModelBase>()
            val array = it.split("|||||")
            var model: ModelBase
            for (temp in array) {
                if (temp.startsWith("<<<<<")) {
                    model = ModelBase().apply {
                        type = TYPE_IMAGE_MODEL
                        position = list.size
                        fileName = temp.substring(5)
                    }
                } else {
                    model = ModelBase().apply {
                        type = TYPE_TEXT_MODEL
                        position = list.size
                        text = temp
                    }
                }
                list.add(model)
            }
            Log.e("TAG", "List size: " + list.size)
            return@concatMap Observable.just(list.apply {
                sort()
                saveAll()
            })
        }
    }
}