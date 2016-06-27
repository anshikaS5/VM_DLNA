package com.vmokshagroup.dlnaplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vmokshagroup.dlnaplayer.R;
import com.vmokshagroup.dlnaplayer.modelclass.VideoGridModel;
import com.vmokshagroup.dlnaplayer.util.ImageLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by mehtermu on 22-04-2016.
 */
public class GridVideoAdaptor extends RecyclerView.Adapter<GridVideoAdaptor.MyViewHolder> {
    ArrayList<VideoGridModel> videoGridArrayList;
    public Context context;
    String strDuration;

    public static ImageLoader imageLoader;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, duration;
        public ImageView thumbNail;
        public ProgressBar pb;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.video_title);
            duration = (TextView) view.findViewById(R.id.video_duration);
            thumbNail = (ImageView) view.findViewById(R.id.video_thumbnale);
            pb = (ProgressBar) view.findViewById(R.id.progresser_bar);
        }
    }

    public GridVideoAdaptor(Context context, ArrayList<VideoGridModel> videoGridArrayList) {
        this.videoGridArrayList = videoGridArrayList;
        this.context = context;
        if (imageLoader == null)
            imageLoader = new ImageLoader(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_grid_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.itemView.invalidate();
        final VideoGridModel videoGridModel = videoGridArrayList.get(position);
        String strTitle = videoGridModel.getTitle();
        holder.title.setText(strTitle);
        strDuration = "";
        if (videoGridModel.getDuration() == null) {
            new TaskDuration() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    strDuration = s;
                    holder.duration.setText(strDuration);
                    videoGridModel.setDuration(strDuration);
                }
            }.execute(videoGridModel.getFileIcon());
        } else {
            holder.duration.setText(videoGridModel.getDuration());
        }
        Bitmap b = ThumbnailUtils.createVideoThumbnail(videoGridModel.getFileIcon(), MediaStore.Video.Thumbnails.MINI_KIND);
        if (videoGridModel.getImagebitmap() == null) {
            loadBitmap(videoGridModel.getFileIcon(), holder.thumbNail, holder.pb, position);
        } else {
            Bitmap bitmap1 = imageLoader.displayImage(videoGridModel.getFileIcon());
            holder.thumbNail.setImageBitmap(bitmap1);
        }

    }


    @Override
    public int getItemCount() {
        return videoGridArrayList.size();
    }

    public class TaskDuration extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bmp = null;
            String time = null;
            try {
                if (Build.VERSION.SDK_INT >= 14)
                    retriever.setDataSource(params[0], new HashMap<String, String>());
                else
                    retriever.setDataSource(params[0]);

                time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            } catch (Exception e) {
                Log.i("", "");
                FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
                try {
                    fmmr.setDataSource(params[0]);
                    time = fmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } finally {
                    fmmr.release();
                }
            }
            if (time != null) {
                DecimalFormat formatter = new DecimalFormat("00");
                long timeInmillisec = Long.parseLong(time);
                long duration = timeInmillisec / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;
                long seconds = duration - (hours * 3600 + minutes * 60);
                if (hours == 0) {
                    return formatter.format(minutes) + ":" + formatter.format(seconds);
                } else {
                    return formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds);
                }

            } else {
                return "00:00";
            }

        }
    }

    public class TaskThumbNail extends AsyncTask<String, Integer, Bitmap> {
        Bitmap bmp;

        @Override
        protected Bitmap doInBackground(String... params) {
            FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
            try {
                fmmr.setDataSource(params[0]);

                Bitmap b = fmmr.getFrameAtTime(400000);
//                if (b != null) {
//                    Bitmap b2 = fmmr.getFrameAtTime(400000, FFmpegMediaMetadataRetriever.OPTION_NEXT_SYNC);
//                    fmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
//                    if (b2 != null) {
//                        b = b2;
//                    }
//                }

                if (b != null) {
                    bmp = b;

                } else {
                    Log.e("Thumbnail", "Failed to extract frame");
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } finally {
                fmmr.release();
            }
//            imageView.setImageBitmap(b);

            return bmp;
        }
    }

    public void loadBitmap(final String url, final ImageView imageView, final ProgressBar pb, final int p) {

        final Bitmap bitmap = imageLoader.displayImage(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {


            TaskThumbNail taskThumbNail = new TaskThumbNail() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pb.setVisibility(View.VISIBLE);
                    pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                    pb.setProgress(values[0]);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    pb.setVisibility(View.INVISIBLE);
                    imageView.setImageBitmap(bitmap);
                    imageLoader.queuePhoto(url, imageView, bitmap);
                    videoGridArrayList.get(p).setImagebitmap(bitmap);
                }
            };
            taskThumbNail.execute(url);
        }
    }


    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GridVideoAdaptor.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GridVideoAdaptor.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


}
