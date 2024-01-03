package com.example.androidphotos87;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import model.Picture;

public class SlideshowAdapter extends RecyclerView.Adapter<SlideshowAdapter.SlideshowViewHolder> {

    private final List<Picture> pictures;

    public SlideshowAdapter(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public SlideshowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slideshow_item, parent, false);
        return new SlideshowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideshowViewHolder holder, int position) {
        Picture picture = pictures.get(position);

        holder.imageView.post(() -> {
            Bitmap bitmap = decodeSampledBitmapFromPath(picture.getPath(), holder.imageView.getWidth(), holder.imageView.getHeight());
            holder.imageView.setImageBitmap(bitmap);
        });
    }
    @Override
    public int getItemCount() {
        return pictures.size();
    }

    static class SlideshowViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        SlideshowViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
        }
    }
    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
