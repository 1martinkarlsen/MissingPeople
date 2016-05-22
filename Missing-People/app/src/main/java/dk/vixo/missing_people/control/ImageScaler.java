package dk.vixo.missing_people.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Vixo on 21-05-2016.
 */
public class ImageScaler {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int width     = options.outWidth;
        final int height    = options.outHeight;
        int inSampleSize    = 1;

        if(width > reqWidth || height > reqHeight) {

            final int halfWidth     = width / 2;
            final int halfHeight    = height / 2;

            while((halfWidth / inSampleSize) > reqWidth && (halfHeight / inSampleSize) > reqHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampleBitmapFromByteArray(byte[] imgArr, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imgArr, 0, imgArr.length);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(imgArr, 0, imgArr.length);
    }

    public static Bitmap scaleToListView(Bitmap image) {
        return null;
    }

}
