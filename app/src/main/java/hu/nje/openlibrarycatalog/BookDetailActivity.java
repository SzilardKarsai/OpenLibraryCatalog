package hu.nje.openlibrarycatalog;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        ImageView imageCover      = findViewById(R.id.detailImageCover);
        TextView textTitle        = findViewById(R.id.detailTextTitle);
        TextView textAuthor       = findViewById(R.id.detailTextAuthor);
        TextView textYear         = findViewById(R.id.detailTextYear);
        TextView textFavorite     = findViewById(R.id.detailTextFavorite);
        TextView textCoverUrlInfo = findViewById(R.id.detailTextCoverUrl);

        // Extrák olvasása – ezeket az adapter adja át Intenttel
        String title  = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String year   = getIntent().getStringExtra("year");
        String cover  = getIntent().getStringExtra("coverUrl");

        // Alap adatok kiírása
        textTitle.setText(title != null ? title : "-");
        textAuthor.setText(author != null ? author : "-");
        textYear.setText(year != null ? year : "-");

        if (cover != null && !cover.isEmpty()) {
            Glide.with(this)
                    .load(cover)
                    .into(imageCover);
            textCoverUrlInfo.setText(cover);
        } else {
            imageCover.setImageResource(R.drawable.ic_launcher_background);
            textCoverUrlInfo.setText("Nincs elérhető borító URL.");
        }

        // 🔥 Kedvenc státusz kiírása (ugyanazzal az ID-logikával, mint az adapterben)
        hu.nje.openlibrarycatalog.FavoritesStorage favoritesStorage =
                new hu.nje.openlibrarycatalog.FavoritesStorage(this);

        String safeTitle  = title  == null ? "" : title;
        String safeAuthor = author == null ? "" : author;
        String safeYear   = year   == null ? "" : year;
        String safeCover  = cover  == null ? "" : cover;

        String bookId = safeTitle + "|" + safeAuthor + "|" + safeYear + "|" + safeCover;

        boolean isFav = favoritesStorage.isFavorite(bookId);
        textFavorite.setText(isFav ? "Igen" : "Nem");
    }
}
