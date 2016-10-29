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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mReceiver = new FeedResultReceiver(new Handler(), this);
        RssRetrieverService.requestFeed(this, mReceiver);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        List<FeedItem> items = resultData.getParcelableArrayList(getString(R.string.feed_results));
        if (items != null) {
            Log.d(TAG, "Got " + items.size() + " items!");
            for (FeedItem item : items) {
                Log.d(TAG, item.title);
            }
        }
    }
}
