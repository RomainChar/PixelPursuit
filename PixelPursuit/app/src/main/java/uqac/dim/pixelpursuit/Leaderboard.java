package uqac.dim.pixelpursuit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Leaderboard extends AppCompatActivity {

    private final String TAG = "Leaderboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int scorePerso = sharedPreferences.getInt("Score", -1);
        TextView affichageScore = ((TextView)findViewById(R.id.tv_score_perso_leaderboard));
        if (scorePerso < 0){
            affichageScore.setVisibility(View.GONE);
        }
        else{
            affichageScore.setText(String.format(getResources().getString(R.string.leaderboard_score), scorePerso));
        }

        RecyclerView recyclerview_leaderboard = findViewById(R.id.rv_leaderboard);

        getPlayersFromDb(players -> {

            //Trie la liste en fonction du score
            players.sort((o1, o2) -> Long.compare(o2.getPlayerScore(), o1.getPlayerScore()));

            List<LeaderboardPlayer> playersFinale = new ArrayList<>();

            for (int i=0; i<20; i++){
                playersFinale.add(players.get(i));
            }

            recyclerview_leaderboard.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerview_leaderboard.setAdapter(new LeaderboardAdapter(getApplicationContext(),playersFinale));
        });
    }

    private void getPlayersFromDb(FirestoreCallback firestoreCallback) {
        List<LeaderboardPlayer> players = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("player")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Récupère et parse les données de la bdd et remplit la liste "players" avec
                            players.add(new LeaderboardPlayer(Objects.requireNonNull(document.getData().get("nom")).toString(), (Long)document.getData().get("score"), new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss").format(((Timestamp)document.getData().get("date")).toDate())));
                        }
                        firestoreCallback.onCallBack(players);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private interface FirestoreCallback {
        void onCallBack(List<LeaderboardPlayer> players);
    }
}