package com.sc.contacts;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhen on 2018/3/17.
 */

public class SCPluginContact extends CordovaPlugin {
    private CallbackContext callbackContext;
    private Context context;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        context = cordova.getActivity();
        this.callbackContext = callbackContext;
        if (args.get(0).equals("getAppInfo")) {
            ArrayList<AppInfo> infos = getAppList(context);
            String appStr = new Gson().toJson(infos);
            PluginResult result = new PluginResult(PluginResult.Status.OK, appStr);
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
            return true;
        } else if (isMarshmallow()) {
            if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                ArrayList<ContactInfo> infos = getContact(context);
                if (infos != null) {
                    String contactStr = new Gson().toJson(infos);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, contactStr);
                    result.setKeepCallback(true);
                    callbackContext.sendPluginResult(result);
                }
                return true;
            }
        }
        return super.execute(action, args, callbackContext);
    }
    
    private static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }
    
    private ArrayList<AppInfo> getAppList(Context context) {
        PackageManager pm = context.getPackageManager();
        // Return a List of all packages that are installed on the device.
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            AppInfo info = new AppInfo();
            info.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            info.setPackageName(packageInfo.packageName);
            info.setAppVersion(packageInfo.versionName);
            info.setUserApp((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
            
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);//android.intent.action.MAIN
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageInfo.packageName);
            List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
            if (resolveinfoList != null  && resolveinfoList .size() > 0 && info.isUserApp()) {
                info.setAppLuncherClass(resolveinfoList.get(0).activityInfo.name);
            }
            appList.add(info);
        }
        return appList;
    }
    
    private ArrayList<ContactInfo> getContact(Context context) {
        ArrayList<ContactInfo> infos = new ArrayList<ContactInfo>();
        try {
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            while (cursor.moveToNext()) {
                ContactInfo info = new ContactInfo();
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                info.setContactName(name);
                Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                      new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                                      ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                                      null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(
                                                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    info.getPhoneNos().add(phoneNumber);
                }
                phones.close();
                infos.add(info);
            }
            cursor.close();
        } catch (Exception e) {
            return null;
        }
        return infos;
    }
}

