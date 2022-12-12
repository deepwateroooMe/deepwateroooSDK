package com.deepwaterooo.dwsdk.beans;


import android.os.Parcel;
import android.os.Parcelable;

import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.google.gson.annotations.SerializedName;
import com.deepwaterooo.dwsdk.appconfig.JSONConstants;

/**
 * Class used to get the PlayerDO attributes from server Login API response
 */
public class PlayerDO implements Parcelable {

    public static final Creator<PlayerDO> CREATOR = new Creator<PlayerDO>() {
            @Override
            public PlayerDO createFromParcel(Parcel in) {
                return new PlayerDO(in);
            }

            @Override
            public PlayerDO[] newArray(int size) {
                return new PlayerDO[size];
            }
        };
    @SerializedName(JSONConstants.FIRST_NAME)
    private String firstName;
    @SerializedName(JSONConstants.LAST_NAME)
    private String lastName;
    @SerializedName(JSONConstants.DATE_OF_BIRTH)
    private String dateofBirth;
    @SerializedName(JSONConstants.GENDER)
    private String gender;
    @SerializedName(JSONConstants.GRADE)
    private String grade;
//    @SerializedName(JSONConstants.PARENT_EMAIL)
//    private String parentEmail;
//    @SerializedName(JSONConstants.STUDENT_ID)
//    private String studentID;
    @SerializedName(JSONConstants.IEP)
    private String IEP;
    @SerializedName(JSONConstants.LANGUAGE)
    private String language;
    @SerializedName(JSONConstants.IEP_DESCRIPTION)
    private String IEPDescription;
    @SerializedName(JSONConstants.IMG_BASE_64)
    private String imgbase64;
    @SerializedName(JSONConstants.FULL_NAME)
    private String fullName;
    @SerializedName(JSONConstants.OWNER)
    private String owner;
    @SerializedName(JSONConstants.PROFILE_URL)
    private String profileURL;
    @SerializedName(JSONConstants.STATUS)
    private String status;
    @SerializedName(JSONConstants.CREATED_AT)
    private String createdAt;
    @SerializedName(JSONConstants.UPDATED_AT)
    private String updatedAt;
    @SerializedName(JSONConstants.ID)
    private String id;
    @SerializedName(JSONConstants.CURRENT_LEVEL)
    private String currentLevel;
    @SerializedName(JSONConstants.ACTIVITY_DATE)
    private String activityDate;
    @SerializedName(JSONConstants.GAME)
    private String game;
    @SerializedName(JSONConstants.AVATAR)
    private String avatar;

    protected PlayerDO(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        dateofBirth = in.readString();
        gender = in.readString();
        grade = in.readString();
        IEP = in.readString();
        language = in.readString();
        IEPDescription = in.readString();
        imgbase64 = in.readString();
        fullName = in.readString();
        owner = in.readString();
        profileURL = in.readString();
        status = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        id = in.readString();
        currentLevel = in.readString();
        activityDate = in.readString();
        game = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(dateofBirth);
        dest.writeString(gender);
        dest.writeString(grade);
//        dest.writeString(parentEmail);
//        dest.writeString(studentID);
        dest.writeString(IEP);
        dest.writeString(language);
        dest.writeString(IEPDescription);
        dest.writeString(imgbase64);
        dest.writeString(fullName);
        dest.writeString(owner);
        dest.writeString(profileURL);
        dest.writeString(status);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(id);
        dest.writeString(currentLevel);
        dest.writeString(activityDate);
        dest.writeString(game);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

//    public String getParentEmail() {
//        return parentEmail;
//    }
//
//    public void setParentEmail(String parentEmail) {
//        this.parentEmail = parentEmail;
//    }
//
//    public String getStudentID() {
//        return studentID;
//    }
//
//    public void setStudentID(String studentID) {
//        this.studentID = studentID;
//    }

    public String getIEP() {
        return IEP;
    }

    public void setIEP(String IEP) {
        this.IEP = IEP;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIEPDescription() {
        return IEPDescription;
    }

    public void setIEPDescription(String IEPDescription) {
        this.IEPDescription = IEPDescription;
    }

    public String getImgbase64() {
        return imgbase64;
    }

    public void setImgbase64(String imgbase64) {
        this.imgbase64 = imgbase64;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getProfileImageId() {
        return avatar;
    }

    public void setProfileImageId(String imageId) {
        this.avatar = imageId;
    }
}