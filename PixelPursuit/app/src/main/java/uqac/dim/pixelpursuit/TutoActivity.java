package uqac.dim.pixelpursuit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TutoActivity extends AppCompatActivity {
    int currentPage = 0;

    String[] tutoTexts;
    int[] tutoImages = {R.drawable.generaltutorial,R.drawable.logotutorial ,R.drawable.scoretutorial, R.drawable.difficultytutorial};

    ImageView background;
    TextView tutoText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuto);

        tutoText = findViewById(R.id.tutoText);

        String tutoGeneralText = getResources().getString(R.string.TutoGeneral);
        String tutoScoreText = getResources().getString(R.string.TutoScore);
        String tutoDifficultyText = getResources().getString(R.string.TutoDifficulty);
        String tutoLogoText = getResources().getString(R.string.TutoLogo);
        tutoTexts = new String[]{tutoGeneralText,tutoLogoText, tutoScoreText, tutoDifficultyText};
        background = findViewById(R.id.tutoImage);
        UpdatePage();
    }


    public void NextPage(View view) {
        if(currentPage < tutoTexts.length - 1)
        {
            currentPage++;
            UpdatePage();
        }
    }

    public void PrevPage(View view) {
        if(currentPage != 0)
        {
            currentPage--;
            UpdatePage();
        }
    }

    private void UpdatePage()
    {
        background.setBackgroundResource(tutoImages[currentPage]);
        tutoText.setText(tutoTexts[currentPage]);
    }
}
