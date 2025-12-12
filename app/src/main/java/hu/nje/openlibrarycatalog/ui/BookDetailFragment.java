package hu.nje.openlibrarycatalog.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hu.nje.openlibrarycatalog.R;
public class BookDetailFragment extends Fragment {

    private ImageView imageCover;
    private TextView textTitle;
    private TextView textAuthor;
    private TextView textYear;
    private TextView textDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_book_detail, container, false);

        imageCover     = root.findViewById(R.id.detailImageCover);
        textTitle      = root.findViewById(R.id.detailTextTitle);
        textAuthor     = root.findViewById(R.id.detailTextAuthor);
        textYear       = root.findViewById(R.id.detailTextYear);
        textDescription= root.findViewById(R.id.detailTextDescription);

        Bundle args = getArguments();

        String title  = args != null ? args.getString("title") : null;
        String author = args != null ? args.getString("author") : null;
        String year   = args != null ? args.getString("year") : null;
        String cover  = args != null ? args.getString("coverUrl") : null;
        String workId = args != null ? args.getString("workId") : null;

        textTitle.setText(title  != null ? title  : "-");
        textAuthor.setText(author != null ? author : "-");
        textYear.setText(year   != null ? year   : "-");

        // BORÍTÓ KÉP BETÖLTÉSE
        if (cover != null && !cover.isEmpty()) {
            Glide.with(this)
                    .load(cover)
                    .into(imageCover);
        } else {
            imageCover.setImageResource(R.drawable.ic_launcher_background);
        }

        // LEÍRÁS BETÖLTÉSE
        if (workId != null && !workId.isEmpty()) {
            loadDescription(workId, textDescription, title, author, year);
        } else {
            textDescription.setText("Ehhez a könyvhöz nem található leírás (nincs workId).");
        }

        return root;
    }

    private void loadDescription(String workId,
                                 TextView targetView,
                                 String titleFromArgs,
                                 String authorFromArgs,
                                 String yearFromArgs) {

        new Thread(() -> {
            try {
                // workId normalizálása (OLxxxxW esetén)
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

                if (json.has("description")) {
                    Object descObj = json.get("description");
                    if (descObj instanceof JSONObject) {
                        description = ((JSONObject) descObj).optString("value", null);
                    } else {
                        description = descObj.toString();
                    }
                }

                if (json.has("subjects")) {
                    StringBuilder sbSubjects = new StringBuilder();
                    for (int i = 0; i < json.getJSONArray("subjects").length(); i++) {
                        if (i > 0) sbSubjects.append(", ");
                        sbSubjects.append(json.getJSONArray("subjects").getString(i));
                    }
                    subjectsText = sbSubjects.toString();
                }

                if (description == null || description.trim().isEmpty()) {
                    String safeTitle  = (titleFromArgs != null && !titleFromArgs.isEmpty())
                            ? titleFromArgs
                            : "Ismeretlen cím";
                    String safeAuthor = (authorFromArgs != null && !authorFromArgs.isEmpty())
                            ? authorFromArgs
                            : "ismeretlen szerző";

                    StringBuilder auto = new StringBuilder();
                    auto.append("A(z) \"").append(safeTitle).append("\" című mű ");
                    auto.append(safeAuthor).append(" tollából származik. ");

                    if (yearFromArgs != null && !yearFromArgs.isEmpty()) {
                        auto.append("Megjelenés éve: ").append(yearFromArgs).append(". ");
                    }

                    auto.append("Az OpenLibrary sajnos nem tartalmaz részletes leírást ehhez a műhöz.");
                    description = auto.toString();
                }

                if (subjectsText != null && !subjectsText.isEmpty()) {
                    description = description + "\n\nTémák: " + subjectsText + ".";
                }

                String finalDescription = description;

                // Fragment-kompatibilis UI frissítés
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> targetView.setText(finalDescription));
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            targetView.setText("Hiba történt a leírás betöltésekor.")
                    );
                }
            }
        }).start();
    }
}
