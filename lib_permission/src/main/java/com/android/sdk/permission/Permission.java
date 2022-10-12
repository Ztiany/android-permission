package com.android.sdk.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is copied from <a href='https://github.com/yanzhenjie/AndPermission'>AndPermission</a>.
 */
public final class Permission {

    /**
     * Turn permissions into text.
     */
    public static List<String> transformText(Context context, String... permissions) {
        return transformText(context, Arrays.asList(permissions));
    }

    /**
     * Turn permissions into text.
     */
    public static List<String> transformText(Context context, String[]... groups) {
        List<String> permissionList = new ArrayList<>();
        for (String[] group : groups) {
            permissionList.addAll(Arrays.asList(group));
        }
        return transformText(context, permissionList);
    }

    /**
     * Turn permissions into text.
     */
    public static List<String> transformText(Context context, List<String> permissions) {
        List<String> textList = new ArrayList<>();
        for (String permission : permissions) {
            switch (permission) {
                case Manifest.permission.READ_CALENDAR:
                case Manifest.permission.WRITE_CALENDAR: {
                    String message = context.getString(R.string.Permission_name_calendar);

                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }

                case Manifest.permission.CAMERA: {
                    String message = context.getString(R.string.Permission_name_camera);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.GET_ACCOUNTS:
                case Manifest.permission.READ_CONTACTS:
                case Manifest.permission.WRITE_CONTACTS: {
                    String message = context.getString(R.string.Permission_name_contacts);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.ACCESS_FINE_LOCATION:
                case Manifest.permission.ACCESS_COARSE_LOCATION: {
                    String message = context.getString(R.string.Permission_name_accounts);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.RECORD_AUDIO: {
                    String message = context.getString(R.string.Permission_name_microphone);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_PHONE_STATE:
                case Manifest.permission.CALL_PHONE:
                case Manifest.permission.ADD_VOICEMAIL:
                case Manifest.permission.USE_SIP:
                case Manifest.permission.READ_PHONE_NUMBERS:
                case Manifest.permission.ANSWER_PHONE_CALLS: {
                    String message = context.getString(R.string.Permission_name_phone);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_CALL_LOG:
                case Manifest.permission.WRITE_CALL_LOG:
                case Manifest.permission.PROCESS_OUTGOING_CALLS: {
                    int messageId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ?
                            R.string.Permission_name_call_log : R.string.Permission_name_phone;
                    String message = context.getString(messageId);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.BODY_SENSORS: {
                    String message = context.getString(R.string.Permission_name_sensors);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.ACTIVITY_RECOGNITION: {
                    String message = context.getString(R.string.Permission_name_activity_recognition);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.SEND_SMS:
                case Manifest.permission.RECEIVE_SMS:
                case Manifest.permission.READ_SMS:
                case Manifest.permission.RECEIVE_WAP_PUSH:
                case Manifest.permission.RECEIVE_MMS: {
                    String message = context.getString(R.string.Permission_name_sms);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE: {
                    String message = context.getString(R.string.Permission_name_storage);
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
            }
        }
        return textList;
    }

} 