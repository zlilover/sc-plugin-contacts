package com.sc.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by lizhen on 2018/3/17.
 */

public class AppInfo implements Parcelable{
    //应用名称
    private String appName;
    //应用版本号
    private String appVersion;
    //应用包名
    private String packageName;
    //是否是用户app
    private boolean isUserApp;
    //app的启动类名
    private String appLuncherClass;
    
    public AppInfo() {
    }
    
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName) {
        this.appName = appName;
    }
    
    public String getAppVersion() {
        return appVersion;
    }
    
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public boolean isUserApp() {
        return isUserApp;
    }
    
    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }
    
    public String getAppLuncherClass() {
        return appLuncherClass;
    }
    
    public void setAppLuncherClass(String appLuncherClass) {
        this.appLuncherClass = appLuncherClass;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
    @Override
    public AppInfo[] newArray(int size) {
    return new AppInfo[size];
}

@Override
public AppInfo createFromParcel(Parcel source) {
AppInfo appInfo = new AppInfo();
appInfo.setAppName(source.readString());
appInfo.setAppVersion(source.readString());
appInfo.setPackageName(source.readString());
appInfo.setUserApp(source.readInt() == 1);
appInfo.setAppLuncherClass(source.readString());
return null;
}
};

@Override
public void writeToParcel(Parcel dest, int flags) {
dest.writeString(appName);
dest.writeString(appVersion);
dest.writeString(packageName);
dest.writeInt(isUserApp ? 1: 0);
dest.writeString(appLuncherClass);
}

@Override
public String toString() {
return "AppInfo [appName=" + appName
+ ", app_version=" + appVersion + ", packagename="
+ packageName + ", isUserApp=" + isUserApp + "]";
}
}

