package com.ab.texteditor

import android.app.Activity
import android.app.Application
import android.util.Log
import com.ab.texteditor.constants.INTERNAL_STORAGE_PATH
import com.ab.texteditor.model.ModelBase
import com.ab.texteditor.model.TYPE_TEXT_MODEL
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.vicpin.krealmextensions.save
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import java.io.File


/**
 * Created by: anirban on 18/11/17.
 */
object RxNetworkHelper {
    var credentialsProvider: CognitoCachingCredentialsProvider? = null
    private var syncSubscription: Subscription? = null

    fun init(application: Application) {
        credentialsProvider = credentialsProvider ?: CognitoCachingCredentialsProvider(
                application, /* get the context for the application */
                "ap-south-1:b7e4b37c-38b7-441f-9fbb-0a37e5cae43a", /* Identity Pool ID */
                Regions.AP_SOUTH_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )
    }

    fun uploadFile(application: Application, filePath: String) {
        credentialsProvider = credentialsProvider ?: CognitoCachingCredentialsProvider(
                application, /* get the context for the application */
                "ap-south-1:b7e4b37c-38b7-441f-9fbb-0a37e5cae43a", /* Identity Pool ID */
                Regions.AP_SOUTH_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        )
        val s3 = AmazonS3Client(credentialsProvider)
        val transferUtility = TransferUtility(s3, application.applicationContext)
        val file = File(filePath)

        Observable.just(transferUtility.upload(
                "textediting/images",     /* The bucket to upload to */
                file.name,    /* The key for the uploaded object */
                File(filePath)        /* The file where the data to upload exists */
        )).subscribeOn(Schedulers.io())
                .subscribe({
                    it.refresh()
                }, {
                    Log.e("TAG", "error: " + it.message)
                })

    }

    fun syncSubscriber(items: List<ModelBase>, context: Activity) {
        val deviceId = Utils.getDeviceIdForPushNotification(context)
        val file = File(INTERNAL_STORAGE_PATH + deviceId + ".txt")
        var text = ""


        syncSubscription?.cancelOngoing()
        syncSubscription = Observable.from(items)
                .subscribeOn(Schedulers.io())
                .retry(5)
                .map {
                    it.save()
                    when (it.type) {
                        TYPE_TEXT_MODEL -> text += it.text
                        else -> text += "|||||<<<<<" + it.fileName + "|||||"
                    }
                    it
                }.toList()
                .map {
                    if (!file.exists()) {
                        file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    file.writeText(text)
                    file
                }.map {
            credentialsProvider = credentialsProvider ?: CognitoCachingCredentialsProvider(
                    context.applicationContext, /* get the context for the application */
                    "ap-south-1:b7e4b37c-38b7-441f-9fbb-0a37e5cae43a", /* Identity Pool ID */
                    Regions.AP_SOUTH_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
            )
            val s3 = AmazonS3Client(credentialsProvider)
            TransferUtility(s3, context.applicationContext).upload(
                    "textediting/deviceContent",     /* The bucket to upload to */
                    file.name,    /* The key for the uploaded object */
                    file        /* The file where the data to upload exists */
            )
        }
                .subscribe({
                    Log.e("TAG", "Successful")
                }, {
                    Log.e("TAG", it.message)
                })
    }

    fun saveModel(model: ModelBase) {
        Observable.just(model)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if(it.text != null  || it.imageUrl != null || it.fileName != null)
                    it.save()
                }, {

                })
    }

}