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

import static android.graphics.Color.HSVToColor;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;
import static android.graphics.Color.colorToHSV;

public class MainActivity extends Activity {

    ImageView imgView;
    Button load_photo;
    private Bitmap toDisplay;
    //String imgDecodableString;
    private static int RESULT_LOAD_IMG = 1;


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
                //making the Bitmap in black and white
//                int h = bmp.getHeight();
//                int w = bmp.getWidth();
//                int[] newColors = new int[(h * w)];
//                //go through each pixel and make it black and white
//                for (int i = 0; i < w; i++) {
//                    for (int j = 0; j < h; j++) {
//                        //converting to HSL, then setting the color's saturation to 0
//                        int pix = bmp.getPixel(i, j);
//                        int grey = (Color.red(pix) + Color.blue(pix) + Color.green(pix)) / 3;
//                        //putting the newly saturated color into the newColors array
//                        newColors[(w*j) + i] =
//                                Color.argb(Color.alpha(pix), grey, grey, grey);
//                    }
//                }
//                //make a new bitmap from newColors
//                Bitmap newBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//                newBmp.setPixels(newColors, 0, w, 0, 0, w, h);
                toDisplay = bmp;
                imgView.setImageBitmap(bmp);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

//    private int[] findWalls() {
//
//
//    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
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
                    Log.d("paint", "got here!");
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
