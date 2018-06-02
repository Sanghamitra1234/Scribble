package com.example.sang.scribble;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

import static com.example.sang.scribble.R.menu.menu_action;


public class MainActivity extends AppCompatActivity {
    //private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar_bottom;
    private Toolbar toolbar;
    private customView customView;

    public MainActivity() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar_bottom = findViewById(R.id.toolbar_bottom);
        //toolbar=findViewById(R.id.m_toolbarUp);
        customView = findViewById(R.id.custom_view);

        mToolbar_bottom.inflateMenu(R.menu.menu);
        mToolbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleDrawingIconTouched(item.getItemId());
                return false;
            }
        });

    }

    private void handleDrawingIconTouched(int itemId) {
        switch (itemId) {
            case R.id.action_delete:
                deleteDialog();
                break;
            case R.id.action_erase:
                int count = 0;
                count++;
                    customView.erase(true);
                break;
            case R.id.action_color:
                openColorPicker();
                break;
            case R.id.action_save:
                saveThisDrawing();
                break;
            case R.id.action_share:
                shareDrawing();
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void deleteDialog() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(getString(R.string.delete_drawing));
        deleteDialog.setMessage(getString(R.string.new_drawing_warning));
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                customView.eraseAll();
                dialog.dismiss();
            }
        });
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.show();
    }

    private void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(this);
        final ArrayList<String> colors = new ArrayList<>();
        colors.add("#258180");
        colors.add("#3C8D2F");
        colors.add("#20724F");
        colors.add("#6a3ab2");
        colors.add("#323299");
        colors.add("#800080");
        colors.add("#b79716");
        colors.add("#966d37");
        colors.add("#b77231");
        colors.add("#808000");

        colorPicker.setRoundColorButton(true).setColumns(5).setColorButtonTickColor(Color.parseColor("#f8f8ff")).setDefaultColorButton(Color.parseColor("#f84c44")).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                customView.setPaintColor(color);
                // customView.setPrev_paintColor(color);
            }

            @Override
            public void onCancel() {
            }
        }).show();

    }

    public void saveDrawingDialog() {
        //save drawing attach to Notification Bar and let User Open Image to share.
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveThisDrawing();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    public void saveThisDrawing() {
        String path = Environment.getExternalStorageDirectory().toString();
        path = path + "/" + getString(R.string.app_name);
        File dir = new File(path);
        //save drawing
        customView.setDrawingCacheEnabled(true);

        //attempt to save
        String imTitle = "Drawing" + "_" + System.currentTimeMillis() + ".png";
        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), customView.getDrawingCache(),
                imTitle, "a drawing");

        try {
            if (!dir.isDirectory() || !dir.exists()) {
                dir.mkdirs();
            }
            customView.setDrawingCacheEnabled(true);
            File file = new File(dir, imTitle);
            FileOutputStream fOut = new FileOutputStream(file);
            Bitmap bm = customView.getDrawingCache();
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Oops! Image could not be saved. Do you have enough space in your device?1");
            alert.setPositiveButton("OK", null);
            alert.show();

        } catch (IOException e) {
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! Image could not be saved. Do you have enough space in your device2?", Toast.LENGTH_SHORT);
            unsavedToast.show();
            e.printStackTrace();
        }


        if (imgSaved != null) {
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
            savedToast.show();
        }

        customView.destroyDrawingCache();
    }

    private void shareDrawing() {
        customView.setDrawingCacheEnabled(true);
        customView.invalidate();

        Bitmap bitmap = Bitmap.createBitmap(customView.getWidth(), customView.getHeight(), Bitmap.Config.RGB_565);
        customView.draw(new Canvas(bitmap));

        try {

            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.jpg"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.jpg");
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.sang.scribble.fileprovider", newFile);


        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

    }
}




