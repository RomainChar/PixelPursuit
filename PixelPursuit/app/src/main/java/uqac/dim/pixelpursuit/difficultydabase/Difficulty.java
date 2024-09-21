package uqac.dim.pixelpursuit.difficultydabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Difficulty {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nom")
    public String nom;

    @ColumnInfo(name = "vitesse")
    public int vitesse;

    @ColumnInfo(name = "temps")
    public int temps;

    @ColumnInfo(name = "taille")
    public int taille;

    public Difficulty(String nom, int vitesse, int temps, int taille){
        this.nom = nom;
        this.vitesse = vitesse;
        this.temps = temps;
        this.taille = taille;
    }

    @NonNull
    @Override
    public String toString() {
        return "Nom : " + nom + " , Vitesse = " + String.valueOf(vitesse) + " , Temps = " + String.valueOf(temps) + " , Taille = " + String.valueOf(taille);
    }
}
