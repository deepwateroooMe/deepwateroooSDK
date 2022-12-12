package com.deepwaterooo.dwsdk.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.google.gson.annotations.SerializedName;

public class AppUpdatesDO implements Parcelable {

    @SerializedName(JSONConstants.ROLE)
    private String role;

    @SerializedName(JSONConstants.SETTINGS)
    private AppUpdateSettingsDO settings;

    protected AppUpdatesDO(Parcel in) {
        role = in.readString();
    }

    public static final Creator<AppUpdatesDO> CREATOR = new Creator<AppUpdatesDO>() {
            @Override
            public AppUpdatesDO createFromParcel(Parcel in) {
                return new AppUpdatesDO(in);
            }

            @Override
            public AppUpdatesDO[] newArray(int size) {
                return new AppUpdatesDO[size];
            }
        };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(role);
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setSettings(AppUpdateSettingsDO settings) {
        this.settings = settings;
    }
    public AppUpdateSettingsDO getSettings() {
        return this.settings;
    }
}