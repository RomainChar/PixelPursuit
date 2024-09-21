package uqac.dim.pixelpursuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar actionbar = (Toolbar) findViewById(R.id.ActionBar);
        actionbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        actionbar.setTitle(R.string.Menu);
        setSupportActionBar(actionbar);
    }

    public void PlayGame(View v){
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

    public void Tutoriel(View v){
        Intent intent = new Intent(MainActivity.this, TutoActivity.class);
        startActivity(intent);
    }

    public void Leaderboard(View v){
        Intent intent = new Intent(MainActivity.this, Leaderboard.class);
        startActivity(intent);
    }

    public void Settings(View v){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void ImageConfig(View v){
        Intent intent = new Intent(MainActivity.this, ImagesMenu.class);
        startActivity(intent);
    }

    public void Credits(View v){
        Intent intent = new Intent(MainActivity.this, CreditsActivity.class);
        startActivity(intent);
    }

    public void Exit(View v){
        finish();
    }
}