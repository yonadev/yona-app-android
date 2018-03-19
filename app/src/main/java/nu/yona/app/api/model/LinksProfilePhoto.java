package nu.yona.app.api.model;

import android.content.ContentValues;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinksProfilePhoto extends BaseEntity {

    @SerializedName("yona:userPhoto")
    @Expose
    private Href userPhoto;

    @Override
    public ContentValues getDbContentValues() {
        return null;
    }

    public Href getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Href userPhoto) {
        this.userPhoto = userPhoto;
    }
}
