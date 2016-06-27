package com.vmokshagroup.dlnaplayer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.vmokshagroup.dlnaplayer.R;
import com.vmokshagroup.dlnaplayer.modelclass.MimeTypeMap;

import java.util.List;


public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    VideoView v;
    MediaController mediaController;
    boolean seekAllowed = false;
    ProgressBar progresser_bar;
    PackageManager packageManager;
    RelativeLayout complete_layout;
    // private static ProgressDialog progressDialog;


    @TargetApi(17)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_video_player);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mediaController = new MediaController(this);
        v = (VideoView) findViewById(R.id.videoView);
        complete_layout=(RelativeLayout)findViewById(R.id.complete_layout);
        progresser_bar = (ProgressBar) findViewById(R.id.progresser_bar);

        complete_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mediaController.show();
                return true;
            }
        });
        //TODO:
        //getting the list of media players


       /* packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,"1");
        intent.setData(uri);
        List<ResolveInfo> playerList;
        playerList = packageManager.queryIntentActivities(intent, 0);
        startActivity(intent);*/


        progresser_bar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);


//TODO:Anshika Code

        final MediaPlayer.OnInfoListener onInfoToPlayStateListener = new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
//

                        progresser_bar.setVisibility(View.GONE);

                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
//

                        progresser_bar.setVisibility(View.VISIBLE);

                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
//
                        progresser_bar.setVisibility(View.GONE);

                        return true;
                    }
                }
                return false;
            }

        };

        v.setOnInfoListener(onInfoToPlayStateListener);

    }



    @Override
    public void onCompletion(MediaPlayer v) {
        finish();
    }

    //Convenience method to show a video
   /* public static void showRemoteVideo(Context ctx, String url) {
        Intent i = new Intent(ctx, VideoPlayerActivity.class);

        i.putExtra("url", url);
        ctx.startActivity(i);
    }*/

    //call the broadcast receiver

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);

                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        showVideo();

                        break;
                    case WifiManager.WIFI_STATE_DISABLED:

                        progresser_bar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "No wifi connection", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:

                        break;

                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);

    }

    private void showVideo() {
        try {

            int position = GridVideoActivity.videoplayerPostion;
            Uri uri = Uri.parse(MediaServerActivity.mVideoGridArray.get(position).getFileIcon());
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getMimeTypeFromUrl(uri.toString());


            if (uri != null) {


                v.setMediaController(mediaController);
                v.setOnCompletionListener(this);
                v.setVideoURI(uri);
                v.start();

                //TODO:

            } else {

                Toast.makeText(getApplicationContext(), "Uri is null", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Must set url extra paremeter in intent.");

            }

        } catch (NullPointerException ex) {
            Toast.makeText(VideoPlayerActivity.this, R.string.info_could_not_start_activity, Toast.LENGTH_SHORT)
                    .show();
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(VideoPlayerActivity.this, R.string.info_no_handler, Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /*  @Override
      public void onBackPressed() {

          finish();

      }
  */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          /*  if(progressDialog.isShowing())
                progressDialog.dismiss();*/
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
