package uqac.dim.pixelpursuit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import uqac.dim.pixelpursuit.difficultydabase.Difficulty;
import uqac.dim.pixelpursuit.difficultydabase.DifficultyDatabase;

public class SettingsActivity extends AppCompatActivity {
    private int speed;
    private int imagesize;
    private int time;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar actionbar = (Toolbar) findViewById(R.id.ActionBar);
        actionbar.setTitleTextColor(Color.parseColor("#ffffffff"));
        actionbar.setTitle(R.string.SettingsTitle);
        setSupportActionBar(actionbar);

        sp = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        EditText et = (EditText) findViewById(R.id.EditText_Username);
        et.setText(sp.getString("nom", getResources().getString(R.string.DefaultName)));
        et.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("nom", s.toString());
                editor.apply();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        SeekBar sb1 = findViewById(R.id.SeekBar_Speed);
        TextView tv1 = findViewById(R.id.Text_SpeedValue);
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                tv1.setText(String.valueOf(progress));
                speed = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        SeekBar sb2 = findViewById(R.id.SeekBar_ImageSize);
        TextView tv2 = findViewById(R.id.Text_ImageSizeValue);
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                tv2.setText(String.valueOf(progress));
                imagesize = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        SeekBar sb3 = findViewById(R.id.SeekBar_Time);
        TextView tv3 = findViewById(R.id.Text_TimeValue);
        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                tv3.setText(String.valueOf(progress));
                time = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        sb1.setMin(10);
        sb1.setMax(100);
        sb1.setProgress(20);
        tv1.setText("20");
        speed = 20;
        sb2.setMin(2);
        sb2.setMax(10);
        sb2.setProgress(5);
        tv2.setText("5");
        imagesize = 5;
        sb3.setMin(1);
        sb3.setMax(5);
        sb3.setProgress(10);
        tv3.setText("5");
        time = 5;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra("vitesse", 10);
                TextView tv1 = findViewById(R.id.Text_SpeedValue);
                tv1.setText(String.valueOf(result));
                SeekBar sb1 = findViewById(R.id.SeekBar_Speed);
                sb1.setProgress(result);
                speed = result;
                result = data.getIntExtra("taille", 5);
                TextView tv2 = findViewById(R.id.Text_ImageSizeValue);
                tv2.setText(String.valueOf(result));
                SeekBar sb2 = findViewById(R.id.SeekBar_ImageSize);
                sb2.setProgress(result);
                imagesize = result;
                result = data.getIntExtra("temps", 5);
                TextView tv3 = findViewById(R.id.Text_TimeValue);
                tv3.setText(String.valueOf(result));
                SeekBar sb3 = findViewById(R.id.SeekBar_Time);
                sb3.setProgress(result);
                time = result;
                String resultS = data.getStringExtra("nomDifficulte");
                EditText et = findViewById(R.id.EditText_Difficulty);
                et.setText(resultS);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("DEBUG","CANCELED");
            }
        }
    }

    public void LevelSelection(View v){
        Intent intent = new Intent(SettingsActivity.this, LevelSelectorActivity.class);
        startActivityForResult(intent, 1);
    }

    public void LaunchGamewithSettings(View v){

        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("speed", speed);
        editor.putInt("imagesize", imagesize);
        editor.putInt("time", time);
        editor.apply();

        Intent intent = new Intent(SettingsActivity.this, GameActivity.class);
        startActivity(intent);
    }

    public void Quit(View v){
        finish();
    }

    public void SaveDifficulty(View v){
        EditText et = findViewById(R.id.EditText_Difficulty);
        DifficultyDatabase difficultyDatabase;
        difficultyDatabase = DifficultyDatabase.getDatabase(getApplicationContext());
        for (Difficulty diff : difficultyDatabase.difficultyDAO().getAllDifficulties()) {
            if (diff.nom.equals(et.getText().toString())){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(String.format(getResources().getString(R.string.NewDifficultyWarning), et.getText().toString(), diff.vitesse, diff.taille, diff.temps, speed, imagesize, time))
                        .setTitle(R.string.DifficulyAlreadyExistTitle)
                        .setPositiveButton(R.string.Erase, ((dialog, which) -> OverwriteDifficulty(difficultyDatabase, diff)))
                        .setNeutralButton(R.string.Copy, ((dialog, which) -> CopyDifficulty(difficultyDatabase, diff.nom)))
                        .setNegativeButton(R.string.Cancel, null);
                builder.create().show();
                return;
            }
        }
        difficultyDatabase.difficultyDAO().insertDifficulty(new Difficulty(et.getText().toString(), speed, time, imagesize));
    }

    private void OverwriteDifficulty(DifficultyDatabase difficultyDatabase, Difficulty diff){
        String name = diff.nom;
        difficultyDatabase.difficultyDAO().deleteDifficulty(diff);
        difficultyDatabase.difficultyDAO().insertDifficulty(new Difficulty(name, speed, time, imagesize));
    }

    private void CopyDifficulty(DifficultyDatabase difficultyDatabase, String name){
        String newname = name;
        boolean cantAdd = true;
        while(cantAdd) {
            cantAdd = false;
            for (Difficulty diff : difficultyDatabase.difficultyDAO().getAllDifficulties()) {
                if (diff.nom.equals(newname)){
                    cantAdd = true;
                    newname += " Copy";
                    break;
                }
            }
        }
        difficultyDatabase.difficultyDAO().insertDifficulty(new Difficulty(newname, speed, time, imagesize));
    }
}