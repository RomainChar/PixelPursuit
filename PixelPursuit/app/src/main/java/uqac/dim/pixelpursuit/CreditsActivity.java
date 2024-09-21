package uqac.dim.pixelpursuit;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CreditsActivity extends AppCompatActivity {

    private ImageView imageViewEcolesLogo;
    private ImageView imageViewDeveloperPhoto;
    private TextView textViewDeveloperNom;
    private TextView textViewDeveloperDescription;
    private boolean isFirstLogoClick = true;

    private final Developer[] developers = {
            new Developer(R.drawable.romainc_photo, "Romain", "Charrondiere", ""),
            new Developer(R.drawable.julienb_photo, "Julien", "Baert", ""),
            new Developer(R.drawable.romainv_photo, "Romain", "Vidal", ""),
            new Developer(R.drawable.jimmyc_photo, "Jimmy", "Cao Van Truong", "")
    };

    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);

        developers[0].setDescription(getString(R.string.romaincdescription));
        developers[1].setDescription(getString(R.string.julienbdescription));
        developers[2].setDescription(getString(R.string.romainvdescription));
        developers[3].setDescription(getString(R.string.jimmycdescription));

        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        imageViewEcolesLogo = findViewById(R.id.imageViewEcolesLogo);
        imageViewDeveloperPhoto = findViewById(R.id.imageViewDeveloperPhoto);
        textViewDeveloperNom = findViewById(R.id.textViewDeveloperNom);
        textViewDeveloperDescription = findViewById(R.id.textViewDeveloperDescription);


        imageViewLogo.setOnClickListener(v -> animateTexts());
    }

    private void animateTexts() {
        // Animation pour le texte sortant
        Animation slideOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                updateTextsAndImages();
                // Animation pour le texte entrant
                Animation slideInAnimation = AnimationUtils.loadAnimation(CreditsActivity.this, R.anim.slide_in_right);
                imageViewDeveloperPhoto.startAnimation(slideInAnimation);
                textViewDeveloperNom.startAnimation(slideInAnimation);
                textViewDeveloperDescription.startAnimation(slideInAnimation);

                isFirstLogoClick = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        if (isFirstLogoClick){
            imageViewEcolesLogo.startAnimation(slideOutAnimation);
        }

        imageViewDeveloperPhoto.startAnimation(slideOutAnimation);
        textViewDeveloperNom.startAnimation(slideOutAnimation);
        textViewDeveloperDescription.startAnimation(slideOutAnimation);
    }

    private void updateTextsAndImages() {
        if (isFirstLogoClick){
            imageViewDeveloperPhoto.setVisibility(View.VISIBLE);
            imageViewEcolesLogo.setVisibility(View.GONE);
            textViewDeveloperNom.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textViewDeveloperNom.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, R.id.imageViewDeveloperPhoto);
            textViewDeveloperNom.setLayoutParams(layoutParams);
            textViewDeveloperDescription.setVisibility(View.VISIBLE);
        }

        Developer currentDeveloper = developers[currentIndex];
        imageViewDeveloperPhoto.setImageResource(currentDeveloper.getPhotoId());
        textViewDeveloperNom.setText(currentDeveloper.getFirstName() + " " + currentDeveloper.getLastName());
        textViewDeveloperDescription.setText(currentDeveloper.getDescription());

        // Passer au développeur suivant
        currentIndex = (currentIndex + 1) % developers.length;
    }
}
