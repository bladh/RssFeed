package se.tedsys.rssfeed;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private static final String TAG = FeedFragment.class.getSimpleName();
    private FeedFragmentListener mListener;
    private RecyclerView mRecyclerView;
    private FeedItemAdapter mAdapter;
    private List<FeedItem> mDataset;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedFragment() {
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //set up fragment after saved parameters
        }
        mDataset = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_feed, container, false);
        mRecyclerView = (RecyclerView) mainView.findViewById(R.id.recycler_view);
        mAdapter = new FeedItemAdapter(mDataset);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        return mainView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FeedFragmentListener) {
            mListener = (FeedFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateItems(List<FeedItem> items) {
        Log.d(TAG, "Received " + items.size() + " new items!");
        mDataset = items;
        mAdapter.updateDataSet(items);
    }

    public interface FeedFragmentListener {
        //no methods yet
    }
}
