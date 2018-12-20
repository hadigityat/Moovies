package com.can.moovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    private static final String ARG_MOVIE_ITEM = "item";

    private MovieItem mMovieItem;
    private String mTrailerLink;
    private TextView mTrailer;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param item MovieItem.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    public static MovieDetailsFragment newInstance(MovieItem item) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovieItem = (MovieItem) getArguments().getSerializable(ARG_MOVIE_ITEM);
        }
        if(mMovieItem != null) {
            getTrailerLink(mMovieItem.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_details, container, false);

        final ImageView poster = v.findViewById(R.id.poster_image);

        Picasso.get()
                .load(Constants.IMAGE_BASE_URL + mMovieItem.getBackdrop())
                .placeholder(R.drawable.progress_anim)
                .into(poster);


        TextView title = v.findViewById(R.id.title);
        TextView overview = v.findViewById(R.id.overview);
        TextView rating = v.findViewById(R.id.rating);

        title.setText(mMovieItem.getTitle());
        String synopsis = getText(R.string.movie_overview) + " " + mMovieItem.getOverview();
        overview.setText(synopsis);
        String averageRating = getText(R.string.movie_rating) + " " + mMovieItem.getRating();
        rating.setText(averageRating);

        mTrailer = v.findViewById(R.id.trailer);

        return v;
    }

    private void getTrailerLink(int id)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MOVIES_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesAPIService service = retrofit.create(MoviesAPIService.class);

        service.getTrailers(id, Constants.API_KEY).enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                TrailersResponse trailersResponse = response.body();
                if(trailersResponse != null && trailersResponse.getTrailers() != null)
                {
                    List<TrailerItem> trailerItems = trailersResponse.getTrailers();
                    for(int i = 0; i < trailerItems.size();i++) {
                        if (trailerItems.get(i).getType().equals(Constants.MOVIE_TYPE_TRAILER)) {
                             mTrailerLink = trailerItems.get(i).getSource();
                            if (mTrailerLink != null) {
                                mTrailer.setVisibility(View.VISIBLE);
                                mTrailer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mTrailerLink));
                                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://www.youtube.com/watch?v=" + mTrailerLink));
                                        if (getContext() != null) {
                                            try {
                                                getContext().startActivity(appIntent);
                                            } catch (ActivityNotFoundException ex) {
                                                getContext().startActivity(webIntent);
                                            }
                                        }
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
                else onFailure(null, null);
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                Log.e("MovieDetailsFragment", "onFailure ");

                //won't display trailer
            }
        });
    }
}


