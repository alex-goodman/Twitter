package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    // pass in Tweets array in the constructor
    private static List<Tweet> mTweets;
    static Context context;
    static TwitterClient client;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        client = TwitterApp.getRestClient(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }


    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@" + tweet.user.screenName);
        holder.tvTime.setText(tweet.createdAt);
        holder.faveCount.setText(tweet.faveCount);
        holder.rtCount.setText(tweet.rtCount);

        // load in profile picture with Glide
        GlideApp.with(context)
                .load(tweet.user.profileImageUrl)
                .transform(new RoundedCornersTransformation(15, 0))
                .into(holder.ivProfPic);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // might not need these next two methods in the end
    // clear all elements in the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfPic)ImageView ivProfPic;
        @BindView(R.id.tvUsername)TextView tvUsername;
        @BindView(R.id.tvBody)TextView tvBody;
        @BindView(R.id.tvScreenName)TextView tvScreenName;
        @BindView(R.id.tvTime)TextView tvTime;
        @BindView(R.id.reply)ImageView reply;
        @BindView(R.id.fave)ImageView fave;
        @BindView(R.id.faveCount)TextView faveCount;
        @BindView(R.id.rtCount)TextView rtCount;
        @BindView(R.id.retweet)ImageView retweet;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Tweet tweet = mTweets.get(position);
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });

            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Tweet tweet = mTweets.get(position);
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });

            fave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Fave", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    Tweet tweet = mTweets.get(position);
                    client.favoriteTweet(Long.toString(tweet.uid), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "Tweet favorited", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e("FavoriteAction", responseString);
                        }
                    });
                }
            });

            retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Tweet tweet = mTweets.get(position);
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
        }
    }
}
