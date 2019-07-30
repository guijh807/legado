package io.legado.app.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus

inline fun <reified EVENT> eventObservable(tag: String): LiveEventBus.Observable<EVENT> {
    return LiveEventBus.get().with(tag, EVENT::class.java)
}

inline fun <reified EVENT> postEvent(tag: String, event: EVENT) {
    return LiveEventBus.get().with(tag, EVENT::class.java).post(event)
}

inline fun <reified EVENT> AppCompatActivity.observeEvent(tag: String, observer: Observer<EVENT>) {
    return eventObservable<EVENT>(tag).observe(this, observer)
}

inline fun <reified EVENT> AppCompatActivity.observeEventSticky(tag: String, observer: Observer<EVENT>) {
    return eventObservable<EVENT>(tag).observeSticky(this, observer)
}

inline fun <reified EVENT> Fragment.observeEvent(tag: String, observer: Observer<EVENT>) {
    return eventObservable<EVENT>(tag).observe(this, observer)
}

inline fun <reified EVENT> Fragment.observeEventSticky(tag: String, observer: Observer<EVENT>) {
    return eventObservable<EVENT>(tag).observeSticky(this, observer)
}