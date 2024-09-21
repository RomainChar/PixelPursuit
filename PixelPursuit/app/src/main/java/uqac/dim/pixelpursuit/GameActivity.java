package uqac.dim.pixelpursuit;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    MediaPlayer musicPlayer;
    MediaPlayer soundPlayer = null;
    Random ran = new Random();
    int score = 0;
    double speedModifier = 0.1;
    double maxTimer = 5;
    double timer = 5;
    int nbImage;
    int gridSize = 1;
    ImageView imageToFind;
    ImageButton correctImage;
    ArrayList<ImageButton> imagesGrid = new ArrayList<>();
    ArrayList<Bitmap> images = new ArrayList<>();
    GridLayout layout;
    GameActivity activity = this;
    TextView leftText;
    TextView rightText;
    boolean pause = false;
    int nbWhite = 2;
    PackHelper helper;
    int nbRound = 10;
    Pack pack;

    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            showSureAlert();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        imageToFind = findViewById(R.id.imageToFind);
        helper = new PackHelper(this);
        pack = helper.getChosenPack();

        maxTimer = sharedPreferences.getInt("time", 5);
        gridSize = sharedPreferences.getInt("imagesize", 2);
        speedModifier = (float)(sharedPreferences.getInt("speed", 20)) / 100f;


        leftText = findViewById(R.id.left_text);
        rightText = findViewById(R.id.right_text);
        layout = findViewById(R.id.game_gridlayout);
        layout.setColumnCount(gridSize);
        layout.setRowCount(gridSize);
        timer = maxTimer;

        LaunchGame();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        musicPlayer = MediaPlayer.create(this, R.raw.roccowseabattlesinspace);
        musicPlayer.setLooping(true);
        musicPlayer.setVolume(0.5f,0.5f);
        musicPlayer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(musicPlayer != null)
        {
            musicPlayer.stop();
            musicPlayer.release();
        }
        if(soundPlayer != null)
        {
            soundPlayer.stop();
            soundPlayer.release();
        }
    }

    private void newRound()
    {
        int idImageToFind = ran.nextInt(images.size() - nbWhite);

        for (ImageButton b: imagesGrid)
        {
            int i = idImageToFind;
            while(i == idImageToFind)
                i = ran.nextInt(images.size());
            b.setImageBitmap(images.get(i));
        }
        correctImage = imagesGrid.get(ran.nextInt(imagesGrid.size()));
        correctImage.setImageBitmap(images.get(idImageToFind));
        imageToFind.setImageBitmap(images.get(idImageToFind));
    }

    private void LaunchGame()
    {
        layout.post(() -> {
            UpdateLayout();

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!pause) {
                        if (timer <= 0) {
                            runOnUiThread(() -> onLose());
                            cancel();
                        } else {
                            runOnUiThread(() -> UpdateScoreText());
                            timer -= 0.1;
                        }
                    }
                }
            }, 0, 100);

            UpdateParametersText();
            newRound();
        });

    }

    @Override
    public void onClick(View v) {
        ImageButton b = (ImageButton) v;
        if(soundPlayer != null)
            soundPlayer.release();

        if(timer > 0 && b == correctImage)
        {
            nbRound -= 1;
            soundPlayer = MediaPlayer.create(this, R.raw.coin);
            soundPlayer.setVolume(1,1);
            score += (int) (gridSize + (5 - maxTimer));
            if(nbRound == 0)
                UpgradeDifficulty();
            timer = maxTimer;
            newRound();
            //WIN
        }
        else
        {
            soundPlayer = MediaPlayer.create(this, R.raw.losesound);
            soundPlayer.setVolume(0.4f,0.4f);
            timer -= 0.5;
            if(timer < 0)
                timer = 0;
        }

        soundPlayer.start();
        UpdateScoreText();
    }

    private void onLose()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.NoMoreTime)
                .setMessage(String.format(getResources().getString(R.string.Score), score))
                .setCancelable(false)
                .setPositiveButton(R.string.BackToMenu, (dialog, which) -> {
                    //Ajout de l'intention à renvoyer avec extra
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(sharedPreferences.getInt("Score", 0) < score) {
                        editor.putInt("Score", (score));
                        editor.apply();
                    }
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> result = new HashMap<>();
                    result.put("nom", sharedPreferences.getString("nom", "USER"));
                    result.put("score", score);
                    Timestamp date = new Timestamp(System.currentTimeMillis());
                    result.put("date", date);
                    db.collection("player").add(result);

                    finish();
                });

        builder.create().show();
    }

    private void showSureAlert()
    {
        pause = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PAUSE")
                .setMessage(R.string.QuitGameTitle)
                .setCancelable(false)
                .setPositiveButton(R.string.Leave, (dialog, which) -> onLose())
                .setNegativeButton(R.string.Cancel, (dialog, which) -> pause = false);

        builder.create().show();
    }

    private void UpdateScoreText()
    {
        leftText.setText(String.format(getResources().getString(R.string.ScoreAndTime), score, String.format("%.1f", Math.max(0, timer))));

    }

    private void UpdateParametersText()
    {
        rightText.setText(String.format(getResources().getString(R.string.Parameters), String.format("%.1f", maxTimer), layout.getColumnCount()));

    }

    private void UpdateLayout()
    {
        int width = layout.getWidth() / layout.getColumnCount();
        int height = layout.getHeight() / layout.getRowCount();

        List<Bitmap> unsizedImage = helper.getSecondaryImageBitmaps(pack);
        images.clear();

        int len = unsizedImage.size();
        int j = 0;
        while(j < len)
        {
            Bitmap resized = Bitmap.createScaledBitmap(unsizedImage.get(j), width, height, true);
            images.add(resized);
            j += 1;
        }

        Bitmap white = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        white.eraseColor(Color.TRANSPARENT);

        for(int i = 0; i < nbWhite; i++)
            images.add(white);

        nbImage = layout.getColumnCount() * layout.getRowCount();
        for (int i = imagesGrid.size(); i < nbImage; i++)
        {
            ImageButton b = new ImageButton(activity);
            b.setPadding(0,0,0,0);
            b.setOnClickListener(activity);
            b.setBackgroundColor(Color.TRANSPARENT);

            imagesGrid.add(b);
            layout.addView(b);
        }
    }

    private void UpgradeDifficulty()
    {
        nbRound = 10;
        if(maxTimer - speedModifier > 1)
        {
            maxTimer -= speedModifier;
        }
        else
        {
            maxTimer = 1;
        }
        gridSize += 1;
        layout.setColumnCount(layout.getColumnCount() + 1);
        layout.setRowCount(layout.getRowCount() + 1);
        UpdateLayout();
        UpdateParametersText();
    }
}
