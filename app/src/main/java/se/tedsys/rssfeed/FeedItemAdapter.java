package se.tedsys.rssfeed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.ViewHolder> {

    private List<FeedItem> mDataset;

    public void updateDataSet(List<FeedItem> items) {
        mDataset = items;
        notifyDataSetChanged();
    }

    public FeedItemAdapter(List<FeedItem> dataset) {
        mDataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FeedItem item = mDataset.get(position);
        holder.mTitle.setText(item.title);
        holder.mAuthor.setText(item.author);
        holder.mDate.setText(item.publishedDate);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTitle;
        TextView mAuthor;
        TextView mDate;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) itemView.findViewById(R.id.card_title);
            mAuthor = (TextView) itemView.findViewById(R.id.card_author);
            mDate = (TextView) itemView.findViewById(R.id.card_date);
        }
    }
}
