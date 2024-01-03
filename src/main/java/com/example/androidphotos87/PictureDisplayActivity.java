package com.example.androidphotos87;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.Album;
import AlbumList.AlbumList;
import model.Picture;
public class PictureDisplayActivity extends AppCompatActivity {
    private String albumName;
    private String photoPath;
    private String sourceActivity;
    private int photoIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        albumName = getIntent().getStringExtra("albumName");
        photoPath = getIntent().getStringExtra("selectedPhotoPath");
        photoIndex = getIntent().getIntExtra("photoIndex", -1);
        sourceActivity = getIntent().getStringExtra("sourceActivity");
        ImageView imageView = findViewById(R.id.imageView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(photoPath.substring(photoPath.lastIndexOf('/') + 1));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load the photo from photoPath into imageView
        if (photoPath != null && !photoPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            imageView.setImageBitmap(bitmap);
        } else {
            // Handle the case where photoPath is null or empty
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
        //Set up for tag overlay and toggle button
        Button toggleTagsButton = findViewById(R.id.toggle_tags_button);
        ScrollView tagScrollView = findViewById(R.id.tag_scroll_view);
        toggleTagsButton.setVisibility(View.INVISIBLE);

        // Initially hide the tag overlay
        tagScrollView.setVisibility(View.GONE);
        if(sourceActivity.equals("PictureGridActivity")){
            toggleTagsButton.setText("Show Tags");
            toggleTagsButton.setVisibility(View.VISIBLE);
        }


        //Toggle Button click listener
        toggleTagsButton.setOnClickListener(v -> {
            if (tagScrollView.getVisibility() == View.GONE) {
                tagScrollView.setVisibility(View.VISIBLE);
                toggleTagsButton.setText("Hide Tags");
                updateTagOverlay(getCurrentPicture()); // Ensure tags are up-to-date
            } else {
                tagScrollView.setVisibility(View.GONE);
                toggleTagsButton.setText("Show Tags");
            }
        });

        // Load and show tags if available
        updateTagOverlay(getCurrentPicture());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(sourceActivity.equals("PictureGridActivity")){
            getMenuInflater().inflate(R.menu.menu_photo_display, menu);

        }else{
            if(sourceActivity.equals("SearchActivity")){
                getMenuInflater().inflate(R.menu.menu_search_photo_display, menu);

            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.delete) {
            deletePhoto();
            return true;
        } else if (id == R.id.slideshow) {
            // Launch SlideshowActivity
            launchSlideshow();
            return true;
        } else if (id == R.id.tag) {
            // Show tag options
            showTagMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void launchSlideshow() {
        // Find the current album from AlbumList
        Album currentAlbum = null;
        for (Album album : AlbumList.getAlbumArrayList()) {
            if (album.getAlbumName().equals(albumName)) {
                currentAlbum = album;
                break;
            }
        }

        if (currentAlbum != null && !currentAlbum.getPhotos().isEmpty()) {
            Intent intent = new Intent(this, SlideshowActivity.class);
            intent.putExtra("albumPictures", new ArrayList<>(currentAlbum.getPhotos()));
            intent.putExtra("currentPhotoIndex", photoIndex); // Pass the current photo index
            startActivity(intent);
        } else {
            Toast.makeText(this, "No photos available for slideshow.", Toast.LENGTH_SHORT).show();
        }
    }
    private void deletePhoto() {
        List<Album> albums = AlbumList.getAlbumArrayList();
        Album currentAlbum = null;

        for (Album album : albums) {
            if (album.getAlbumName().equals(albumName)) {
                currentAlbum = album;
                break;
            }
        }
        if (currentAlbum != null && photoIndex != -1) {
            currentAlbum.getPhotos().remove(photoIndex);
            AlbumList.saveAlbumList(this);
            setResult(RESULT_OK); // Set result for the calling activity
            finish();
        } else {
            Toast.makeText(this, "Error deleting photo", Toast.LENGTH_SHORT).show();
        }
    }
    private void showTagMenu() {
        if(sourceActivity.equals("PictureGridActivity")) {
            PopupMenu popup = new PopupMenu(this, findViewById(R.id.tag));
            popup.getMenu().add(Menu.NONE, 1, Menu.NONE, "Add Person Tag");
            popup.getMenu().add(Menu.NONE, 2, Menu.NONE, "Add Location Tag");

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        // Code to add Person Tag
                        addPersonTag();
                        return true;
                    case 2:
                        // Code to add Location Tag
                        addLocationTag();
                        return true;
                    default:
                        return false;
                }
            });

            popup.show();
        }
    }
    private void addPersonTag() {
        // Show an AlertDialog to input a person's name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Person Tag");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String tag = input.getText().toString();
            addTagToPicture(tag, true); // true for person tag
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void addLocationTag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Location Tag");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String locationTag = input.getText().toString();
            if (!locationTag.isEmpty()) {
                setPictureLocation(locationTag);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void setPictureLocation(String locationTag) {
        Picture currentPicture = getCurrentPicture();
        if (currentPicture != null) {
            currentPicture.setLocation(locationTag);
            updateTagOverlay(currentPicture);
        }
    }
    private void addTagToPicture(String tag, boolean isPerson) {
        Picture currentPicture = getCurrentPicture();
        if (currentPicture != null) {
            if (isPerson) {
                currentPicture.addPerson(tag);
            } else {
                currentPicture.setLocation(tag);
            }
            updateTagOverlay(currentPicture);
            saveAlbumData();
        }
    }
    private void deleteTag(String tagText, boolean isPersonTag) {
        Picture currentPicture = getCurrentPicture();
        if(sourceActivity.equals("PictureGridActivity")) {
            if (currentPicture != null) {
                if (isPersonTag) {
                    // For person tags, remove the exact tag
                    currentPicture.getPeople().removeIf(personTag -> ("Person: " + personTag).equals(tagText));
                } else {
                    // For location tag, set location to null if it matches
                    if (("Location: " + currentPicture.getLocation()).equals(tagText)) {
                        currentPicture.setLocation(null);
                    }
                }
                updateTagOverlay(currentPicture);
                saveAlbumData(); // Save the updated data
            }
        }
    }
    private Picture getCurrentPicture() {
        Album currentAlbum = null;
        for (Album album : AlbumList.getAlbumArrayList()) {
            if (album.getAlbumName().equals(albumName)) {
                currentAlbum = album;
                break;
            }
        }

        if (currentAlbum != null && photoIndex >= 0 && photoIndex < currentAlbum.getPhotos().size()) {
            return currentAlbum.getPhotos().get(photoIndex);
        } else {
            return null;
        }
    }
    private void updateTagOverlay(Picture picture) {
        LinearLayout tagOverlay = findViewById(R.id.tagOverlay);
        tagOverlay.removeAllViews(); // Clear existing views

        if (sourceActivity.equals("PictureGridActivity")) {
            for (String person : picture.getPeople()) {
                addTagView(tagOverlay, "Person: " + person, true);
            }


            if (picture.getLocation() != null && !picture.getLocation().isEmpty()) {
                addTagView(tagOverlay, "Location: " + picture.getLocation(), false);
            }

            tagOverlay.setVisibility(picture.getPeople().isEmpty() && picture.getLocation() == null ? View.GONE : View.VISIBLE);
        }
    }
    private void addTagView(LinearLayout parent, String tagText, boolean isPersonTag) {
        View tagView = LayoutInflater.from(this).inflate(R.layout.tag_item, parent, false);

        TextView textView = tagView.findViewById(R.id.tag_text);
        textView.setText(tagText);

        Button deleteButton = tagView.findViewById(R.id.delete_tag_button);
        deleteButton.setOnClickListener(v -> deleteTag(tagText, isPersonTag));

        parent.addView(tagView);
    }
    private String getFormattedTags(Picture picture) {
        StringBuilder tags = new StringBuilder();
        for (String person : picture.getPeople()) {
            tags.append("Person: ").append(person).append("\n");
        }
        if (picture.getLocation() != null && !picture.getLocation().isEmpty()) {
            tags.append("Location: ").append(picture.getLocation());
        }
        return tags.toString().trim();
    }
    private void saveAlbumData(){
        AlbumList.saveAlbumList(this);
    }
    @Override
    protected void onPause() {
        AlbumList.saveAlbumList(this);  // Save data when the app is paused
        super.onPause();
    }
}