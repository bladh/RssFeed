package se.tedsys.rssfeed;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class FeedResultReceiver extends ResultReceiver {

    private final Receiver mReceiver;

    public FeedResultReceiver(Handler handler, Receiver receiver) {
        super(handler);
        this.mReceiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
