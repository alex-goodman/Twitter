package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private EditText etCompose;
    private Button btTweet;
    private Tweet replyTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // will be null if not a reply
        replyTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));


        client = TwitterApp.getRestClient(this);

        etCompose = (EditText) findViewById(R.id.etCompose);
        btTweet = (Button) findViewById(R.id.btTweet);

        // if the tweet is a reply to another
        if (replyTweet != null) {
            String displayText = "@" + replyTweet.user.screenName + " ";
            etCompose.setText(displayText);
            etCompose.setSelection(displayText.length());
        }
    }

    public void onSubmit(View view) {
        String newTweet = etCompose.getText().toString();
        String replyTo = null;
        if (replyTweet != null) replyTo = Long.toString(replyTweet.uid);

        // post the new tweet through and API POST request
        client.postNewTweet(newTweet, replyTo, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    Log.i("POST Tweet", "Successfully created new tweet");
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("POST Tweet", "Error parsing JSON response from posting");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("POST Tweet", responseString);
            }
        });
    }
}
