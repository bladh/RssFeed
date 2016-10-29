package se.tedsys.rssfeed;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import junit.framework.Assert;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The RssRetrieverService will retrieve and parse the xml from {@link #DEFAULT_URL}.
 */
public class RssRetrieverService extends IntentService {
    private static final String TAG = RssRetrieverService.class.getSimpleName();
    private static final String ACTION_GET = "se.tedsys.rssfeed.action.GET";
    private static final String TAG_RECEIVER = "TAG_RECEIVER";
    private static final String DEFAULT_URL = "http://www.theverge.com/google/rss/index.xml";
    private static final String NAMESPACE = null;

    private ResultReceiver mReceiver;
    private XmlPullParser mParser;
    private boolean mParsing;

    public RssRetrieverService() {
        super("RssRetrieverService");
        try {
            final XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            mParser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Could not create xml pull parser", e);
        }
        mParsing = false;
    }

    public static void requestFeed(Context context, ResultReceiver receiver) {
        final Intent intent = new Intent(context, RssRetrieverService.class);
        intent.putExtra(TAG_RECEIVER, receiver);
        intent.setAction(ACTION_GET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET.equals(action) && !mParsing) {
                Log.d(TAG, "Activity requests feed");
                mReceiver = intent.getParcelableExtra(TAG_RECEIVER);
                parseXml();
                mParsing = true;
            }
        }
    }

    private void parseXml() {
        if (mParser == null) {
            Log.e(TAG, "Cannot parse XML because the parser is null");
            return;
        }

        final ArrayList<FeedItem> items = new ArrayList<>();
        try (InputStream stream = getUrlStream(DEFAULT_URL)) {
            mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            mParser.setInput(stream, NAMESPACE);
            mParser.nextTag();
            items.addAll(readFeed(mParser));
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing XML", e);
        }

        if (mReceiver != null) {
            final Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(getString(R.string.feed_results), items);
            mReceiver.send(0, bundle);
        }
    }

    private List<FeedItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<FeedItem> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private FeedItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "entry");
        String title = null;
        String id = null;
        String link = null;
        String date = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            final String name = parser.getName();
            switch (name) {
                case "title":
                    title = readTag(parser, "title");
                    break;
                case "id":
                    id = readTag(parser, "id");
                    break;
                case "link":
                    link = readLink(parser);
                    break;
                case "published":
                    date = readTag(parser, "published");
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new FeedItem(title, date, id, link);
    }

    private String readTag(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NAMESPACE, tag);
        final String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, NAMESPACE, tag);
        return text;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, NAMESPACE, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")) {
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, NAMESPACE, "link");
        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private InputStream getUrlStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }
}
