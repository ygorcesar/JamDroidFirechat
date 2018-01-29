package com.ygorcesar.jamdroidfirechat.extensions

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(): VM {
    return ViewModelProviders.of(this).get(VM::class.java)
}

inline fun <reified VM : ViewModel> Fragment.provideViewModel(): VM {
    return ViewModelProviders.of(this).get(VM::class.java)
}

inline fun <reified VM : ViewModel> Fragment.provideViewModelWithFactory(factory: ViewModelProvider.Factory): VM {
    return ViewModelProviders.of(activity!!, factory).get(VM::class.java)
}