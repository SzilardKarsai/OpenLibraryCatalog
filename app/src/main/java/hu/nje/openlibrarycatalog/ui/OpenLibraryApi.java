package hu.nje.openlibrarycatalog.ui;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface OpenLibraryApi {

    @GET("search.json")
    Call<SearchResponse> SearchBooks(@Query("q") String query);
}
