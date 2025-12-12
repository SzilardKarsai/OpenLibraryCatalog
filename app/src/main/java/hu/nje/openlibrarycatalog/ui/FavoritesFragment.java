package hu.nje.openlibrarycatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import hu.nje.openlibrarycatalog.FavoritesStorage;
import hu.nje.openlibrarycatalog.R;

public class FavoritesFragment extends Fragment {

    private BookAdapter adapter;
    private FavoritesStorage favoritesStorage;
    private View emptyText;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritesStorage = new FavoritesStorage(requireContext());

        RecyclerView recycler = view.findViewById(R.id.recyclerFavorites);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new BookAdapter(favoritesStorage);
        recycler.setAdapter(adapter);

        emptyText = view.findViewById(R.id.textFavoritesEmpty);
        adapter.setOnFavoritesChangedListener(() -> loadFavorites());

        // részletes nézet indítása kedvencekből is
        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putString("title", item.getTitle());
            args.putString("author", item.getAuthor());
            args.putString("year", item.getYear());
            args.putString("coverUrl", item.getCoverUrl());
            args.putString("workId", item.getWorkId());

            // Navigálás a BookDetailFragmentre
            Navigation.findNavController(view)
                    .navigate(R.id.bookDetailFragment, args);
        });

        loadFavorites();
    }

    private void loadFavorites() {
        Set<String> favoriteIds = favoritesStorage.getAllFavorites();

        if (favoriteIds.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            adapter.setItems(new ArrayList<>());
            return;
        }

        emptyText.setVisibility(View.GONE);

        List<BookItem> favoriteItems = new ArrayList<>();

        for (String id : favoriteIds) {

            // id = title|author|year|coverUrl|workId
            String[] parts = id.split("\\|", 5);

            if (parts.length < 4) continue; // nagyon régi, sérült adat

            String title  = parts[0];
            String author = parts[1];
            String year   = parts[2];
            String cover  = parts[3];
            String workId = (parts.length == 5 && !parts[4].isEmpty()) ? parts[4] : null;

            BookItem item = new BookItem(title, author, year, cover, workId);
            item.setFavorite(true);
            favoriteItems.add(item);
        }

        adapter.setItems(favoriteItems);
    }
}
