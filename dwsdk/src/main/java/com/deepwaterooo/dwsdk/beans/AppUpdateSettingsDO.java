package com.deepwaterooo.dwsdk.beans;
/*
 * Copyright (c) 2017 Square Panda Inc.
 *
 * All Rights Reserved.
 * Dissemination, use, or reproduction of this material is strictly forbidden
 * unless prior written permission is obtained from Square Panda Inc.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.google.gson.annotations.SerializedName;

public class AppUpdateSettingsDO implements Parcelable {

    @SerializedName(JSONConstants.OWNER)
    private String owner;

    @SerializedName(JSONConstants.PLAYER_UPDATES)
    private String player_updates;

    @SerializedName(JSONConstants.CREATED_AT)
    private String createdAt;

    @SerializedName(JSONConstants.UPDATED_AT)
    private String updatedAt;

    @SerializedName(JSONConstants.ID)
    private String id;

    protected AppUpdateSettingsDO(Parcel in) {
        owner = in.readString();
        player_updates = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        id = in.readString();
    }

    public static final Creator<AppUpdateSettingsDO> CREATOR = new Creator<AppUpdateSettingsDO>() {
            @Override
            public AppUpdateSettingsDO createFromParcel(Parcel in) {
                return new AppUpdateSettingsDO(in);
            }

            @Override
            public AppUpdateSettingsDO[] newArray(int size) {
                return new AppUpdateSettingsDO[size];
            }
        };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(owner);
        dest.writeString(player_updates);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(id);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPlayer_updates() {
        return player_updates;
    }

    public void setPlayer_updates(String player_updates) {
        this.player_updates = player_updates;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getQrCodeStatus() { // 这应该是什么地方需要去掉的乱代码
        return true;
    }
}