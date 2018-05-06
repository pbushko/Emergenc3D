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
    public static Bitmap toDisplay;
    private static Bitmap userBitmap;
    String imgDecodableString;
    static float col[] = new float[3];
    private static int RESULT_LOAD_IMG = 1;
    private static int MIN_WALL_WIDTH = 5;
    private static int MIN_WALL_LENGTH_VERT = 20;
    private static int MIN_WALL_LENGTH_HOR = 15;
    private static float BOUND = 0.3f;
    public static ArrayList userCoords = new ArrayList<Integer>();
    public static ArrayList windowCoords = new ArrayList<Integer>();

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
                userBitmap = toDisplay.copy(toDisplay.getConfig(), true);
                imgView.setImageBitmap(userBitmap);
                //resetting the user input when a new image is selected
                userCoords = new ArrayList<Integer>();
                windowCoords = new ArrayList<Integer>();
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

    public static int[] findWalls() {
        Bitmap bmp = toDisplay;
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        ArrayList coords = new ArrayList<Integer>();

        //go through pixel chunks and see if there are black vertical lines
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int pix = bmp.getPixel(i, j);
                Color.RGBToHSV(Color.red(pix), Color.green(pix), Color.blue(pix), col);
                if (col[2] <= BOUND) {
                    //checking to see if the black pixel is too close to the edge to be
                    //part of a wall.  if too close, go to the next row of pixels
                    //look at if it's black for a whole width; checking the "vertical" walls
                    //check if the wall is one we've already found before
                    if (i < w - MIN_WALL_WIDTH && !vertFoundBefore(i, j, toArr(coords)) && isWallWidthVertical(bmp, i, j)) {
                        //find the y coords of the wall
                        int height = getWallLength(bmp, i, j, h);
                        if (height >= MIN_WALL_LENGTH_VERT) {
                            coords.add(i);
                            coords.add(j);
                            coords.add(i + MIN_WALL_WIDTH * 2);
                            coords.add(j + height);

                            if (j+height+MIN_WALL_WIDTH+5 < h && i-5 > 0 && isWindowVert(bmp, i, (j+height+1))) {
                                int windowHeight = getWindowLength(bmp, i, j+height+1, h);
                                if(windowHeight >= MIN_WALL_LENGTH_VERT) {
                                    windowCoords.add(i);
                                    windowCoords.add(j + height);
                                    windowCoords.add(i + MIN_WALL_WIDTH * 2);
                                    windowCoords.add(j + height + windowHeight);
                                }
                            }
                        }
                    }
                    //checking the "horizontal" walls
                    if(j < h - MIN_WALL_WIDTH && !horizFoundBefore(i, j, toArr(coords)) && isWallWidthHorizontal(bmp, i, j)) {
                        int width = getWallWidth(bmp, i, j, w);
                        if (width >= MIN_WALL_LENGTH_HOR) {
                            coords.add(i);
                            coords.add(j);
                            coords.add(i + width);
                            coords.add(j + MIN_WALL_WIDTH * 2);

                            if (i+width+MIN_WALL_WIDTH+5 < w && j-5 > 0 && isWindowHoriz(bmp, (i+width+1), j)) {
                                int windowWidth = getWindowWidth(bmp, i+width+1, j, w);
                                if(windowWidth >= MIN_WALL_LENGTH_HOR) {
                                    windowCoords.add(i + width);
                                    windowCoords.add(j);
                                    windowCoords.add(i + width + windowWidth);
                                    windowCoords.add(j + MIN_WALL_WIDTH * 2);
                                }
                            }
                        }
                    }
                }
            }
        }
        return toArr(coords);
    }

    private static boolean isWallWidthVertical(Bitmap bmp, int x, int y){
        int tot = 0;
        for (int k = 1; k < MIN_WALL_WIDTH + 1 && k > 0; k++) {
            int pix = bmp.getPixel(x + k, y);
            Color.RGBToHSV(Color.red(pix), Color.green(pix), Color.blue(pix), col);
            //can't be a wall if it's missing a black pixel
            if (col[2] <= BOUND) {
                tot++;
            }
        }
        if(tot >= MIN_WALL_WIDTH) {
            return true;
        }
        return false;
    }

    private static boolean isWallWidthHorizontal(Bitmap bmp, int x, int y){
        int tot = 0;
        for (int k = 1; k < MIN_WALL_WIDTH + 1 && k > 0; k++) {
            int pix = bmp.getPixel(x, y+k);
            Color.RGBToHSV(Color.red(pix), Color.green(pix), Color.blue(pix), col);
            //can't be a wall if it's missing a black pixel
            if (col[2] <= BOUND) {
                tot++;
            }
        }
        if(tot >= MIN_WALL_WIDTH) {
            return true;
        }
        return false;
    }

    private static int getWallLength(Bitmap bmp, int x, int y, int h) {
        int count = 0; //how far down the wall goes from it's start
        for (int i = y; i < h; i++) {
            if (isWallWidthVertical(bmp, x, i))
                count++;
            else
                return count;
        }
        return count;
    }

    private static int getWallWidth(Bitmap bmp, int x, int y, int w) {
        int count = 0; //how far down the wall goes from it's start
        for (int i = x; i < w; i++) {
            if (isWallWidthHorizontal(bmp, i, y))
                count++;
            else
                return count;
        }
        return count;
    }

    private static boolean vertFoundBefore(int i, int j, int[] coords) {
        int len = coords.length;
        for(int k = 0; k < len; k+=4) {
            //when looking at the x coordinate, is it within the same y vals?
            if(coords[k] - (MIN_WALL_WIDTH * 3) <= i && coords[k] + (MIN_WALL_WIDTH * 3) >= i) {
                if (coords[k + 1] <= j && coords[k + 3] >= j) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean horizFoundBefore(int i, int j, int[] coords) {
        int len = coords.length;
        for(int k = 0; k < len; k+=4) {
            //when looking at the y coordinate, is it within the same x vals?
            if(coords[k+1] - (MIN_WALL_WIDTH * 5) <= j && coords[k+1] + (MIN_WALL_WIDTH * 5) >= j) {
                if (coords[k] <= i && coords[k + 2] >= i) {
                    return true;
                }
            }
        }
        return false;
    }

    //look to see if a window is attached to the wall
    private static boolean isWindowVert(Bitmap bmp, int x, int y) {
        if (x-2 < 0 || x+MIN_WALL_WIDTH*2+2 >= bmp.getWidth())
            return false;
        int pix[] = new int[5+MIN_WALL_WIDTH*2];
        pix[0] = bmp.getPixel(x-2, y);
        pix[1] = bmp.getPixel(x-1, y);
        pix[2] = bmp.getPixel(x, y);
        pix[3] = bmp.getPixel(x+1, y);
        pix[4] = bmp.getPixel(x+2, y);
        for(int k = -1; k < pix.length-6; k++) {
            pix[k+6] = bmp.getPixel(x+MIN_WALL_WIDTH+k, y);
        }
        float cols[] = new float[pix.length];
        for (int k = 0; k < pix.length; k++) {
            Color.RGBToHSV(Color.red(pix[k]), Color.green(pix[k]), Color.blue(pix[k]), col);
            cols[k] = col[2];
        }
        return ((cols[0]<=BOUND || cols[1]<=BOUND || cols[2]<=BOUND || cols[3]<=BOUND || cols[4]<=BOUND) && check(cols));
    }

    private static boolean check(float[] toCheck) {
        for(int i = 3; i < toCheck.length; i++) {
            if(toCheck[i] <= BOUND)
                return true;
        }
        return false;
    }

    private static boolean isWindowHoriz(Bitmap bmp, int x, int y) {
        if (y-2 < 0 || y+MIN_WALL_WIDTH*2+2 >= bmp.getHeight())
            return false;
        int pix[] = new int[5+MIN_WALL_WIDTH*2];
        pix[0] = bmp.getPixel(x, y-2);
        pix[1] = bmp.getPixel(x, y-1);
        pix[2] = bmp.getPixel(x, y);
        pix[3] = bmp.getPixel(x, y+1);
        pix[4] = bmp.getPixel(x, y+2);
        for(int k = -1; k < pix.length-6; k++) {
            pix[k+6] = bmp.getPixel(x, y+MIN_WALL_WIDTH+k);
        }
        float cols[] = new float[pix.length];
        for (int k = 0; k < pix.length; k++) {
            Color.RGBToHSV(Color.red(pix[k]), Color.green(pix[k]), Color.blue(pix[k]), col);
            cols[k] = col[2];
        }
        return ((cols[0]<=BOUND || cols[1]<=BOUND || cols[2]<=BOUND || cols[3]<=BOUND || cols[4]<=BOUND) && check(cols));
    }

    private static int getWindowLength(Bitmap bmp, int x, int y, int h) {
        int count = 0; //how far down the wall goes from it's start
        for (int i = y; i+MIN_WALL_WIDTH+5 < h; i++) {
            if (isWallWidthVertical(bmp, x, i))
                return count;
            else if (isWindowVert(bmp, x, i))
                count++;
            else
                return count;
        }
        return count;
    }

    private static int getWindowWidth(Bitmap bmp, int x, int y, int w) {
        int count = 0; //how far down the wall goes from it's start
        for (int i = x; i+MIN_WALL_WIDTH+5 < w; i++) {
            if (isWallWidthHorizontal(bmp, i, y))
                return count;
            else if (isWindowHoriz(bmp, i, y))
                count++;
            else
                return count;
        }
        return count;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        //detect user touch
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //if there has been a touch, paint a yellow square
            if (toDisplay != null) {
                int w = toDisplay.getWidth();
                int h = toDisplay.getHeight();
                //if within the image boundaries, draw
                if (x <= w && y <= h) {
                    //filling pixels that we'll put in
                    int[] pix = new int[x*y];
                    for (int i = 0; i < pix.length; i++) {
                        pix[i] = YELLOW;
                    }

                    userCoords.add(x);
                    userCoords.add(y);
                    userCoords.add(x+(MIN_WALL_WIDTH*2));
                    userCoords.add(y+(MIN_WALL_WIDTH*2));

                    if (x > 8 && y > 8)
                        userBitmap.setPixels(pix, 0, w, (x - 8), (y - 8), 16, 16);
                    imgView.setImageBitmap(userBitmap);
                }
            }
        }
        return true;
    }

    public static int[] getUserCoords() {
        return toArr(userCoords);
    }
    public static int[] getWindowCoords() {
        return toArr(windowCoords);
    }

    private static int[] toArr(ArrayList<Integer> is) {
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
}
