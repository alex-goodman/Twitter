package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {
    //attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public User() {}

    public static User fromJSON(JSONObject object) throws JSONException {
        User user = new User();

        // fill in the values
        user.name = object.getString("name");
        user.uid = object.getLong("id");
        user.screenName = object.getString("screen_name");
        user.profileImageUrl = object.getString("profile_image_url");

        return user;
    }
}
