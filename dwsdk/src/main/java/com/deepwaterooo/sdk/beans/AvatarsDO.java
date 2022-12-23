package com.deepwaterooo.sdk.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class used to get the Avatar attributes from server API response
 */

public class AvatarsDO {

    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("assetURL")
    @Expose
    private String assetURL;
    @SerializedName("2x")
    @Expose
    private String _2x;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * @return The owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner The owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The assetURL
     */
    public String getAssetURL() {
        return assetURL;
    }

    /**
     * @param assetURL The assetURL
     */
    public void setAssetURL(String assetURL) {
        this.assetURL = assetURL;
    }

    /**
     * @return The _2x
     */
    public String get2x() {
        return _2x;
    }

    /**
     * @param _2x The 2x
     */
    public void set2x(String _2x) {
        this._2x = _2x;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The updatedAt
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }
}