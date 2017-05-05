package com.aman.example.user.storeapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
Cursor cursor;
    ListView listview;
    SimpleCursorAdapter setListAdapter;
    RadioButton internal,external;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView)findViewById(R.id.listview);
        getPermission();
        internal = (RadioButton)findViewById(R.id.radio_pirates);
        internal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contentResolver();

            }
        });

        external = (RadioButton)findViewById(R.id.radio_ninjas);
        external.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Nothing found", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public boolean getPermission() {
        Log.e("permissionb","12");
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            Log.e("permissionb","123243");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 11);

            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contentResolver();
                } else {

                }
                return;
            }
        }
    }

    public  void contentResolver()
{
    final String[] columns = { android.provider.MediaStore.Audio.Albums._ID,
            android.provider.MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART };
   cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,columns, null, null,
           "("+MediaStore.Audio.Albums.ALBUM +") ASC");
    final String[] displayFields = new String[] { MediaStore.Audio.Albums.ALBUM };
    final int[] displayViews = new int[] { android.R.id.text1 };

    //setListAdapter(this,android.R.layout.simple_list_item_1, cursor, displayFields,displayViews));



    setListAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1, cursor, displayFields,displayViews);
    listview.setAdapter(setListAdapter);

    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaPlayer mediaplayer = new MediaPlayer();
           // mediaplayer.selectTrack(position);
            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                if(listview !=null && cursor!=null) {
                  //  String url ="/sdcard/music.mp3";
                    int album = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM);
                    int  image = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                    int  album_id = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
                   // cursor.moveToFirst();
                    cursor.moveToPosition(position);
//                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                        String title=cursor.getString(album) ;
                         String image_path =cursor.getString(image);
                      i.putExtra("Album",title);
                      i.putExtra("AlbumId",album);
                       i.putExtra("Image",image_path);
                    i.putExtra("album_id",album_id);
                      startActivity(i);
                      // finish();
                  //}
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    });
}



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    // Pirates are the best
                    getPermission();
                contentResolver();
                    break;
            case R.id.radio_ninjas:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
}
