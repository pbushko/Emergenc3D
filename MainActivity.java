package com.example.pbush.modelhelp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.MAGENTA;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;

public class MainActivity extends Activity {

    ImageView imgView;
    Button load_photo;
    private Bitmap toDisplay;
    String imgDecodableString;
    private static int RESULT_LOAD_IMG = 1;
    private static int MIN_WALL_WIDTH = 5;
    private static int MIN_WALL_LENGTH = 20;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.main_menu_text1);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    public void goToGL(View view) {
        Intent GLIntent = new Intent(MainActivity.this,
                GlActivity.class);
        startActivity(GLIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                Bitmap bmp = null;
                try {
                    bmp = getBitmapFromUri(selectedImage);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                toDisplay = bmp;
                Log.d("walls", "wall coords found: "+Arrays.toString(findWalls()));
                //imgView.setImageBitmap(bmp);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private int[] findWalls() {
        Bitmap bmp = toDisplay;
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        ArrayList coords = new ArrayList<Integer>();
        ArrayList testi = new ArrayList<Integer>();
        ArrayList testj = new ArrayList<Integer>();
        ArrayList testHeight = new ArrayList<Integer>();

        //go through pixel chunks and see if there are black vertical lines
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int pix = bmp.getPixel(i, j);
                if (pix == BLACK) {
                    //checking to see if the black pixel is too close to the edge to be
                    //part of a wall.  if too close, go to the next row of pixels
                    if (i >= w - MIN_WALL_WIDTH)
                        i = w;
                    //if not too close, check if it is a wall!
                    else {
                        //look at if it's black for a whole width
                        if (isWallWidthVertical(bmp, i, j)) {
                            //check if the wall is one we've already found before
                            if (!horizFoundBefore(i, j, toArr(coords))) {
                                //find the y coords of the wall
                                int height = getWallLength(bmp, i, j);

                                if (height >= MIN_WALL_LENGTH) {
                                    coords.add(i + 5);
                                    coords.add(j);
                                    coords.add(i + 5);
                                    coords.add(j + height);
                                    testi.add(i);
                                    testj.add(j);
                                    testHeight.add(height);
                                }
//                                if ((int) coords.get(coords.size() - 1) == j + height) {
//                                    colorWalls(w, h, testi, testj, testHeight, newBmp);
//                                    return toArr(coords);
//                                }
                            }
                            //ofsetting  i so we don't find the same wall
                            i += MIN_WALL_WIDTH * 2;
                        }
                    }
                }
            }
        }
        //setting the coords to a different color so I can see what it finds
        Bitmap newBmp = toDisplay.copy(toDisplay.getConfig(), true);
        colorWalls(w, h, toArr(testi), toArr(testj), toArr(testHeight), newBmp);
        return toArr(coords);
    }

    private int[] toArr(ArrayList<Integer> is) {
        int size = is.size();
        if (size == 0) {
            int retVal[] = new int[4];
            return retVal;
        }
        int retVal[] = new int[size];
        for (int i = 0; i < size; i++) {
            retVal[i] = is.get(i);
        }
        return retVal;
    }

    private boolean isWallWidthVertical(Bitmap bmp, int x, int y){
        int tot = 0;
        for (int k = 1; k < MIN_WALL_WIDTH + 1 && k > 0; k++) {
            int check = bmp.getPixel(x + k, y);
            //can't be a wall if it's missing a black pixel
            if (check == BLACK) {
                tot++;
            }
        }
        if(tot >= MIN_WALL_WIDTH - 3) {
            return true;
        }
        return false;
    }

    private int getWallLength(Bitmap bmp, int x, int y) {
        int count = 0; //how far down the wall goes from it's start
        for (int i = y; i < bmp.getHeight(); i++) {
            if (isWallWidthVertical(bmp, x, i))
                count++;
            else
                return count;
        }
        return count;
    }

    private boolean horizFoundBefore(int i, int j, int[] coords) {
        for(int k = 0; k < coords.length; k+=4) {
            //Log.d("wow", "coords:" + coords[k] + " " + coords[k + 1] + " " + i + " " + j);
            //when looking at the x coordinate, is it within the same y vals?
            if(k%4 == 0 && coords[k] - (MIN_WALL_WIDTH * 3) <= i && coords[k] + (MIN_WALL_WIDTH * 3) >= i) {
                if (coords[k + 1] <= j && coords[k + 3] >= j) {
                    return true;
                }
            }
        }
        return false;
    }

    private void colorWalls(int w, int h, int[] i, int[] j, int[] height, Bitmap newBmp) {
        int[] wallCol = new int[w*h];
        for (int k = 0; k < wallCol.length; k++) {
            wallCol[k] = MAGENTA;
        }
        for (int k = 0; k < i.length; k++) {
            newBmp.setPixels(wallCol, 0, w, i[k],j[k], MIN_WALL_WIDTH, height[k]);
        }
        imgView.setImageBitmap(newBmp);
        toDisplay = newBmp;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        //detect user touch
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //if there has been a touch, paint a yellow square
            Log.d("paint",
                    "here is the image coords:" + toDisplay.getWidth() + "  " + toDisplay.getHeight());
            Log.d("draw", "you touched here:" + x + "   " + y);
            if (toDisplay != null) {
                int w = toDisplay.getWidth();
                int h = toDisplay.getHeight();
                //if within the image boundaries, draw
                if (x <= w && y <= h) {
                    Bitmap newBmp = toDisplay.copy(toDisplay.getConfig(), true);
                    //filling pixels that we'll put in
                    int[] pix = new int[x*y];
                    for (int i = 0; i < pix.length; i++) {
                        pix[i] = YELLOW;
                    }
                    //Log.d("paint", "got here!");
                    //newBmp.setPixel(x, y, RED);
                    if (x > 8 && y > 8)
                        newBmp.setPixels(pix, 0, w, (x - 8), (y - 8), 16, 16);
                    imgView.setImageBitmap(newBmp);
                }
            }
        }
        return true;
    }

}
