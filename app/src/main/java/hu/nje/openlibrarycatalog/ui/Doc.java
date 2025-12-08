package hu.nje.openlibrarycatalog.ui;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Doc {

    public String title;
    @SerializedName("author_name")
    public List<String> authorName;
    @SerializedName("first_publish_year")
    public Integer firstPublishYear;
    @SerializedName("cover_i")
    public Integer coverId;
}
