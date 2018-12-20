package com.can.moovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieListFragment.OnListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MOVIE_LIST_TYPE = "type";

    private MovieListType mMovieListType;
    private RecyclerView mListView;
    private ProgressBar mProgressBar;
    MoviesAPIService mService;
    private int mPage = 1;
    private List<MovieItem> mList;
    private MoviesListAdapter mAdapter;
    private int mTotalPages;
    private LinearLayout mErrorLayout;
    private Button mRetryButton;
    private Retrofit mRetrofit;

    private OnListFragmentInteractionListener mListener;

    public MovieListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieListType MovieListType.
     * @return A new instance of fragment MovieListFragment.
     */
    public static MovieListFragment newInstance(MovieListType movieListType) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_LIST_TYPE, movieListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieListType = (MovieListType) getArguments().getSerializable(MOVIE_LIST_TYPE);
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.MOVIES_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mListView = v.findViewById(R.id.movie_list_view);
        mProgressBar = v.findViewById(R.id.progress_circular);
        mErrorLayout = v.findViewById(R.id.error_layout);
        mRetryButton = v.findViewById(R.id.retry_button);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                mPage = 1;
                downloadPopularMovies();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mListView.setLayoutManager(layoutManager);

        //Avoid unnecessary layout passes by setting setHasFixedSize to true when changing the
        // contents of the adapter does not change it's height or the width.
        mListView.setHasFixedSize(true);

        Log.e("C_A_N", "list on create view, list type: " + mMovieListType );

        mService = mRetrofit.create(MoviesAPIService.class);
        switch (mMovieListType)
        {
            case POPULAR:
                mPage = 1;
                downloadPopularMovies();
        }

        return v;
    }

    void downloadPopularMovies()
    {
        mService.getPopularMovies(Constants.API_KEY, Constants.MOVIES_SORT_BY_POPULARITY, mPage)
                .enqueue(new Callback<MoviesResponse>() {

                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        MoviesResponse moviesResponse = response.body();
                        Log.e("C_A_N", "movies response: " + moviesResponse);

                        if (moviesResponse != null && moviesResponse.getResults() != null) {
                            if (mPage == 1) {
                                mProgressBar.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                                mTotalPages = moviesResponse.getTotalPages();

                                mList = moviesResponse.getResults();
                                mAdapter = new MoviesListAdapter(getActivity(), mList, mListView);
                                mListView.setAdapter(mAdapter);
                                mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                    @Override
                                    public void onLoadMore() {
                                        if(mPage < mTotalPages) {
                                            downloadPopularMovies();
                                        }
                                    }
                                });

                            } else {
                                mList.addAll(moviesResponse.getResults());
                                mAdapter.setLoaded();
                                mAdapter.notifyDataSetChanged();
                            }
                            mPage++;

                        } else onFailure(null, null);
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        Log.e("MovieListFragment", "onFailure");
                        mProgressBar.setVisibility(View.GONE);
                        showErrorScreen();
                    }
                });
    }

    void showErrorScreen()
    {
        mListView.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListItemSelected(MovieItem item);
    }
}
