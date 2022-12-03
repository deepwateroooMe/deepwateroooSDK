package com.deepwaterooo.dwsdk.beans;

import android.os.Parcel;
import android.os.Parcelable;
import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.google.gson.annotations.SerializedName;

/**
 * Class used to get the Login user attributes from server API response
 */
public class LoginUserDO implements Parcelable {

    @SerializedName(JSONConstants.OWNER)
    private String owner;
    @SerializedName(JSONConstants.FIRST_NAME)
    private String firstName;
    @SerializedName(JSONConstants.LAST_NAME)
    private String lastName;
    @SerializedName(JSONConstants.EMAIL)
    private String email;
    @SerializedName(JSONConstants.GAME)
    private String game;
    @SerializedName(JSONConstants.ROLE)
    private String role;
    @SerializedName(JSONConstants.WELCOME_FEED)
    private String welcomefeed;
    @SerializedName(JSONConstants.TERMS)
    private boolean terms;
    @SerializedName(JSONConstants.PRIVACY)
    private boolean privacy;
    @SerializedName(JSONConstants.STATUS)
    private String status;
    @SerializedName(JSONConstants.USERNAME)
    private String username;
    @SerializedName(JSONConstants.CREATED_AT)
    private String createdAt;
    @SerializedName(JSONConstants.UPDATED_AT)
    private String updatedAt;
    @SerializedName(JSONConstants.CONFIRMATION_TOKEN)
    private String confirmationToken;
    @SerializedName(JSONConstants.CONFIRMATION_TOKEN_EXPIRES)
    private String confirmationTokenExpires;
    @SerializedName(JSONConstants.PLAYER_LIMIT)
    private int playerLimit;
    @SerializedName(JSONConstants.ID)
    private String id;
    @SerializedName(JSONConstants.PLAYER_COUNT)
    private int playerCount;
    @SerializedName(JSONConstants.PLAYER)
    ArrayList<PlayerDO> player = new ArrayList<PlayerDO>();
    @SerializedName(JSONConstants.TOKEN_ID)
    private String tokenId;
    @SerializedName(JSONConstants.CUSTOM_WORDS_LIMIT)
    private String customWordsLimit;
    @SerializedName(JSONConstants.GR_AVATAR_URL)
    private String gravatarUrl;
    @SerializedName(JSONConstants.BUCKET_URL)
    private String bucketUrl;
    @SerializedName(JSONConstants.STAY_UP_TO_DATE)
    private String spNews;
    @SerializedName(JSONConstants.LOGINTOKEN)
    private String loginToken;
    @SerializedName(JSONConstants.LOGINTOKENEXPIRES)
    private String loginTokenExpires;
    @SerializedName(JSONConstants.RESETPASSWORDTOKEN)
    private String resetPasswordToken;
    @SerializedName(JSONConstants.RESETPASSWORDEXPIRES)
    private String resetPasswordExpires;
    @SerializedName(JSONConstants.SETTINGS)
    private AppUpdateSettingsDO settings;

    protected LoginUserDO(Parcel in) {
        owner = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        game = in.readString();
        role = in.readString();
        welcomefeed = in.readString();
        terms = in.readByte() != 0;
        privacy = in.readByte() != 0;
        status = in.readString();
        username = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        confirmationToken = in.readString();
        confirmationTokenExpires = in.readString();
        playerLimit = in.readInt();
        id = in.readString();
        playerCount = in.readInt();
        tokenId = in.readString();
        customWordsLimit = in.readString();
        gravatarUrl = in.readString();
        in.readTypedList(player, PlayerDO.CREATOR);
        bucketUrl = in.readString();
        spNews = in.readString();
        loginToken = in.readString();
        loginTokenExpires = in.readString();
        resetPasswordToken = in.readString();
        resetPasswordExpires = in.readString();
        settings = in.readParcelable(AppUpdateSettingsDO.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<LoginUserDO> CREATOR = new Creator<LoginUserDO>() {
            @Override
            public LoginUserDO createFromParcel(Parcel in) {
                return new LoginUserDO(in);
            }
            @Override
            public LoginUserDO[] newArray(int size) {
                return new LoginUserDO[size];
            }
        };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(owner);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(game);
        dest.writeString(role);
        dest.writeString(welcomefeed);
        dest.writeByte((byte) (terms ? 1 : 0));
        dest.writeByte((byte) (privacy ? 1 : 0));
        dest.writeString(status);
        dest.writeString(username);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(confirmationToken);
        dest.writeString(confirmationTokenExpires);
        dest.writeInt(playerLimit);
        dest.writeString(id);
        dest.writeInt(playerCount);
        dest.writeString(tokenId);
        dest.writeString(customWordsLimit);
        dest.writeString(gravatarUrl);
        dest.writeTypedList(player);
        dest.writeString(bucketUrl);
        dest.writeString(spNews);
        dest.writeString(loginToken);
        dest.writeString(loginTokenExpires);
        dest.writeString(resetPasswordToken);
        dest.writeString(resetPasswordExpires);
        dest.writeParcelable(settings, flags);
    }
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
    public boolean getTerms() {
        return terms;
    }
    public void setTerms(boolean terms) {
        this.terms = terms;
    }
    public boolean getPrivacy() {
        return privacy;
    }
    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
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
    public String getConfirmationToken() {
        return confirmationToken;
    }
    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }
    public String getConfirmationTokenExpires() {
        return confirmationTokenExpires;
    }
    public void setConfirmationTokenExpires(String confirmationTokenExpires) {
        this.confirmationTokenExpires = confirmationTokenExpires;
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
    public ArrayList<PlayerDO> getPlayer() {
        return player;
    }
    public void setPlayer(ArrayList<PlayerDO> player) {
        this.player = player;
    }
    public String getTokenId() {
        return tokenId;
    }
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
    public String getCustomWordsLimit() {
        return customWordsLimit;
    }
    public void setCustomWordsLimit(String customWordsLimit) {
        this.customWordsLimit = customWordsLimit;
    }
    public String getGravatarUrl() {
        return gravatarUrl;
    }
    public void setGravatarUrl(String gravatarUrl) {
        this.gravatarUrl = gravatarUrl;
    }
    public String getBucketUrl() {
        return bucketUrl;
    }
    public void setBucketUrl(String bucketUrl) {
        this.bucketUrl = bucketUrl;
    }
    public String getSpNews() {
        return spNews;
    }
    public void setSpNews(String spNews) {
        this.spNews = spNews;
    }
    public String getLoginToken() {
        return loginToken;
    }
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
    public String getLoginTokenExpires() {
        return loginTokenExpires;
    }
    public void setLoginTokenExpires(String loginTokenExpires) {
        this.loginTokenExpires = loginTokenExpires;
    }
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }
    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }
    public String getResetPasswordExpires() {
        return resetPasswordExpires;
    }
    public void setResetPasswordExpires(String resetPasswordExpires) {
        this.resetPasswordExpires = resetPasswordExpires;
    }
    public AppUpdateSettingsDO getSettings() {
        return settings;
    }
    public void setSettings(AppUpdateSettingsDO settings) {
        this.settings = settings;
    }
}