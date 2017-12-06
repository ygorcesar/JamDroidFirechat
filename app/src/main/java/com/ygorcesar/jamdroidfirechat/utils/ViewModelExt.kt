package com.ygorcesar.jamdroidfirechat.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity

inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(): VM {
    return ViewModelProviders.of(this).get(VM::class.java)
}