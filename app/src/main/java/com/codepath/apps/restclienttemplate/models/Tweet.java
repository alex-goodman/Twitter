package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public long uid; //database id for the tweet
    public String createdAt; // tweet time
    public User user;
    public String faveCount; // number of likes
    public String rtCount; // number of retweets


    public Tweet() {}

    // deserialize JSON
    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();

        // get values from JSON
        tweet.body = object.getString("text");
        tweet.uid = object.getLong("id");
        tweet.createdAt = getRelativeTimeAgo(object.getString("created_at"));
        tweet.user = User.fromJSON(object.getJSONObject("user"));
        tweet.faveCount = object.getString("favorite_count");
        tweet.rtCount = object.getString("retweet_count");
        return tweet;

    }

    // from Jerome Jaglale at http://jeromejaglale.com/doc/java/twitter
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        long dateMillis;

        try {
            dateMillis = sf.parse(rawJsonDate).getTime();
            Date today = new Date();
            long duration = today.getTime() - dateMillis;
            int second = 1000;
            int minute = 60 * second;
            int hour = minute * 60;
            int day = hour * 24;

            if (duration < second * 7) return "now";
            else if (duration < minute) {
                int n = (int) Math.floor(duration / second);
                return n + "s";
            }
            else if (duration < 2 * minute) return "1m";
            else if (duration < hour) {
                int n = (int) Math.floor(duration / minute);
                return n + "m";
            }
            else if (duration < hour * 2) {
                return "1h";
            }
            else if (duration < day) {
                int n = (int) Math.floor(duration / hour);
                return n + "h";
            }
            else if (duration > day && duration < 2 * day) {
                return "yesterday";
            }
            else {
                int n = (int) Math.floor(duration / day);
                return n + "d";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
