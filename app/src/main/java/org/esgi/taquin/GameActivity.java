package org.esgi.taquin;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.esgi.taquin.R;

public class GameActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gameGrid;
    private Image2Adapter imageAdapter;
    private TextView scoreTextView;
    private int score = 0;
    private Chronometer chrono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Initialisation des éléments de la deuxième fenêtre
        gameGrid = findViewById(R.id.game_grid);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();

        scoreTextView = findViewById(R.id.score);
        scoreTextView.setText(R.string.score + score);
        ImageView imageExample = findViewById(R.id.example_image);
        chrono = findViewById(R.id.chrono);
        chrono.start();

        //Récupération des données passée depuis la première activité menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("type");
            int img = extras.getInt("id");
            String path = extras.getString("uri");
            assert value != null;

            switch (value) {
                case "easy":
                    if (path != null && !path.isEmpty()) {
                        imageAdapter = new Image2Adapter(this, 3, path);
                        imageExample.setImageBitmap(BitmapFactory.decodeFile(path));
                    } else {
                        imageAdapter = new Image2Adapter(this, 3, img);
                        imageExample.setImageResource(img);
                    }
                    gameGrid.setColumnWidth(metrics.widthPixels / 3);
                    break;
                case "medium":
                    if (path != null && !path.isEmpty()) {
                        imageAdapter = new Image2Adapter(this, 4, path);
                        imageExample.setImageBitmap(BitmapFactory.decodeFile(path));
                    } else {
                        imageAdapter = new Image2Adapter(this, 4, img);
                        imageExample.setImageResource(img);
                    }
                    gameGrid.setColumnWidth(metrics.widthPixels / 4);
                    break;
                case "hard":
                    if (path != null && !path.isEmpty()) {
                        imageAdapter = new Image2Adapter(this, 5, path);
                        imageExample.setImageBitmap(BitmapFactory.decodeFile(path));
                    } else {
                        imageAdapter = new Image2Adapter(this, 5, img);
                        imageExample.setImageResource(img);
                    }
                    gameGrid.setColumnWidth(metrics.widthPixels / 5);
                    break;
            }
            gameGrid.setAdapter(imageAdapter);
        }
        gameGrid.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        imageAdapter.move(position);
        gameGrid.setAdapter(imageAdapter);
        score++;
        scoreTextView.setText("Score : " +  score);

        //Vérificationd de fin de partie
        if(imageAdapter.winCheck()){
            chrono.stop();
            Toast message = Toast.makeText(getApplicationContext(),R.string.end_game , Toast.LENGTH_SHORT);
            message.show();
        }
    }
}
