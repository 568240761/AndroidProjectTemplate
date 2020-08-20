package com.ly.module.basic

import android.content.Context
import androidx.fragment.app.Fragment
import com.ly.module.basic.manager.ObservableManager

/**
 * Created by Lan Yang on 2020/8/20
 * 项目里所有[Fragment]的父类
 */
class BasicFragment : Fragment(),ObservableManager.Observer {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ObservableManager.registerObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        ObservableManager.unregisterObserver(this)
    }

    override fun onChange(type: Int, data: Any?) {}
}