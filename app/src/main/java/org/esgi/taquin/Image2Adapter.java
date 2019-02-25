package org.esgi.taquin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Image2Adapter extends BaseAdapter {

    private final Context mContext;
    private final Bitmap[] mImages;
    private final int[] mPosition = {0, 0, 0, 0};
    private final int mDimension;
    private final ArrayList<Integer> listInts = new ArrayList<>();
    private final ArrayList<Integer> listSolution = new ArrayList<>();
    private final Bitmap blankImg;
    private int blankImgInt = 0;

    public Image2Adapter(Context c, int d, int selectedImage) {

        mContext = c;
        Bitmap img = BitmapFactory.decodeResource(c.getResources(), selectedImage);
        mImages = new Bitmap[d * d];
        mDimension = d;

        /*  boucle pour générer des valeurs aléatoire */
        for (int i = 0; i < (mDimension * mDimension); i++) {
            this.listInts.add(i);
            this.listSolution.add(i);
        }

        /* calcule des dimensions des images */
        int pieceWidth = img.getWidth() / mDimension;
        int pieceHeight = img.getHeight() / mDimension;

        /* hidden image */
        Bitmap tmpBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.unlock);
        blankImg = Bitmap.createScaledBitmap(tmpBitmap, pieceWidth, pieceHeight, true);

        /* decoupage de la bitmap */
        int r = 0;
        for (int i = 0; i < mDimension; i++) {
            for (int j = 0; j < mDimension; j++) {
                Bitmap piece = Bitmap.createBitmap(img, pieceHeight * j, pieceWidth * i, pieceWidth, pieceHeight);
                mImages[listInts.get(r)] = piece;
                r++;
            }
        }

        // mettre l'image cachée à null
        blankImgInt = (mDimension * mDimension) - 1;
        mImages[blankImgInt] = blankImg;

        shuffle();
    }


    public Image2Adapter(Context c, int d, String selectedImage) {

        mContext = c;
        Bitmap img = BitmapFactory.decodeFile(selectedImage);
        mImages = new Bitmap[d * d];
        mDimension = d;

        /*  boucle pour générer des valeurs aléatoire */
        for (int i = 0; i < (mDimension * mDimension); i++) {
            this.listInts.add(i);
            this.listSolution.add(i);
        }

        /* calcule des dimensions des images */
        int pieceWidth = img.getWidth() / mDimension;
        int pieceHeight = img.getHeight() / mDimension;

        /* hidden image */
        Bitmap tmpBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.unlock);
        blankImg = Bitmap.createScaledBitmap(tmpBitmap, pieceWidth, pieceHeight, true);

        /* decoupage de la bitmap */
        int r = 0;
        for (int i = 0; i < mDimension; i++) {
            for (int j = 0; j < mDimension; j++) {
                Bitmap piece = Bitmap.createBitmap(img, pieceHeight * j, pieceWidth * i, pieceWidth, pieceHeight);
                mImages[listInts.get(r)] = piece;
                r++;
            }
        }

        // mettre l'image cachée à null
        blankImgInt = (mDimension * mDimension) - 1;
        mImages[blankImgInt] = blankImg;

        shuffle();
    }


    private void shuffle(){
        Random rand = new Random();
        int max = mDimension * mDimension * rand.nextInt(3200);
        int min = mDimension;
        int randomReps = rand.nextInt((max - min)) + min;

        int toShift = (mDimension * mDimension) -1;
        int positionShift;
        for(int i = 0; i< randomReps; i ++){
            positionShift = randInt(toShift);
            move(positionShift);
        }
    }

    private static int randInt(int max) {
        Random rand = new Random();
        return rand.nextInt((max) + 1);
    }

    public void move(int pos) {

        int index = 0;
        boolean found = false;
        int positionUp;
        int positionDown;
        int positionLeft;
        int positionRight;


        //calcule des mPosition left & right

        positionLeft = pos - 1;
        positionRight = pos + 1;

        if (positionLeft >= 0  && ( pos % mDimension) != 0 ) {
            mPosition[0] = positionLeft;
        } else {
            mPosition[0] = -1;
        }

        if ( positionRight < ( mDimension * mDimension) && (( pos + 1 ) % mDimension) != 0 ) {
            mPosition[1] = positionRight;
        } else {
            mPosition[1] = -1;
        }


        // calcule des mPosition up & down
        positionUp = pos - mDimension;
        positionDown = pos + mDimension;

        if (positionUp >= 0) {
            mPosition[2] = positionUp;
        } else {
            mPosition[2] = -1;
        }

        if (positionDown < (mDimension * mDimension)) {
            mPosition[3] = positionDown;
        } else {
            mPosition[3] = -1;
        }


        for (int i = 0; i < mPosition.length ; i++) {

            if (mPosition[i] == blankImgInt) {
                found = true;
                index = mPosition[i];
                break;
            }
        }

        //si le mouvement est possible
        if (found) {
            blankImgInt = pos;
            mImages[index] = mImages[pos];
            mImages[pos] = blankImg;

            // réorganier le lableau des ordre

            Integer lIndice = listInts.get(index);
            Integer lPos = listInts.get(pos);

            listInts.set(index, lPos);
            listInts.set( pos ,lIndice ) ;
            Log.e("rrr", listInts.toString());
        }

    }

    public Boolean winCheck(){
        boolean win = true;
        for (int i = 0; i<listSolution.size();i++){
            if (!listSolution.get(i).equals(listInts.get(i))){
                win = false;
            }
        }
        return win;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public Object getItem(int position) {
        return mImages[position];
    }

    @Override
    public long getItemId(int position) {
        return listInts.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(3, 3, 3, 3);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(mImages[position]);
        return imageView;
    }

}

