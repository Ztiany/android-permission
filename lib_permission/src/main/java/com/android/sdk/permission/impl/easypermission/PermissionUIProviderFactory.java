package com.android.sdk.permission.impl.easypermission;


import com.android.sdk.permission.api.IPermissionUI;

public class PermissionUIProviderFactory {

    private static IPermissionUI sIPermissionUI;

    static IPermissionUI getPermissionUIProvider() {
        if (sIPermissionUI == null) {
            sIPermissionUI = new DefaultPermissionUI();
        }
        return sIPermissionUI;
    }

    public static void registerPermissionUIProvider(IPermissionUI iPermissionUI) {
        sIPermissionUI = iPermissionUI;
    }

}
