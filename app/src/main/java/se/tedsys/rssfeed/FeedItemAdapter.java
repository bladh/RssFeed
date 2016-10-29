package se.tedsys.rssfeed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class FeedItemAdapter extends RecyclerView.Adapter<FeedItemAdapter.ViewHolder>
        implements ViewHolderClickListener {

    private List<FeedItem> mDataset;
    final private AdapterListener mListener;

    public void updateDataSet(List<FeedItem> items) {
        mDataset = items;
        notifyDataSetChanged();
    }

    public FeedItemAdapter(List<FeedItem> dataset, AdapterListener listener) {
        mDataset = dataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(v, this);
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

    @Override
    public void onViewHolderClicked(int index) {
        FeedItem item = mDataset.get(index);
        mListener.feedItemClicked(item);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle;
        TextView mAuthor;
        TextView mDate;
        ViewHolderClickListener clickListener;

        public ViewHolder(View v, ViewHolderClickListener listener) {
            super(v);
            v.setOnClickListener(this);
            clickListener = listener;
            mTitle = (TextView) itemView.findViewById(R.id.card_title);
            mAuthor = (TextView) itemView.findViewById(R.id.card_author);
            mDate = (TextView) itemView.findViewById(R.id.card_date);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onViewHolderClicked(getAdapterPosition());
            }
        }
    }

    interface AdapterListener {
        void feedItemClicked(FeedItem item);
    }
}
