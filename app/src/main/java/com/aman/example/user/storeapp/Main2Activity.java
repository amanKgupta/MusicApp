package com.aman.example.user.storeapp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main2Activity extends AppCompatActivity {
    ListView listview;
    SimpleCursorAdapter setListAdapter;
    TextView textView,maxtime;
    Cursor cursor;
    RelativeLayout layout;
    Context context;
    Uri uri;
    private int playingPostion=0;
    Button previous,next,psong,nsong;
    SeekBar seekBar;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayList<String> names = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    Button start,stop;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000;
    private int currentSongIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listview = (ListView)findViewById(R.id.songsList);
       // autoCompleteTextView = AutoCompleteTextView.class.cast(findViewById(R.id.autoCompleteTextView));
        maxtime = (TextView)findViewById(R.id.maxtime);
         psong =(Button)findViewById(R.id.previous);
         nsong = (Button)findViewById(R.id.next);
        String Album = getIntent().getStringExtra("Album");
        String path = getIntent().getStringExtra("Image");
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        if (path ==null)
        {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.rescue);
            imageView.setImageBitmap(largeIcon);
        }
        else {
            imageView.setImageBitmap(bitmap);
        }

        textView =(TextView)findViewById(R.id.album);
        textView.setText(Album);

            final String[] columns = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Playlists.Members.ALBUM};
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
              cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,columns,selection, null,
                                                   "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

            if(cursor!=null && cursor.getCount()>0) {
                final String[] displayFields = new String[]{MediaStore.Audio.Media.TITLE};
                final int[] displayViews = new int[]{R.id.txt};
                //setListAdapter(this,android.R.layout.simple_list_item_1, cursor, displayFields,displayViews));

                setListAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, displayFields, displayViews);
                 previous = (Button)findViewById(R.id.button);
                  next = (Button)findViewById(R.id.button3);
                listview.setAdapter(setListAdapter);
                start =(Button)findViewById(R.id.button);
                stop =(Button)findViewById(R.id.button2);
                layout = (RelativeLayout)findViewById(R.id.l);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Toast.makeText(Main2Activity.this, "touch events", Toast.LENGTH_SHORT).show();
                         mediaPlayer = new MediaPlayer();

                        layout.setVisibility(View.VISIBLE);
                       // Log.e("Media uri", MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {

                            Log.e("path", ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)+"");
                             uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
                          //  mediaPlayer.create(Main2Activity.this,Uri.parse(Environment.getExternalStorageDirectory().getPath()+cursor.moveToPosition(position) ));
                            mediaPlayer.setDataSource(getApplicationContext(),uri);

                            mediaPlayer.prepare();

                      //  mediaPlayer.setDataSource(getApplicationContext(), uri);

                          // mediaPlayer.prepare();

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        if (mediaPlayer.isPlaying()  )
                        {
                                mediaPlayer.stop();
                            //mediaPlayer.reset();
                        }
                        else{

                            mediaPlayer.start();

                             }
                        startPlayProgressUpdater();
                     seekBar.setMax(mediaPlayer.getDuration());
                        int progress=setProgressText();
                        seekBar.setProgress(progress);
                        seekBar.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                seekChange(v);

                                return false;
                            }
                        });
//                        try {
//                           // mediaPlayer.prepare();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.e("Prepare", String.valueOf(e));
//                        }



                    }
                });
                psong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mediaPlayer.setDataSource(getApplicationContext(),uri);
                            if (!mediaPlayer.isPlaying())
                            {

                                mediaPlayer.start();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }
                });
                
                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      music();
                    }
                });
               previous.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       setProgressText();
                       int currentPosition = mediaPlayer.getCurrentPosition();
                       if (currentPosition - seekBackwardTime >= 0)
                       {
                           mediaPlayer.seekTo(currentPosition-seekBackwardTime);
                       }
                       else
                           mediaPlayer.seekTo(0);
                   }
               });
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setProgressText();
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
                            mediaPlayer.seekTo(currentPosition + seekForwardTime);

                        }else{
                            // forward to end position
                           mediaPlayer.seekTo(mediaPlayer.getDuration());
                           // mediaPlayer.seekTo(currentPosition + seekForwardTime);
                        }
                        }

                });

            }

        }
    private void seekChange(View v){
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mediaPlayer.seekTo(sb.getProgress());
            Log.e("current duration",mediaPlayer.getDuration()+"");
           int progress = setProgressText();
             sb.setProgress(progress);
        }
    }
    protected int setProgressText() {

        final int HOUR = 60*60*1000;
        final int MINUTE = 60*1000;
        final int SECOND = 1000;
        TextView curr = (TextView)findViewById(R.id.textView);
         TextView max = (TextView)findViewById(R.id.maxtime);
        int durationInMillis = mediaPlayer.getDuration();
        int curVolume = mediaPlayer.getCurrentPosition();

        int durationHour = durationInMillis/HOUR;
        int durationMint = (durationInMillis%HOUR)/MINUTE;
        int durationSec = (durationInMillis%MINUTE)/SECOND;

        int currentHour = curVolume/HOUR;
        int currentMint = (curVolume%HOUR)/MINUTE;
        int currentSec = (curVolume%MINUTE)/SECOND;

        if(durationHour>0){
           Log.e(" 1 = ",String.format("%02d:%02d:%02d/%02d:%02d:%02d",
                    currentHour,currentMint,currentSec, durationHour,durationMint,durationSec));
            curr.setText(String.format("%02d:%02d:%02d",currentHour,currentMint,currentSec));

            max.setText(String.format("%02d:%02d:%02d",durationHour,durationMint,durationSec));
        }else{
           Log.e(" 1 = ",String.format("%02d:%02d/%02d:%02d",
                    currentMint,currentSec, durationMint,durationSec));
            curr.setText(String.format("%02d:%02d", currentMint,currentSec));
            max.setText(String.format("%02d:%02d", durationMint,durationSec));

        }
        return currentSec;
    }
    public void startPlayProgressUpdater() {


        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    int progress=setProgressText();
                    seekBar.setProgress(progress);
                    startPlayProgressUpdater();

                }
            };
            handler.postDelayed(notification,1000);
        }else{
            mediaPlayer.pause();
            stop.setText(getString(R.string.play_str));
            seekBar.setProgress(0);
        }
    }
    private void music(){
        if (stop.getText() == getString(R.string.play_str)) {
            stop.setText(getString(R.string.pause_str));
            try{

                mediaPlayer.start();
                startPlayProgressUpdater();
              Log.e("seek",String.valueOf(mediaPlayer.getCurrentPosition()));

            }catch (IllegalStateException e) {
                mediaPlayer.pause();
            }
        }else {
            stop.setText(getString(R.string.play_str));
            mediaPlayer.pause();
        }
    }
}

