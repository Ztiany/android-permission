/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.sdk.permission.impl.easypermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.sdk.permission.api.IPermissionUI;

import timber.log.Timber;

/**
 * <pre>
 *   See:
 *          https://github.com/googlesamples/easypermissions
 *          http://droidyue.com/blog/2016/01/17/understanding-marshmallow-runtime-permission/index.html
 *          http://jijiaxin89.com/2015/08/30/Android-s-Runtime-Permission/
 *
 * Utility to request and check System permissions for apps targeting Android M (API >= 23).
 *
 * </pre>
 */
final class EasyPermissions {

    interface PermissionCaller {

        void onPortionPermissionsGranted(boolean allGranted, int requestCode, List<String> perms);

        void onPermissionsDenied(int requestCode, List<String> perms);

        /**
         * @return PermissionCaller must be Fragment(app and support) or Activity
         */
        Object getRequester();

        IPermissionUI getPermissionUIProvider();
    }

    static private Context getContext(PermissionCaller permissionCaller) {
        Object object = permissionCaller.getRequester();
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            throw new RuntimeException("PermissionCaller getRequester mu");
        }
    }

    /**
     * ???????????????
     */
    static boolean hasPermissions(Context context, String... perms) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Timber.w("hasPermissions: API version < M, returning true by default");
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    /**
     * ????????????
     */
    static void requestPermissions(final PermissionCaller permissionCaller, final int requestCode, final String... perms) {

        if (hasPermissions(getContext(permissionCaller), perms)) {
            permissionCaller.onPortionPermissionsGranted(true, requestCode, Arrays.asList(perms));
            return;
        }

        final String[] filterPerms = filter(getContext(permissionCaller), perms);//??????????????????

        //???????????????????????????????????????
        boolean shouldShowRationale = false;
        for (String filterPerm : filterPerms) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(permissionCaller, filterPerm);
        }

        if (shouldShowRationale) {
            permissionCaller.getPermissionUIProvider().showPermissionRationaleDialog(
                    getContext(permissionCaller), filterPerms,
                    (dialog, which) -> executePermissionsRequest(permissionCaller, filterPerms, requestCode),
                    (dialog, which) -> permissionCaller.onPermissionsDenied(requestCode, Arrays.asList(filterPerms)));
            return;
        }

        //?????????????????????????????????????????????
        executePermissionsRequest(permissionCaller, filterPerms, requestCode);
    }

    /**
     * ????????????????????????
     */
    @TargetApi(23)
    static String[] filter(Context context, String[] perms) {
        List<String> permList = new ArrayList<>();
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                permList.add(perm);
            }
        }
        return permList.toArray(new String[0]);
    }

    /**
     * ??????????????????????????????
     */
    static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, PermissionCaller permissionCaller) {
        // Make a collection of granted and denied permissions from the request.
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        // Report denied permissions, if any.
        if (!denied.isEmpty()) {
            permissionCaller.onPermissionsDenied(requestCode, denied);
        }
        // Report granted permissions, if any.
        if (!granted.isEmpty()) {
            boolean allGranted = denied.isEmpty();
            permissionCaller.onPortionPermissionsGranted(allGranted, requestCode, granted);
        }
    }

    /**
     * ?????????????????????onPermissionDenial?????????,????????????shouldShowRationale???false????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    static boolean checkDeniedPermissionsNeverAskAgain(final PermissionCaller permissionCaller, List<String> deniedPerms) {
        boolean shouldShowRationale = false;
        for (String perm : deniedPerms) {
            shouldShowRationale = shouldShowRequestPermissionRationale(permissionCaller, perm);
        }
        return !shouldShowRationale;
    }

    /**
     * ??????????????????????????????intent??????????????????
     *
     * @param context ?????????
     * @return Intent
     */
    static Intent getIntentForPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     *
     * @return true?????????
     */
    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(PermissionCaller permissionCaller, String perm) {
        if (permissionCaller.getRequester() instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) permissionCaller.getRequester(), perm);
        } else if (permissionCaller.getRequester() instanceof Fragment) {
            return ((Fragment) permissionCaller.getRequester()).shouldShowRequestPermissionRationale(perm);
        } else
            return permissionCaller.getRequester() instanceof android.app.Fragment
                    && ((android.app.Fragment) permissionCaller.getRequester()).shouldShowRequestPermissionRationale(perm);
    }

    /**
     * ??????????????????
     */
    @TargetApi(23)
    private static void executePermissionsRequest(PermissionCaller permissionCaller, String[] perms, int requestCode) {
        if (permissionCaller.getRequester() instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) permissionCaller.getRequester(), perms, requestCode);
        } else if (permissionCaller.getRequester() instanceof Fragment) {
            ((Fragment) permissionCaller.getRequester()).requestPermissions(perms, requestCode);
        } else if (permissionCaller.getRequester() instanceof android.app.Fragment) {
            ((android.app.Fragment) permissionCaller.getRequester()).requestPermissions(perms, requestCode);
        }
    }

}
