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

import java.util.List;
import java.util.ArrayList;

import hu.nje.openlibrarycatalog.databinding.FragmentSearchBinding;
import hu.nje.openlibrarycatalog.ui.Doc;
import hu.nje.openlibrarycatalog.ui.RetrofitClient;
import hu.nje.openlibrarycatalog.ui.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private BookAdapter adapter;

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

        // RecyclerView beállítása
        adapter = new BookAdapter();
        binding.recyclerViewBooks.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        binding.recyclerViewBooks.setAdapter(adapter);

        // Üres állapot
        binding.textEmptyState.setText("Kezdj el keresni az Open Library-ben!");
        binding.textEmptyState.setVisibility(View.VISIBLE);

        // SearchView eseménykezelő
        binding.searchViewBooks.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        binding.searchViewBooks.clearFocus();
                        if (query != null && !query.trim().isEmpty()) {
                            searchBooks(query.trim());
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
    }
    // Keresés
    public void searchBooks(String query) {
        // Keresés folyamatban jelzése
        binding.textEmptyState.setText("Keresés folyamatban...");
        binding.textEmptyState.setVisibility(View.VISIBLE);

        // Retrofit hívás
        RetrofitClient.getApi().SearchBooks(query).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().docs != null) {
                    List<BookItem> resultList = new ArrayList<>();
                    for (Doc doc : response.body().docs) {

                        // Cím
                        String title = doc.title != null
                                ? doc.title
                                : "Cím nélkül";

                        // Szerző (ha több van, az elsőt vesszük)
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
                            coverUrl = "https://covers.openlibrary.org/b/id/"
                                    + doc.coverId + "-M.jpg";
                        }

                        resultList.add(
                                new BookItem(title, author, year, coverUrl)
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
                    // Sikertelen válasz vagy üres
                    adapter.setItems(new ArrayList<>());;
                    binding.textEmptyState.setText("Nem sikerült beolvasni az adatoakt");
                    binding.textEmptyState.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                if (!isAdded()) return;

                adapter.setItems(new ArrayList<>());
                binding.textEmptyState.setText("Hiba történt a keresés során.");
                binding.textEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    // Binding nullázása
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}