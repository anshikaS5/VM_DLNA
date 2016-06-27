package com.vmokshagroup.dlnaplayer.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vmokshagroup.dlnaplayer.R;
import com.vmokshagroup.dlnaplayer.adapter.GridVideoAdaptor;
import com.vmokshagroup.dlnaplayer.modelclass.MimeTypeMap;

public class GridVideoActivity extends AppCompatActivity {
    RecyclerView gridRecyclerView;
    Toolbar toolbar;
    GridVideoAdaptor gridVideoAdaptor;
    private LruCache<String, Bitmap> mMemoryCache;
    private TextView mVideo_number, mVideo_text;
    private ImageView mSettingsImage, mRefreshImage;
    public  static int videoplayerPostion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_video);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        unitiliazeUi();

        gridRecyclerView = (RecyclerView) findViewById(R.id.recycler_grid_view);
        mVideo_text = (TextView) findViewById(R.id.video_text);
        mVideo_number = (TextView) findViewById(R.id.video_number);

        mVideo_number.setText("(" + MediaServerActivity.mVideoGridArray.size() + ")");
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        gridVideoAdaptor = new GridVideoAdaptor(GridVideoActivity.this, MediaServerActivity.mVideoGridArray);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridRecyclerView.setLayoutManager(new GridLayoutManager(GridVideoActivity.this, 3));
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridRecyclerView.setLayoutManager(new GridLayoutManager(GridVideoActivity.this, 2));
        }
        gridRecyclerView.setItemAnimator(new DefaultItemAnimator());
        gridRecyclerView.setAdapter(gridVideoAdaptor);
        gridRecyclerView.addOnItemTouchListener(new GridVideoAdaptor.RecyclerTouchListener(this, gridRecyclerView, new GridVideoAdaptor.ClickListener() {
            @Override
            public void onClick(View view, int position) {



                videoplayerPostion=position;
                Intent i=new Intent(GridVideoActivity.this,VideoPlayerActivity.class);
                startActivity(i);
                position = position;
               /* kidDetails(mKidCodeArray.get(position).getChildCode());*/

                //TODO:uncomment the code if above one is not working
                /*try {
                    Uri uri = Uri.parse(MediaServerActivity.mVideoGridArray.get(position).getFileIcon());
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getMimeTypeFromUrl(uri.toString());
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    startActivity(intent);
                } catch (NullPointerException ex) {
                    Toast.makeText(GridVideoActivity.this, R.string.info_could_not_start_activity, Toast.LENGTH_SHORT)
                            .show();
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(GridVideoActivity.this, R.string.info_no_handler, Toast.LENGTH_SHORT)
                            .show();
                }*/
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void unitiliazeUi() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grid_video_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(GridVideoActivity.this, SettingActivity.class));
            return true;
        }
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}
