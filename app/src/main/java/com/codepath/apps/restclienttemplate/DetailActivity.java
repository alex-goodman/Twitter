package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.ivProfPic) ImageView ivProfPic;
    @BindView(R.id.ivReply) ImageView ivReply;
    @BindView(R.id.ivRt) ImageView ivRt;
    @BindView(R.id.ivFave) ImageView ivFave;

    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.rtCount) TextView rtCount;
    @BindView(R.id.faveCount) TextView faveCount;

    Context context;
    TwitterClient client;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        context = getApplicationContext();
        client = TwitterApp.getRestClient(this);

        ButterKnife.bind(this);

        // style the ActionBar like the other screens
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher_twitter_round);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // unwrap the tweet sent from the main activity
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        // set all the elements
        tvName.setText(tweet.user.name);
        tvUsername.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvTime.setText(tweet.createdAt + " ago");
        rtCount.setText(tweet.rtCount);
        faveCount.setText(tweet.faveCount);

        GlideApp.with(this)
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .into(ivProfPic);

        // set onClick listeners for the three action buttons
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComposeActivity.class);
                i.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(i);
            }
        });

        ivRt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.retweet(Long.toString(tweet.uid), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(context, "Retweeted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("RetweetAction", responseString);
                    }
                });
            }
        });

        ivFave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.favoriteTweet(Long.toString(tweet.uid), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(context, "Favorited", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("FavoriteAction", responseString);
                    }
                });
            }
        });
    }
}
