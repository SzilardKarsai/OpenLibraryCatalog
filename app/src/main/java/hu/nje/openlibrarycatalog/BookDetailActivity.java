package hu.nje.openlibrarycatalog;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_detail);

        ImageView imageCover      = findViewById(R.id.detailImageCover);
        TextView textTitle        = findViewById(R.id.detailTextTitle);
        TextView textAuthor       = findViewById(R.id.detailTextAuthor);
        TextView textYear         = findViewById(R.id.detailTextYear);
        TextView textDescription  = findViewById(R.id.detailTextDescription);

        // Intent extrák
        String title  = getIntent().getStringExtra("title");
        String author = getIntent().getStringExtra("author");
        String year   = getIntent().getStringExtra("year");
        String cover  = getIntent().getStringExtra("coverUrl");
        String workId = getIntent().getStringExtra("workId");

        textTitle.setText(title  != null ? title  : "-");
        textAuthor.setText(author != null ? author : "-");
        textYear.setText(year   != null ? year   : "-");

        // BORÍTÓ KÉP BETÖLTÉSE (ha van URL)
        if (cover != null && !cover.isEmpty()) {
            Glide.with(this)
                    .load(cover)
                    .into(imageCover);
        }

        // Kedvenc státusz (ugyanazzal az ID-vel, mint az adapterben)
        FavoritesStorage favoritesStorage = new FavoritesStorage(this);

        String safeTitle  = title  == null ? "" : title;
        String safeAuthor = author == null ? "" : author;
        String safeYear   = year   == null ? "" : year;
        String safeCover  = cover  == null ? "" : cover;
        String safeWorkId = workId == null ? "" : workId;

        String bookId = safeTitle + "|" + safeAuthor + "|" + safeYear + "|" + safeCover + "|" + safeWorkId;

        // LEÍRÁS BETÖLTÉSE
        if (workId != null && !workId.isEmpty()) {
            // → az onCreate-ben kiolvasott title/author/year értékeket átadjuk
            loadDescription(workId, textDescription, title, author, year);
        } else {
            textDescription.setText("Ehhez a könyvhöz nem található leírás (nincs workId).");
        }
    }


    private void loadDescription(String workId,
                                 TextView targetView,
                                 String titleFromIntent,
                                 String authorFromIntent,
                                 String yearFromIntent) {

        new Thread(() -> {
            try {
                // workId normalizálása, hogy akkor is jó legyen, ha csak "OLxxxxW" érkezik
                String normalizedWorkId = workId;
                if (!normalizedWorkId.startsWith("/")) {
                    normalizedWorkId = "/works/" + normalizedWorkId;
                }

                String urlString = "https://openlibrary.org" + normalizedWorkId + ".json";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONObject json = new JSONObject(sb.toString());

                String description = null;
                String subjectsText = null;

                // IGAZI ÖSSZEFOGLALÓ: description, ha van
                if (json.has("description")) {
                    Object descObj = json.get("description");
                    if (descObj instanceof JSONObject) {
                        description = ((JSONObject) descObj).optString("value", null);
                    } else {
                        description = descObj.toString();
                    }
                }

                // SUBJECTS CSAK KIEGÉSZÍTÉSNEK, NEM HELYETTESÍTÉSNEK
                if (json.has("subjects")) {
                    StringBuilder sbSubjects = new StringBuilder();
                    for (int i = 0; i < json.getJSONArray("subjects").length(); i++) {
                        if (i > 0) sbSubjects.append(", ");
                        sbSubjects.append(json.getJSONArray("subjects").getString(i));
                    }
                    subjectsText = sbSubjects.toString();
                }

                //HA NINCS DESCRIPTION → SAJÁT ÖSSZEFOGLALÓ
                if (description == null || description.trim().isEmpty()) {
                    String safeTitle  = (titleFromIntent  != null && !titleFromIntent.isEmpty())
                            ? titleFromIntent
                            : "Ismeretlen cím";
                    String safeAuthor = (authorFromIntent != null && !authorFromIntent.isEmpty())
                            ? authorFromIntent
                            : "ismeretlen szerző";

                    StringBuilder auto = new StringBuilder();
                    auto.append("A(z) \"").append(safeTitle).append("\" című mű ");
                    auto.append(safeAuthor).append(" tollából származik. ");

                    if (yearFromIntent != null && !yearFromIntent.isEmpty()) {
                        auto.append("Megjelenés éve: ").append(yearFromIntent).append(". ");
                    }

                    auto.append("Az OpenLibrary sajnos nem tartalmaz részletes leírást ehhez a műhöz.");
                    description = auto.toString();
                }

                // HA VAN SUBJECTS, CSAK HOZZÁFŰZZÜK A LEÍRÁSHOZ
                if (subjectsText != null && !subjectsText.isEmpty()) {
                    description = description
                            + "\n\nTémák: "
                            + subjectsText
                            + ".";
                }

                String finalDescription = description;
                runOnUiThread(() -> targetView.setText(finalDescription));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        targetView.setText("Hiba történt a leírás betöltésekor.")
                );
            }
        }).start();
    }

}
