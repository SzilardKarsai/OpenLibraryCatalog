package hu.nje.openlibrarycatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import hu.nje.openlibrarycatalog.FavoritesStorage;
import hu.nje.openlibrarycatalog.databinding.FragmentSearchBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import hu.nje.openlibrarycatalog.R;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private BookAdapter adapter;
    private FavoritesStorage favoritesStorage;
    private String lastQuery = null;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Kedvencek tároló + adapter
        favoritesStorage = new FavoritesStorage(requireContext());
        adapter = new BookAdapter(favoritesStorage);

        // 🔹 RecyclerView beállítása
        binding.recyclerViewBooks.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.recyclerViewBooks.setAdapter(adapter);

        // 🔹 Kattintás: részletes nézet indítása
        adapter.setOnItemClickListener(item -> {
            Bundle args = new Bundle();
            args.putString("title", item.getTitle());
            args.putString("author", item.getAuthor());
            args.putString("year", item.getYear());
            args.putString("coverUrl", item.getCoverUrl());
            args.putString("workId", item.getWorkId());

            Navigation.findNavController(view)
                    .navigate(R.id.bookDetailFragment, args);
        });


        // Üres állapot
        binding.textEmptyState.setText("Kezdj el keresni az Open Library-ben!");
        binding.textEmptyState.setVisibility(View.VISIBLE);

        // 🔹 SearchView eseménykezelő
        binding.searchViewBooks.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        binding.searchViewBooks.clearFocus();
                        if (query != null && !query.trim().isEmpty()) {
                            lastQuery = query.trim();
                            searchBooks(lastQuery);
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding == null) return;

        String q = binding.searchViewBooks.getQuery() != null
                ? binding.searchViewBooks.getQuery().toString().trim()
                : "";

        // Ha van beírt keresés (pl. "Harry Potter"), frissítsük a találatokat
        if (!q.isEmpty()) {
            binding.searchViewBooks.clearFocus();

            // Ne indítsuk el feleslegesen duplán ugyanazt a keresést
            if (lastQuery == null || !lastQuery.equals(q)) {
                lastQuery = q;
            }

            // csak akkor indítsuk, ha nincs épp folyamatban kérés
            if (!isLoading) {
                searchBooks(lastQuery);
            }
        }
    }

    //Keresés
    public void searchBooks(String query) {
        if (isLoading) return;
        isLoading = true;

        binding.textEmptyState.setText("Keresés folyamatban...");
        binding.textEmptyState.setVisibility(View.VISIBLE);

        RetrofitClient.getApi().SearchBooks(query).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call,
                                   Response<SearchResponse> response) {

                isLoading = false;

                if (!isAdded()) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().docs != null) {

                    List<BookItem> resultList = new ArrayList<>();

                    for (Doc doc : response.body().docs) {

                        // Cím
                        String title = (doc.title != null) ? doc.title : "Cím nélkül";

                        // Szerző
                        String author = "Ismeretlen szerző";
                        if (doc.authorName != null && !doc.authorName.isEmpty()) {
                            author = doc.authorName.get(0);
                        }

                        // Év
                        String year = "-";
                        if (doc.firstPublishYear != null) {
                            year = String.valueOf(doc.firstPublishYear);
                        }

                        // Borító URL
                        String coverUrl = null;
                        if (doc.coverId != null) {
                            coverUrl = "http://covers.openlibrary.org/b/id/"
                                    + doc.coverId + "-M.jpg";
                        }

                        //workId: doc.key (pl. "/works/OL45883W")
                        resultList.add(
                                new BookItem(title, author, year, coverUrl, doc.key)
                        );
                    }

                    if (!resultList.isEmpty()) {
                        adapter.setItems(resultList);
                        binding.textEmptyState.setVisibility(View.GONE);
                    } else {
                        adapter.setItems(new ArrayList<>());
                        binding.textEmptyState.setText("Nincs találat.");
                        binding.textEmptyState.setVisibility(View.VISIBLE);
                    }

                } else {
                    adapter.setItems(new ArrayList<>());
                    binding.textEmptyState.setText("Nem sikerült beolvasni az adatokat.");
                    binding.textEmptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                isLoading = false;

                if (!isAdded()) return;

                adapter.setItems(new ArrayList<>());
                binding.textEmptyState.setText("Hiba történt a keresés során.");
                binding.textEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
