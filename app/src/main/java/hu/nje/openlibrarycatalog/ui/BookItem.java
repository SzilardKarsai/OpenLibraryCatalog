package hu.nje.openlibrarycatalog.ui;

public class BookItem {
    private String title;
    private String author;
    private String year;
    private String coverUrl;

    private int key;
    //bool változó
    private boolean isFavorite = false;
    public BookItem(String title, String author, String year, String coverUrl) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.coverUrl = coverUrl;
    }
    public int getId() {
        return key; // vagy amiben az OpenLibrary ID van
        // vagy vészmegoldásként: return title + "|" + author;
    }


    public boolean isFavorite(){
        return isFavorite;
    }

    public void setFavorite(boolean favorite){
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
