
package com.example.puzzlegame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    int n = -1;
    GridView gridView;
    RelativeLayout linear;
    ImageView image1;
    TextView textView;
    ArrayList<Bitmap> bitmapArray;
    int rows1, cols1;
    SeekBar seekBar;
    ArrayList<ImageView> images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linear = (RelativeLayout) findViewById(R.id.linear);
        image1 = (ImageView) findViewById(R.id.imageButton);
        textView = (TextView) findViewById(R.id.textView_2);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                n = -1;
                progress = progress * 2;
                if (progress == 0) {
                    progress = 1;
                }
                Bitmap bmOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.bentley);
                image1.setImageBitmap(bmOriginal);
                ImageView imageView = image1;
                linear.removeView(image1);
                textView.setText(String.valueOf(progress));

                rows1 = cols1 = progress;

                splitImage(imageView);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void splitImage(ImageView image1) {
        int rows, cols, chunkHeight, chunkWidth;
        bitmapArray = new ArrayList<>(rows1 * cols1);
        bitmapArray.removeAll(bitmapArray);

        Bitmap bitmap = ((BitmapDrawable) image1.getDrawable()).getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        rows = cols = (int) Math.sqrt(rows1 * cols1);
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        int y = 0;
        for (int i = 0; i < rows; i++) {
            int x = 0;
            for (int j = 0; j < cols; j++) {
                bitmapArray.add(Bitmap.createBitmap(scaledBitmap, x, y, chunkWidth, chunkHeight));
                x += chunkWidth;
            }
            y += chunkHeight;
        }

        bitmapToImage(bitmapArray);
    }

    private void bitmapToImage(final ArrayList<Bitmap> bitmap) {

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(cols1);

        images = new ArrayList<>();
        images.removeAll(images);

        int count2 = 0;
        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols1; j++) {
                ImageView image = new ImageView(this);
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(gridView.getWidth() / rows1, gridView.getWidth() / cols1));
                image.setImageBitmap(bitmap.get(count2));
                images.add(image);
                images.get(count2).setTag(String.valueOf(count2));
                count2++;
            }
        }

        Collections.shuffle(images);

        gridView.setAdapter(new GridAdapter(this, images));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (n == -1 && images.size() != 1) {
                    n = position;
                    show(images);
                    images.get(position).setAlpha((float) 0.5);
                } else if (rows1 == 2) {
                    if ((n == 0 && position != 3) || (n == 3 && position != 0) || (n == 1 && position != 2) || (n == 2 && position != 1)) {
                        Collections.swap(images, n, position);
                        images.get(position).setAlpha((float) 1);
                        show(images);
                        n = -1;
                    }
                } else {
                    if (((position != n + 1 - rows1) && (position != n - 1 - rows1) && (position != n + 1 + rows1) && (position != n - 1 + rows1)) && n != -1) {
                        Collections.swap(images, n, position);
                        images.get(position).setAlpha((float) 1);
                        show(images);
                        n = -1;
                    } else if (((n % rows1 == 0) && ((position % rows1) == rows1 - 1)) || ((n % rows1 == rows1 - 1) && ((position % rows1) == 0))) {
                        if (n != -1) {
                            Collections.swap(images, n, position);
                            images.get(position).setAlpha((float) 1);
                            show(images);
                            n = -1;
                        }
                    }
                }

                int k = 0;
                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i).getTag() == String.valueOf(i)) {
                        k++;
                    }
                }

                if (k == images.size()) {
                    Toast.makeText(MainActivity.this, "G'alaba", Toast.LENGTH_SHORT).show();
                    seekBar.setProgress(0);
                    MainActivity.super.onPause();
                    textView.setText("");
                }
            }
        });
    }

    private void show(ArrayList<ImageView> images) {
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(cols1);
        gridView.setAdapter(new GridAdapter(this, images));
    }


    public void resetButton(View view) {
        if (!(seekBar.getProgress() == 0)) {
            seekBar.setProgress(0);
            super.onPause();
            textView.setText("");
            rows1 = 1;
            cols1 = 1;
            images.removeAll(images);
            bitmapArray.removeAll(bitmapArray);
            images.add(image1);
            show(images);
            n = -1;
        }
    }
}


