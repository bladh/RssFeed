package se.tedsys.rssfeed;

import android.os.Parcel;
import android.os.Parcelable;

public class FeedItem implements Parcelable {
    public static final Parcelable.Creator<FeedItem> CREATOR
            = new Parcelable.Creator<FeedItem>() {
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
    public final String title;
    public final String publishedDate;
    public final String id;
    public final String link;

    public FeedItem(String title, String publishedDate, String id, String link) {
        this.title = title;
        this.publishedDate = publishedDate;
        this.id = id;
        this.link = link;
    }

    private FeedItem(Parcel in) {
        title = in.readString();
        publishedDate = in.readString();
        id = in.readString();
        link = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(publishedDate);
        out.writeString(id);
        out.writeString(link);
    }
}
