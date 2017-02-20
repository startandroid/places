package ru.startandroid.places.web;


import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.startandroid.places.web.data.SearchResponse;
import rx.Observable;

public interface ApiService {

    @GET("discover/search")
    Observable<SearchResponse> search(@Query("at") String coordinates, @Query("q") String query, @Query("size") int size);

}
