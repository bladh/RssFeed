package se.tedsys.rssfeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FeedFragment.FeedFragmentListener,
        FeedResultReceiver.Receiver {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DOWNLOADED_STATE_TAG = "downloaded_state_tag";
    private ResultReceiver mReceiver;
    private FeedFragment mFragment;
    private boolean mDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (FeedFragment) getFragmentManager().findFragmentById(R.id.feed_fragment);
        if (savedInstanceState != null) {
            mDownloaded = savedInstanceState.getBoolean(DOWNLOADED_STATE_TAG, false);
        }
        mReceiver = new FeedResultReceiver(new Handler(), this);
        if (!mDownloaded) {
            RssRetrieverService.requestFeed(this, mReceiver);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0) {
            ArrayList<FeedItem> items = resultData.getParcelableArrayList(getString(R.string.feed_results));
            if (items != null) {
                mDownloaded = true;
                if (mFragment != null) {
                    mFragment.updateItems(items);
                }
            }
        } else {
            String error = resultData.getString(getString(R.string.feed_error));
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DOWNLOADED_STATE_TAG, mDownloaded);
    }

    @Override
    public void onFeedItemClicked(FeedItem item) {
        Intent browser = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(item.link));
        startActivity(browser);
    }
}
