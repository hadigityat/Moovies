package com.can.moovies;

import java.util.List;

public class MoviesResponse {
    private List<MovieItem> results;
    private int total_pages;

    public List<MovieItem> getResults() {
        return results;
    }

    public int getTotalPages() { return total_pages;}

}