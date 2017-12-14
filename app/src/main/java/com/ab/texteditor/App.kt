package com.ab.texteditor

import android.app.Application
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration


/**
 * Created by: anirban on 18/11/17.
 */
class App : Application(), RealmMigration {

    companion object {
        var shouldSync: Boolean = false
    }

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        //This is not handled intentionally
    }


    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(this)
                .build()
        Realm.setDefaultConfiguration(configuration)

        Realm.getInstance(configuration)

        RxNetworkHelper.init(this)


    }


}