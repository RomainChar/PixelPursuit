package uqac.dim.pixelpursuit.difficultydabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Difficulty.class}, version = 1)
public abstract class DifficultyDatabase extends RoomDatabase {
    public abstract DifficultyDAO difficultyDAO();

    private static DifficultyDatabase INSTANCE;

    public static DifficultyDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, DifficultyDatabase.class, "difficulty_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
