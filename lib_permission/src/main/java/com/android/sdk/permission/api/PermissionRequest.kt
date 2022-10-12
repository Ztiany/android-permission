package com.android.sdk.permission.api

interface PermissionRequest {

    fun onGranted(granted: (List<String>) -> Unit): PermissionRequest

    fun onDenied(denied: (List<String>) -> Unit): PermissionRequest

    fun showTips(showTips: Boolean): PermissionRequest

    fun askAgain(askAgain: Boolean): PermissionRequest

    fun customUI(uiProvider: IPermissionUI): PermissionRequest

    fun start()

}