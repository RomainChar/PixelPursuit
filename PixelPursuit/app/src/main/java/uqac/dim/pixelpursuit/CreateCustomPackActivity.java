package uqac.dim.pixelpursuit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CreateCustomPackActivity extends AppCompatActivity {

    private static final int MAX_IMAGES = 8;
    private static final int MIN_IMAGES = 3;
    private final Bitmap[] selectedImages = new Bitmap[MAX_IMAGES+1];
    private final ImageView[] imageViews = new ImageView[MAX_IMAGES+1];
    private CustomPackDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_custom_pack);

        dbHelper = new CustomPackDatabaseHelper(this);

        // Initialisation des vues des images secondaires et de l'image principale
        ImageView selectAllImages = findViewById(R.id.selectAllImage);
        imageViews[0] = findViewById(R.id.mainImageView);
        imageViews[1] = findViewById(R.id.imageView1);
        imageViews[2] = findViewById(R.id.imageView2);
        imageViews[3] = findViewById(R.id.imageView3);
        imageViews[4] = findViewById(R.id.imageView4);
        imageViews[5] = findViewById(R.id.imageView5);
        imageViews[6] = findViewById(R.id.imageView6);
        imageViews[7] = findViewById(R.id.imageView7);
        imageViews[8] = findViewById(R.id.imageView8);

        selectAllImages.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 100);
        });

        // Création d'un listener pour chaque imageViews
        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            imageViews[i].setOnClickListener(v -> dispatchTakePictureIntent(index));
        }


        // Création du listener pour sauvegarder le pack
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
                if (saveCustomPack()){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("PACK_CREATED", true);
                    setResult(RESULT_OK, returnIntent);
                    finish();
            }
        });
    }

    // Création d'une intention pour sélectionner une image dans la galerie
    private void dispatchTakePictureIntent(int index) {
        // Créer un intent pour ouvrir la galerie de photos
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Vérifier si une application peut gérer l'intent
        if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
            // Démarrer l'activité de sélection de l'image depuis la galerie
            startActivityForResult(pickImageIntent, index);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoAppForPictures), Toast.LENGTH_SHORT).show();
        }
    }

    //Lorsqu'une image est sélectionnée, elle est enregistrée et affichée
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); // Compter le nombre total d'images sélectionnées
                if (count<=MAX_IMAGES){
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        handleNewURI(imageUri, i+1);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.SelectMaxImages), MAX_IMAGES), Toast.LENGTH_SHORT).show();

                }

            }
        }
        else if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            handleNewURI(selectedImageUri, requestCode);
        }
    }

    private void handleNewURI(Uri selectedImageUri, int imageIndex){
        try {
            Log.d("TAG", "succès enregistrement image");
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

            // Redimensionnement du bitmap pour éviter les ralentissements de l'application
            Bitmap resizedBitmap = resizeBitmap(imageBitmap, 160);

            selectedImages[imageIndex] = resizedBitmap;
            imageViews[imageIndex].setImageBitmap(resizedBitmap);
        } catch (IOException e) {
            Log.d("TAG", "Erreur à l'enregistrement des images");
            e.printStackTrace();
        }
    }
    //Sauvegarde du pack d'image
    private boolean saveCustomPack() {
        EditText editTextPackName = findViewById(R.id.editTextPackName);
        EditText editTextPackDescription = findViewById(R.id.editTextPackDescription);
        String title = editTextPackName.getText().toString();
        String description = editTextPackDescription.getText().toString();

        // Vérifier que le nom et la description ne sont pas vides
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, R.string.PleaseFillAllFields, Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.e("TAG", "imageView " + imageViews[0].getDrawable());

        PackHelper packHelper = new PackHelper(this);
        if (packHelper.isPackNameTaken(editTextPackName.getText().toString())){
            Toast.makeText(this, R.string.NameTaken, Toast.LENGTH_SHORT).show();
            return false;
        }
        //Vérifier qu'une image principale a été sélectionnée
        if (isDefaultImage(imageViews[0])) {
            Toast.makeText(this, R.string.PleaseSelectMainImage, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Vérifier que toutes les images secondaires ont bien été sélectionnées
        int validImages = 0;
        for (int i = 1; i < selectedImages.length; i++) {
            if (selectedImages[i] != null) {
                validImages++;
            }

        }
        if (validImages < 3 || validImages > MAX_IMAGES) {
            Toast.makeText(this, String.format(getResources().getString(R.string.SelectBetweenImages), MIN_IMAGES, MAX_IMAGES), Toast.LENGTH_SHORT).show();
            return false;
        }

        //Enregistrement des images custom
        String[] imageFileNames = new String[validImages+1];
        for (int i = 0; i < validImages+1; i++) {
            Bitmap imageBitmap = selectedImages[i];
            String imageFileName = saveImageToFile(imageBitmap, title, i-1);
            imageFileNames[i] = imageFileName;
        }

        //Création du nouveau pack custom
        Pack customPack = new Pack(imageFileNames[0], Arrays.copyOfRange(imageFileNames, 1, imageFileNames.length), title, description, 0, false);

        //Ajout du pack dans la base de données
        long newRowId = dbHelper.addPack(customPack);
        if (newRowId != -1) {
            // Afficher un message de succès si l'ajout est réussi
            Toast.makeText(this, R.string.SavePackSuccess, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // Afficher un message d'erreur si l'ajout a échoué
            Toast.makeText(this, R.string.PackSaveError, Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    //Vérifie si une image est toujours celle par défaut (ic_menu_camera)
    private boolean isDefaultImage(ImageView imageView) {
        Drawable defaultDrawable = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_camera);
        Drawable currentDrawable = imageView.getDrawable();
        return currentDrawable != null && currentDrawable.getConstantState().equals(defaultDrawable.getConstantState());
    }

    // Enregistre une image bitmap dans le stockage interne de l'appareil
    private String saveImageToFile(Bitmap imageBitmap, String packName, int index) {
        // Générer le nom de fichier en fonction du nom du pack, de l'indicateur principal/secondaire et de l'index
        String fileName;
        //Génération du nom du fichier en fonction du nom du pack
        if (index == 0) {
            fileName = packName + "_image_main";
        } else {
            fileName = packName + "_image_" + index;
        }

        // Créer le répertoire de stockage des images
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            // Créer le fichier image dans le répertoire de stockage
            File imageFile = File.createTempFile(
                    fileName,  /* préfixe */
                    ".png",         /* suffixe */
                    storageDir      /* répertoire */
            );

            // Écrire le contenu de l'image bitmap dans le fichier
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }

            // Retourner le chemin absolu du fichier image
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("TAG", "Erreur lors de l'enregistrement de l'image : " + e.getMessage());
            return null;
        }
    }

    private Bitmap resizeBitmap(Bitmap original, int maxDimension) {
        int width = original.getWidth();
        int height = original.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            // Le bitmap est plus large que haut
            width = maxDimension;
            height = (int) (width / bitmapRatio);
        } else {
            // Le bitmap est plus haut que large ou carré
            height = maxDimension;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(original, width, height, true);
    }
}

