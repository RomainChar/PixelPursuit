package uqac.dim.pixelpursuit.difficultydabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.DuplicateFormatFlagsException;
import java.util.List;

@Dao
public interface DifficultyDAO {

    @Query("SELECT * FROM difficulty")
    List<Difficulty> getAllDifficulties();

    @Query("SELECT * FROM difficulty WHERE difficulty.nom = :nomBouton LIMIT 1")
    Difficulty getOneDifficultyFromName(String nomBouton);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDifficulty(Difficulty... difficulties);

    @Delete
    void deleteDifficulty(Difficulty difficulty);

    @Query("DELETE FROM difficulty")
    void deleteDifficulties();
}
