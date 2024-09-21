package uqac.dim.pixelpursuit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uqac.dim.pixelpursuit.difficultydabase.Difficulty;
import uqac.dim.pixelpursuit.difficultydabase.DifficultyDatabase;

public class LevelSelectorActivity extends AppCompatActivity {

    // Instance de la base de données locale des difficultés
    private DifficultyDatabase difficultyDatabase;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_selector);

        // Récupération du recyclerView et de la base de données
        recyclerView = findViewById(R.id.rv_lvl_selector);

        difficultyDatabase = DifficultyDatabase.getDatabase(getApplicationContext());

        // Mise à jour du recycler view pour ajouter les difficultés
        List<Difficulty> difficultyList = difficultyDatabase.difficultyDAO().getAllDifficulties();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LevelSelectorAdapter(this.getApplicationContext(), difficultyList));
    }

    // Méthode appelée pour chaque clic sur un bouton d'une difficulté personnalisée
    public void appuiBouton(View view) {
        // On récupère le nom de la difficulté et on la récupère dans la base de données
        String nomDifficulte = String.valueOf(((Button)view).getText());
        Difficulty difficulteeSelectionnee = difficultyDatabase.difficultyDAO().getOneDifficultyFromName(nomDifficulte);
        if (difficulteeSelectionnee != null){
            Log.i("DIM",difficulteeSelectionnee.toString());
            renvoyerValeurs(difficulteeSelectionnee.vitesse, difficulteeSelectionnee.taille, difficulteeSelectionnee.temps, difficulteeSelectionnee.nom);
        }
        else{
            Toast.makeText(getApplicationContext(),R.string.DifficultyErased, Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode appelée pour chaque clic sur un bouton de la difficulté facile
    public void appuiFacile(View view){
        Log.i("DIM", "Nom : Facile , Vitesse = 10 , Taille = 2 , Temps = 5");
        renvoyerValeurs(10, 2, 5, "Facile");
    }

    // Méthode appelée pour chaque clic sur un bouton de la difficulté moyenne
    public void appuiMoyen(View view){
        Log.i("DIM", "Nom : Moyenne , Vitesse = 40 , Taille = 5 , Temps = 4");
        renvoyerValeurs(40, 5, 4, "Moyenne");
    }

    // Méthode appelée pour chaque clic sur un bouton de la difficulté difficile
    public void appuiDifficile(View view){
        Log.i("DIM", "Nom : Difficile , Vitesse = 70 , Taille = 7 , Temps = 2");
        renvoyerValeurs(70, 7, 2, "Difficile");
    }

    // Méthode appelée pour chaque clic sur un bouton de la difficulté pro
    public void appuiPro(View view){
        Log.i("DIM", "Nom : Pro , Vitesse = 100 , Taille = 10 , Temps = 1");
        renvoyerValeurs(100, 10, 1, "Pro");
    }


    // Renvoi des valeurs de la difficulté sélectionnée dans une intention
    public void renvoyerValeurs(int vitesse, int taille, int temps, String nomDifficulte){
        Intent intentRenvoyee = new Intent();
        intentRenvoyee.putExtra("vitesse", vitesse);
        intentRenvoyee.putExtra("taille", taille);
        intentRenvoyee.putExtra("temps", temps);
        intentRenvoyee.putExtra("nomDifficulte", nomDifficulte);
        setResult(Activity.RESULT_OK,intentRenvoyee);
        finish();
    }

    public void supprimerDifficulte(View view) {
        EditText nomDifficulteASuppr = ((EditText)findViewById(R.id.et_supprimer_difficulte));
        String nomDifficulteSupprimee = String.valueOf(nomDifficulteASuppr.getText());
        Difficulty difficulteASuppr = difficultyDatabase.difficultyDAO().getOneDifficultyFromName(nomDifficulteSupprimee);
        if (difficulteASuppr != null){
            difficultyDatabase.difficultyDAO().deleteDifficulty(difficulteASuppr);

            List<Difficulty> difficultyList = difficultyDatabase.difficultyDAO().getAllDifficulties();

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new LevelSelectorAdapter(this.getApplicationContext(), difficultyList));
        }
        else{
            Toast.makeText(getApplicationContext(), R.string.NoDifficultyFound, Toast.LENGTH_SHORT).show();
        }
    }

    public void supprimerDifficultes(View view) {
        difficultyDatabase.difficultyDAO().deleteDifficulties();
        List<Difficulty> difficultyList = difficultyDatabase.difficultyDAO().getAllDifficulties();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LevelSelectorAdapter(this.getApplicationContext(), difficultyList));
    }
}
