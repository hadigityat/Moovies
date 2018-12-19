package com.can.moovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter {

    Context mContext;
    MovieListFragment.OnListFragmentInteractionListener mListener;
    List<MovieItem> mItems;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int mVisibleThreshold = 50;
    private int mLastVisibleItem, mTotalItemCount;
    private boolean mLoading;


    public MoviesListAdapter(Activity activity, List<MovieItem> listItems, RecyclerView recyclerView)
    {
        mContext = activity;
        mListener = (MovieListFragment.OnListFragmentInteractionListener) activity;
        mItems = listItems;

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {

            final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            mTotalItemCount = layoutManager.getItemCount();
                            mLastVisibleItem = layoutManager
                                    .findLastVisibleItemPosition();

                            if (!mLoading
                                    && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {

                                // End has been reached
                                if (mOnLoadMoreListener != null) {
                                    mOnLoadMoreListener.onLoadMore();
                                }
                                mLoading = true;
                            }
                        }
                    });
        }
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, null, false);
        return new MoviesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MoviesViewHolder holder = (MoviesViewHolder) viewHolder;

        MovieItem item = mItems.get(i);
        //Log.e("C_A_N", Constants.IMAGE_BASE_URL + item.getImageURL());
        Picasso.get()
                .load(Constants.IMAGE_BASE_URL + item.getImageURL())
                .placeholder(R.drawable.progress_anim)
                .into(holder.imageView);


    }

    public void setLoaded() {
        mLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_image);
        }
    }
}


