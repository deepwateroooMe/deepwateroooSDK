package com.deepwaterooo.sdk.beans;

import com.google.gson.annotations.SerializedName;
import com.deepwaterooo.sdk.appconfig.JSONConstants;

/**
 * Class used to save the Login User attributes after successful login to send Game developer
 */
// 这个类是可有可无可以删除的.到最后如何嫌工作量太大,或是仅只有一个教育模式就加这个,可能甚至连启蒙模式也删掉了.到时候再看
public class ParentInfoDO {

    @SerializedName(JSONConstants.OWNER)
    private String owner;

    @SerializedName(JSONConstants.FIRST_NAME)
    private String firstName;

    @SerializedName(JSONConstants.LAST_NAME)
    private String lastName;

    @SerializedName(JSONConstants.GAME)
    private String game;

    @SerializedName(JSONConstants.ROLE)
    private String role;

    @SerializedName(JSONConstants.WELCOME_FEED)
    private String welcomefeed;

    @SerializedName(JSONConstants.STATUS)
    private String status;

    @SerializedName(JSONConstants.USERNAME)
    private String username;

    @SerializedName(JSONConstants.CREATED_AT)
    private String createdAt;

    @SerializedName(JSONConstants.UPDATED_AT)
    private String updatedAt;

    @SerializedName(JSONConstants.PLAYER_LIMIT)
    private int playerLimit;

    @SerializedName(JSONConstants.ID)
    private String id;

    @SerializedName(JSONConstants.PLAYER_COUNT)
    private int playerCount;

    @SerializedName(JSONConstants.GR_AVATAR_URL)
    private String gravatarUrl;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getWelcomefeed() {
        return welcomefeed;
    }

    public void setWelcomefeed(String welcomefeed) {
        this.welcomefeed = welcomefeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getPlayerLimit() {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public String getGravatarUrl() {
        return gravatarUrl;
    }

    public void setGravatarUrl(String gravatarUrl) {
        this.gravatarUrl = gravatarUrl;
    }
}