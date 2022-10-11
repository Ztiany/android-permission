package com.android.sdk.permission.utils

import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.option.Option

/**
 *@author Ztiany
 */
fun HostWrapper.toAndPermission(): Option {
    return if (fragment != null) {
        AndPermission.with(fragment)
    } else {
        AndPermission.with(context)
    }
}