package uqac.dim.pixelpursuit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Classe de gestion de la base de données
public class CustomPackDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "packs.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "packs";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_MAIN_IMAGE = "main_image";
    private static final String COLUMN_SECONDARY_IMAGES = "secondary_images";

    // Création de la table des packs
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_MAIN_IMAGE + " TEXT," +
                    COLUMN_SECONDARY_IMAGES + " TEXT)";

    // Constructeur
    public CustomPackDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Méthode appelée lors de la création de la base de données
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // Méthode appelée lors de la mise à jour de la base de données
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprimer la table existante et recréer la base de données
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Ajout d'un nouveau pack à la base de donnée (via la classe pack)
    public long addPack(Pack pack) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, pack.getTitle());
        values.put(COLUMN_DESCRIPTION, pack.getDescription());
        values.put(COLUMN_MAIN_IMAGE, pack.getMainImage());
        // Convertir la liste de chemins d'images secondaires en une seule chaîne de caractères pour le stocker dans la base de données
        String secondaryImages = TextUtils.join(",", pack.getSecondaryImages());
        values.put(COLUMN_SECONDARY_IMAGES, secondaryImages);
        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }

    //Retourne l'ensemble des packs custom de la base de données
    public List<Pack> getAllPacks() {
        List<Pack> packs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_MAIN_IMAGE, COLUMN_SECONDARY_IMAGES};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        // Vérification des données récupérées
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String mainImageFileName = cursor.getString(cursor.getColumnIndex(COLUMN_MAIN_IMAGE));
                String secondaryImageFileNames = cursor.getString(cursor.getColumnIndex(COLUMN_SECONDARY_IMAGES));

                // Création du nouveau pack
                Pack pack = new Pack(mainImageFileName, splitSecondaryImages(secondaryImageFileNames), title, description, id, true);
                //Les packs de la base de données sont custom
                pack.setIsCustom(true);

                // Ajouter le pack à la liste des packs qu'on retourne
                packs.add(pack);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return packs;
    }

    // Méthode pour diviser la chaîne de noms de fichiers d'images secondaires en un tableau (pour passer de la base de données à la valeur utilisée dans l'application)
    private String[] splitSecondaryImages(String secondaryImagesString) {
        if (secondaryImagesString != null && !secondaryImagesString.isEmpty()) {
            return secondaryImagesString.split(",");
        }
        return new String[0];
    }

    //Suppression d'un pack à partir de son id dans la base de données
    public boolean deletePack(int packId) {
        Pack pack = getPackById(packId);
        if (pack == null) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(packId) };
        int deletedRows = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();

        // Si le pack a été supprimé de la base de données, supprimer les images
        if (deletedRows > 0) {
            deleteAllImages(pack);
        }

        return deletedRows > 0;
    }
    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
    private void deleteAllImages(Pack pack) {
        // Supprimer l'image principale
        deleteFile(pack.getMainImage());

        // Supprimer les images secondaires
        for (String imagePath : pack.getSecondaryImages()) {
            deleteFile(imagePath);
        }
    }
    public Pack getPackById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id)};

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);

        Pack pack = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            String mainImage = cursor.getString(cursor.getColumnIndex(COLUMN_MAIN_IMAGE));
            String secondaryImageFileNames = cursor.getString(cursor.getColumnIndex(COLUMN_SECONDARY_IMAGES));

            pack = new Pack(mainImage, splitSecondaryImages(secondaryImageFileNames), title, description, id, true);
        }
        cursor.close();
        return pack;
    }


}


