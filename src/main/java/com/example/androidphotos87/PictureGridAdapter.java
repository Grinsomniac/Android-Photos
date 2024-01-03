package com.example.androidphotos87;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import model.Picture;
import java.util.ArrayList;

public class PictureGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Picture> pictures;
    private String albumName;
    private String sourceActivityName;
    public static final int REQUEST_CODE = 1;
    public PictureGridAdapter(Context context, ArrayList<Picture> pictures, String albumName) {
        this.context = context;
        this.pictures = pictures;
        this.albumName = albumName;
        this.sourceActivityName = context.getClass().getSimpleName();
    }
    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(240, 240));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Loading the image into the ImageView using the file path
        // using BitmapFactory to decode the image file
        String filePath = pictures.get(position).getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(v -> {
            Picture selectedPicture = pictures.get(position);
            Intent intent = new Intent(context, PictureDisplayActivity.class);
            intent.putExtra("albumName", albumName);
            intent.putExtra("selectedPhotoPath", selectedPicture.getPath());
            intent.putExtra("photoIndex", position);
            intent.putExtra("sourceActivity", sourceActivityName);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        });
        imageView.setOnLongClickListener(v -> {
            // Return false to indicate that the long press is not consumed here
            // and should trigger the context menu.
            return false;
        });
        return imageView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
