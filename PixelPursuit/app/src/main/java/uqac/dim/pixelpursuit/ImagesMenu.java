package uqac.dim.pixelpursuit;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImagesMenu extends AppCompatActivity {

    private ImageView mainImageView;
    private TextView selectedPackTitleTextView;
    private TextView selectedPackDescriptionTextView;
    private TextView currentPackDescriptionTextView;
    private TextView currentPackTitleTextView;
    private RecyclerView secondaryImagesRecyclerView;
    private ImageView isSelectedPack;
    private ImageView centralImage;
    private List<Pack> packs;
    private int currentPackIndex = 0;
    private CustomPackDatabaseHelper dbHelper;
    private PackHelper packHelper;
    private XmlResourceParser parser;
    private static final int REQUEST_CREATE_PACK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_menu);

        // Initialisation des helpers et des composants UI
        dbHelper = new CustomPackDatabaseHelper(this);
        packHelper = new PackHelper(this);

        mainImageView = findViewById(R.id.imageSelectedPack);
        selectedPackTitleTextView = findViewById(R.id.titleSelectedPack);
        selectedPackDescriptionTextView = findViewById(R.id.descriptionSelectedPack);
        currentPackTitleTextView = findViewById(R.id.titleCurrentPack);
        currentPackDescriptionTextView = findViewById(R.id.descriptionCurrentPack);
        isSelectedPack = findViewById(R.id.packIsSelected);
        centralImage = findViewById(R.id.centralImage);
        secondaryImagesRecyclerView = findViewById(R.id.secondaryImagesRecyclerView);

        if (secondaryImagesRecyclerView == null) {
            Log.e("ImagesMenu", "RecyclerView is null");
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        secondaryImagesRecyclerView.setLayoutManager(layoutManager);

        parser = getResources().getXml(R.xml.packs);
        packs = packHelper.parsePacks(parser, dbHelper);
        Pack chosenPack = packHelper.getChosenPack();

        if (chosenPack == null) {
            chosenPack = packHelper.getDefaultPack();
        }
        for (int i = 0; i < packs.size(); i++) {
            if (packHelper.isChosenPack(packs.get(i))) {
                currentPackIndex = i;
                break; // Arrête la boucle une fois le pack trouvé
            }
        }
        mainImageView.setImageBitmap(packHelper.getMainImageBitmap(chosenPack));
        updateDisplayedPack();
        selectedPackTitleTextView.setText(chosenPack.getTitle());
        selectedPackDescriptionTextView.setText(chosenPack.getDescription());

        setupListeners();
    }

    private void setupListeners() {
        findViewById(R.id.leftArrow).setOnClickListener(v -> showPreviousPack());
        findViewById(R.id.rightArrow).setOnClickListener(v -> showNextPack());
        findViewById(R.id.buttonSelectPack).setOnClickListener(v -> updateSelectedPack(packs.get(currentPackIndex)));
        findViewById(R.id.buttonCreatePack).setOnClickListener(v -> createNewPack());
        findViewById(R.id.buttonDeletePack).setOnClickListener(v -> deleteCurrentPack());
    }

    //Lance l'activité de création de nouveau pack custom
    private void createNewPack(){
        Intent intent = new Intent(ImagesMenu.this, CreateCustomPackActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_PACK);
    }
    //Supprime le pack actuellement affiché
    private void deleteCurrentPack() {
        Pack currentPack = packs.get(currentPackIndex);
        if (currentPack.getIsCustom()) {
            // Afficher une boîte de dialogue de confirmation avant la suppression
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getText(R.string.DeleteConfirmTitle))
                    .setMessage(getResources().getText(R.string.DeleteConfirmDesc))
                    .setPositiveButton(getResources().getText(R.string.Delete), (dialog, which) -> {
                        // Supprimez le pack de la base de données
                        dbHelper.deletePack(currentPack.getPackId());
                        // Retirez le pack de la liste
                        packs.remove(currentPackIndex);
                        //Resélectionne le pac kpar défaut si le pack supprimé était sélectionné
                        if (currentPack.getTitle().equals(selectedPackTitleTextView.getText())){
                            currentPackIndex = 0;
                            updateSelectedPack(packHelper.getDefaultPack());
                        }
                        if (currentPackIndex >= packs.size()) {
                            currentPackIndex = 0;
                        }
                        // Mettre à jour l'affichage
                        updateDisplayedPack();
                    })
                    .setNegativeButton(getResources().getText(R.string.Cancel), null)
                    .show();
        } else {
            Toast.makeText(this, R.string.DeleteDefaulPacksError, Toast.LENGTH_SHORT).show();
        }
    }

    //Met à jour le pack sélectionné en haut de la page
    private void updateSelectedPack(Pack pack) {
        mainImageView.setImageBitmap(packHelper.getMainImageBitmap(pack));
        selectedPackTitleTextView.setText(pack.getTitle());
        selectedPackDescriptionTextView.setText(pack.getDescription());

        packHelper.savePack(pack);
        updateDisplayedPack();
    }
    // Affiche le pack précédent dans la liste
    private void showPreviousPack() {
        currentPackIndex--;
        if (currentPackIndex < 0) {
            currentPackIndex = packs.size() - 1;
        }
        updateDisplayedPack();
    }

    // MAffiche le pack suivant dans la liste
    private void showNextPack() {
        currentPackIndex++;
        if (currentPackIndex >= packs.size()) {
            currentPackIndex = 0;
        }
        updateDisplayedPack();
    }

    // Met à jour le pack affiché
    private void updateDisplayedPack() {
        Pack currentPack = packs.get(currentPackIndex);

        if (currentPack != null) {

            if (packHelper.isChosenPack(currentPack)){
                isSelectedPack.setVisibility(View.VISIBLE);
                currentPackTitleTextView.setText(String.format(getResources().getString(R.string.SelectedPackTitle), currentPack.getTitle()));
            }
            else {
                isSelectedPack.setVisibility(View.GONE);
                currentPackTitleTextView.setText(currentPack.getTitle());
            }

            centralImage.setImageBitmap(packHelper.getMainImageBitmap(currentPack));
            currentPackDescriptionTextView.setText(currentPack.getDescription());
            List<Bitmap> secondaryImageBitmaps = packHelper.getSecondaryImageBitmaps(currentPack);
            ImageAdapter adapter = new ImageAdapter(this, secondaryImageBitmaps, centralImage);
            secondaryImagesRecyclerView.setAdapter(adapter);


        }
    }

    //Met à jour l'activité lorsqu'un pack custom est ajouté
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_PACK && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("PACK_CREATED", false)) {
                // Réinitialisation de la liste des packs
                packs.clear();

                parser = getResources().getXml(R.xml.packs);
                packs = packHelper.parsePacks(parser, dbHelper);
                Log.e("TAG", packs.toString());
                updateDisplayedPack();
            }
        }
    }

}
