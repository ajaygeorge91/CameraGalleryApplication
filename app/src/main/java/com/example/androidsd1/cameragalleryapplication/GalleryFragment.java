package com.example.androidsd1.cameragalleryapplication;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.spoyl.android.imagecrop.view.ImageCropView;

import java.io.File;
import java.util.ArrayList;


public class GalleryFragment extends Fragment {


    private ImageCropView imageCropView;
    ImageAdapter myImageAdapter;
    RecyclerView galleryView;


    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt("abc", 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryView = (RecyclerView) view.findViewById(R.id.galleryView);
        imageCropView = (ImageCropView) view.findViewById(R.id.image_crop_view);
        myImageAdapter = new ImageAdapter(getActivity());
        galleryView.setAdapter(myImageAdapter);


        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Uri uri;
        if(isSDPresent){
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }else{
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getActivity().getContentResolver().query(
                uri, columns, null, null, orderBy);
        //Total number of images
        int count = cursor.getCount();

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            String s= cursor.getString(dataColumnIndex);
            myImageAdapter.add(s);
            if(i==0){
                Bitmap bm = decodeSampledBitmapFromUri(s, 220, 220);
                imageCropView.setImageBitmap(bm);
            }
        }
        cursor.close();



        return view;
    }



    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

        ArrayList<String> itemList = new ArrayList<String>();
        private LayoutInflater mInflater;

        // data is passed into the constructor
        public ImageAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        // inflates the cell layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_gallery, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each cell
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220, 220);
            holder.imageView.setImageBitmap(bm);
        }

        // total number of cells
        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public void add(String s) {
            itemList.add(s);
            notifyDataSetChanged();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

            }
        }

    }



    public static Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public static int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

}
