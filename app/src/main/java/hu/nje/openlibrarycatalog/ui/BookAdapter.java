package hu.nje.openlibrarycatalog.ui;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.nje.openlibrarycatalog.FavoritesStorage;
import hu.nje.openlibrarycatalog.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<BookItem> items = new ArrayList<>();
    private final FavoritesStorage favoritesStorage;

    // A részletes nézethez szükséges kattintás-listener
    public interface OnItemClickListener {
        void onItemClick(BookItem item);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // konstruktor
    public BookAdapter(FavoritesStorage favoritesStorage) {
        this.favoritesStorage = favoritesStorage;
    }

    // Lista frissítése
    public void setItems(List<BookItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        BookItem item = items.get(position);

        // Alap adatok kiírása
        holder.textTitle.setText(item.getTitle());
        holder.textAuthor.setText(item.getAuthor());
        holder.textYear.setText(item.getYear());

        //  Borító betöltése
        if (item.getCoverUrl() != null) {
            Glide.with(holder.imageCover.getContext())
                    .load(item.getCoverUrl())
                    .into(holder.imageCover);
        } else {
            holder.imageCover.setImageResource(R.drawable.ic_launcher_background);
        }

        // Egyedi azonosító a könyvnek (kedvencekhez)
        String safeCover = item.getCoverUrl() == null ? "" : item.getCoverUrl();
        String bookId = item.getTitle() + "|" + item.getAuthor() + "|" + item.getYear() + "|" + safeCover;

        // Kedvencek tárolóból beolvassuk, hogy ez kedvenc-e
        boolean isFav = favoritesStorage.isFavorite(bookId);
        item.setFavorite(isFav);

        //Ikonok megjelenítése a kedvenc státusz alapján
        if (item.isFavorite()) {
            holder.favoriteIcon.setVisibility(INVISIBLE);
            holder.favoriteIconOn.setVisibility(VISIBLE);
        } else {
            holder.favoriteIconOn.setVisibility(INVISIBLE);
            holder.favoriteIcon.setVisibility(VISIBLE);
        }

        // Kattintás a kedvencre
        View.OnClickListener favClickListener = v -> {
            boolean newState = !item.isFavorite();
            item.setFavorite(newState);

            if (newState) {
                holder.favoriteIcon.setVisibility(INVISIBLE);
                holder.favoriteIconOn.setVisibility(VISIBLE);
            } else {
                holder.favoriteIconOn.setVisibility(INVISIBLE);
                holder.favoriteIcon.setVisibility(VISIBLE);
            }

            // Mentés SharedPreferences-be
            favoritesStorage.setFavorite(bookId, newState);
        };

        holder.favoriteIcon.setOnClickListener(favClickListener);
        holder.favoriteIconOn.setOnClickListener(favClickListener);

        // A teljes sor kattintása át dob a részletes nézetre
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // A ViewHolder cache-eli a sor elemeit (performance miatt)
    static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageButton favoriteIcon;      // üres csillag
        ImageButton favoriteIconOn;    // teli csillag
        ImageView imageCover;
        TextView textTitle;
        TextView textAuthor;
        TextView textYear;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            imageCover     = itemView.findViewById(R.id.imageCover);
            textTitle      = itemView.findViewById(R.id.textTitle);
            textAuthor     = itemView.findViewById(R.id.textAuthor);
            textYear       = itemView.findViewById(R.id.textYear);
            favoriteIcon   = itemView.findViewById(R.id.buttonFavorite2);
            favoriteIconOn = itemView.findViewById(R.id.buttonFavoriteOn);
        }
    }
}
