package se.tedsys.rssfeed;

import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FeedFragment.FeedFragmentListener,
        FeedResultReceiver.Receiver {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ResultReceiver mReceiver;
    private FeedFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = (FeedFragment) getFragmentManager().findFragmentById(R.id.feed_fragment);
        mReceiver = new FeedResultReceiver(new Handler(), this);
        RssRetrieverService.requestFeed(this, mReceiver);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == 0) {
            List<FeedItem> items = resultData.getParcelableArrayList(getString(R.string.feed_results));
            if (items != null && mFragment != null) {
                mFragment.updateItems(items);
            }
        } else {
            String error = resultData.getString(getString(R.string.feed_error));
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }
}
