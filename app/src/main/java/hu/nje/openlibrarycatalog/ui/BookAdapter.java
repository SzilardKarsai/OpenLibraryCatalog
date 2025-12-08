package hu.nje.openlibrarycatalog.ui;

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

import hu.nje.openlibrarycatalog.R;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<BookItem> items = new ArrayList<>();

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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView imageCover;
        TextView textTitle;
        TextView textAuthor;
        TextView textYear;
        ImageButton buttonFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.imageCover);
            textTitle = itemView.findViewById(R.id.textTitle);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textYear = itemView.findViewById(R.id.textYear);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }
    }
}