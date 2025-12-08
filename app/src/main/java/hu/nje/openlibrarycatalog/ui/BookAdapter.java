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

    public interface OnItemClickListener {
        void onItemClick(BookItem item);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public BookAdapter(FavoritesStorage favoritesStorage) {
        this.favoritesStorage = favoritesStorage;
    }

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

        holder.textTitle.setText(item.getTitle());
        holder.textAuthor.setText(item.getAuthor());
        holder.textYear.setText(item.getYear());

        if (item.getCoverUrl() != null) {
            Glide.with(holder.imageCover.getContext())
                    .load(item.getCoverUrl())
                    .into(holder.imageCover);
        } else {
            holder.imageCover.setImageResource(R.drawable.ic_launcher_background);
        }

        // bookId: title|author|year|coverUrl|workId
        String safeCover  = item.getCoverUrl() == null ? "" : item.getCoverUrl();
        String safeWorkId = item.getWorkId()   == null ? "" : item.getWorkId();

        String bookId =
                item.getTitle() + "|" +
                        item.getAuthor() + "|" +
                        item.getYear()   + "|" +
                        safeCover        + "|" +
                        safeWorkId;

        boolean isFav = favoritesStorage.isFavorite(bookId);
        item.setFavorite(isFav);

        if (item.isFavorite()) {
            holder.favoriteIcon.setVisibility(INVISIBLE);
            holder.favoriteIconOn.setVisibility(VISIBLE);
        } else {
            holder.favoriteIconOn.setVisibility(INVISIBLE);
            holder.favoriteIcon.setVisibility(VISIBLE);
        }

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

            favoritesStorage.setFavorite(bookId, newState);
        };

        holder.favoriteIcon.setOnClickListener(favClickListener);
        holder.favoriteIconOn.setOnClickListener(favClickListener);

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

    static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageButton favoriteIcon;
        ImageButton favoriteIconOn;
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
