package com.ly.module.basic.manager

import android.database.Observable

/**
 * Created by Lan Yang on 2020/8/20
 * 数据变化通知管理
 */
object ObservableManager : Observable<ObservableManager.Observer>() {

    /**
     * 通知数据类型为[type]的实例有数据[data]变化
     */
    fun notifyChange(type: Int, data: Any? = null) {
        synchronized(mObservers) {
            for (observer in mObservers) {
                observer.onChange(type, data)
            }
        }
    }

    interface Observer {
        fun onChange(type: Int, data: Any? = null)
    }
}