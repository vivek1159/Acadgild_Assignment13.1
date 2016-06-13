package com.example.vivek.assignment131;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    EditText url;
    Button DownloadButton;
    ProgressBar progressBar;
    ImageView imageView;
    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = (EditText) findViewById(R.id.url);
        DownloadButton = (Button) findViewById(R.id.DownloadButton);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    public void onClickStartDownload(View v) {

        final String downloadUrl = url.getText().toString();

        final ContextWrapper contextWrapper = new ContextWrapper(this);

        final int[] count = {0};
        final int[] total = {0};

        Runnable r = new Runnable() {
            @Override
            public void run() {

                synchronized (this) {
                    try {
                        URL url1 = new URL(downloadUrl);
                        URLConnection connection = url1.openConnection();
                        connection.connect();

                        int total_length = connection.getContentLength();
                        Log.d("TAG:", "total length " + total_length);

                        InputStream inputStream = new BufferedInputStream(url1.openStream(), 4096);
                        OutputStream outputStream = new FileOutputStream(contextWrapper.getFilesDir() + "/image.jpg");
                        byte[] data = new byte[2048];

                        while ((count[0] = inputStream.read(data)) != -1) {
                            outputStream.write(data,0,count[0]);
                            total[0] += count[0];

                            int progress = (total[0] * 100) / total_length;
                            Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putInt("progress", progress);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(r);
        thread.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                progressBar.setProgress(data.getInt("progress"));

                if (data.getInt("progress")>=100) {
                    Log.d("TAG:", "Hurrah download done. progress " + data.getInt("progress"));
                    progressBar.setVisibility(View.INVISIBLE);
                    String image_path = contextWrapper.getFilesDir() + "/image.jpg";
                    imageView.setImageDrawable(Drawable.createFromPath(image_path));
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        };
    }
}
