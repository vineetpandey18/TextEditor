package com.ab.texteditor

import rx.Subscription

/**
 * Created by: anirban on 18/11/17.
 */
fun Subscription.cancelOngoing() {
    if (!this.isUnsubscribed) {
        this.unsubscribe()
    }
}