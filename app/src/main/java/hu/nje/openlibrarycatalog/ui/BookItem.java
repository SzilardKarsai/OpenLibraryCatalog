package hu.nje.openlibrarycatalog.ui;

public class BookItem {
    private String title;
    private String author;
    private String year;
    private String coverUrl;


    private String workId;
    private boolean isFavorite = false;

    public BookItem(String title, String author, String year, String coverUrl, String workId) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.coverUrl = coverUrl;
        this.workId = workId;
    }

    public String getWorkId() {
        return workId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getYear() {
        return year;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
