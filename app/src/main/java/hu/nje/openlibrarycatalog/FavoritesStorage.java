package hu.nje.openlibrarycatalog;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FavoritesStorage {

    private static final String PREF_NAME = "favorites_prefs";
    private static final String KEY_SET = "favorites_set";

    private final SharedPreferences prefs;

    public FavoritesStorage(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private Set<String> getFavoriteSetInternal() {
        Set<String> stored = prefs.getStringSet(KEY_SET, Collections.emptySet());
        return new HashSet<>(stored); // mindig másolatot adunk vissza
    }

    public boolean isFavorite(String id) {
        return getFavoriteSetInternal().contains(id);
    }

    public void setFavorite(String id, boolean favorite) {
        Set<String> set = getFavoriteSetInternal();
        if (favorite) {
            set.add(id);
        } else {
            set.remove(id);
        }
        prefs.edit().putStringSet(KEY_SET, set).apply();
    }

    public Set<String> getAllFavorites() {
        return getFavoriteSetInternal();
    }
}
