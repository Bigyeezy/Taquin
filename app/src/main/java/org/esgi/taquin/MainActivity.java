package org.esgi.taquin;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_SELECTED_IMAGE = 100;
    private final int RESULT_CROP = 400;

    private RadioButton easy;
    private RadioButton medium;
    private RadioButton hard;
    private int selectedImage = -1;
    private ImageView imageChoice;
    private String selectedImageFromGallery;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final GridView galleryGrid = findViewById(R.id.gallery_grid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawer = findViewById(R.id.drawer_layout_menu);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawer.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                }
        );



        Button selectImage = findViewById(R.id.add_image);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = -2;
                imageChoice.setImageResource(R.color.colorPrimary);
                openGallery();
            }
        });

        imageChoice = findViewById(R.id.image_choice);
        galleryGrid.setAdapter(new ImageAdapter(this));

        galleryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedImage = (int) parent.getItemAtPosition(position);
                imageChoice.setImageResource((Integer) galleryGrid.getAdapter().getItem(position));
            }
        });

        easy = findViewById(R.id.setting_1);
        medium = findViewById(R.id.setting_2);
        hard = findViewById(R.id.setting_3);
        Button play = findViewById(R.id.play);
        play.setOnClickListener(this);

        requestStoragePermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RESULT_SELECTED_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECTED_IMAGE  && resultCode == RESULT_OK) {
            Uri selectedImageFromGalleryUri = data.getData();
            performCrop(selectedImageFromGalleryUri);
        }

        if (requestCode == RESULT_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap selectedBitmap = extras.getParcelable("data");
            saveBitmapToFileStorage(selectedBitmap);
            // Set The Bitmap Data To ImageView
            imageChoice.setImageBitmap(selectedBitmap);
            imageChoice.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void performCrop(Uri picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onClick(View v) {
        Intent game_intent = new Intent(MainActivity.this, GameActivity.class);

        if(selectedImage == -2) {
            if (hard.isChecked()) {
                game_intent.putExtra("type", "hard");
                game_intent.putExtra("uri", selectedImageFromGallery);
            }else if (medium.isChecked()) {
                game_intent.putExtra("type", "medium");
                game_intent.putExtra("uri", selectedImageFromGallery);
            } else if (easy.isChecked()) {
                game_intent.putExtra("type", "easy");
                game_intent.putExtra("uri", selectedImageFromGallery);
            }

            startActivity(game_intent);
        } else if (selectedImage != -1) {
            if (hard.isChecked()) {
                game_intent.putExtra("type", "hard");
                game_intent.putExtra("id", selectedImage);
            }else if (medium.isChecked()) {
                game_intent.putExtra("type", "medium");
                game_intent.putExtra("id", selectedImage);
            } else if (easy.isChecked()) {
                game_intent.putExtra("type", "easy");
                game_intent.putExtra("id", selectedImage);
            }

            startActivity(game_intent);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_image_chose, Toast.LENGTH_LONG).show();
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RESULT_SELECTED_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RESULT_SELECTED_IMAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //ee
                }
                return;
            }
        }
    }


    private boolean saveBitmapToFileStorage(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.FRANCE).format(new Date());
        File appDirectory = this.getFilesDir();
        String filename = "IMG_TAQUIN_"+ timeStamp +".PNG";
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG ;
        int compressionQuality = 100;

        File imageFile = new File(appDirectory, filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(format, compressionQuality, fos);
            fos.close();
            selectedImageFromGallery = imageFile.getAbsolutePath();
            return true;
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return false;
    }

}
