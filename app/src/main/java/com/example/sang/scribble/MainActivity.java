package com.example.sang.scribble;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
         mToolbar_bottom=findViewById(R.id.toolbar_bottom);
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
        switch (itemId){
            case R.id.action_delete:
                deleteDialog();
                break;
            case R.id.action_undo:
                customView.onClickUndo();
                break;
            case R.id.action_color:
                    openColorPicker();
                    break;
            case R.id.action_save:
                save();
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
    private void deleteDialog(){
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(getString(R.string.delete_drawing));
        deleteDialog.setMessage(getString(R.string.new_drawing_warning));
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
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
    private void openColorPicker(){
        final ColorPicker colorPicker=new ColorPicker(this);
        final ArrayList<String> colors=new ArrayList<>();
        colors.add("#258174");
        colors.add("#3C8D2F");
        colors.add("#20724F");
        colors.add("#6a3ab2");
        colors.add("#323299");
        colors.add("#800080");
        colors.add("#b79716");
        colors.add("#966d37");
        colors.add("#b77231");
        colors.add("#808000");

        colorPicker.setRoundColorButton(true).setColumns(5).setDefaultColorButton(Color.parseColor("#f84c44")).setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                customView.setPaintColor(color);
            }

            @Override
            public void onCancel(){
            }
        }).show();

    }
    private void save(){

    }

}
