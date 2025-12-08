package hu.nje.openlibrarycatalog.ui;

public class BookItem {
    private String title;
    private String author;
    private String year;
    private String coverUrl;

    public BookItem(String title, String author, String year, String coverUrl) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.coverUrl = coverUrl;
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
