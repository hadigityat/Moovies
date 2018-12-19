package com.can.moovies;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MovieListFragment.OnListFragmentInteractionListener {

    MovieListFragment mMovieListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Listing popular movies by default
        mMovieListFragment = MovieListFragment.newInstance(MovieListType.POPULAR);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_frame, mMovieListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onListItemSelected(MovieItem item) {

    }
}
