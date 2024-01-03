package com.example.androidphotos87;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import java.util.ArrayList; // Import or define your Picture class
import java.util.List;

import model.Picture;
//TODO - FIX fullscreen image scaling - Picurture is getting cut off
public class SlideshowActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SlideshowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        viewPager = findViewById(R.id.viewPager);

        // Assuming you have a method to get the list of pictures
        List<Picture> pictures = getAlbumPictures();
        int currentPhotoIndex = getIntent().getIntExtra("currentPhotoIndex", 0); // Default to 0
        adapter = new SlideshowAdapter(pictures);
        viewPager.setAdapter(adapter);

        // Set the ViewPager2 to start at the current photo
        viewPager.setCurrentItem(currentPhotoIndex, false);
    }

    private List<Picture> getAlbumPictures() {
        List<Picture> pictures = new ArrayList<>();
        if (getIntent() != null && getIntent().getSerializableExtra("albumPictures") != null) {
            pictures = (List<Picture>) getIntent().getSerializableExtra("albumPictures");
        }
        return pictures;
    }
}
