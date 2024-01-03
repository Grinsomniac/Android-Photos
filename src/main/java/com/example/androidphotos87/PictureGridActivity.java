package com.example.androidphotos87;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import AlbumList.AlbumList;
import model.Album;
import model.Picture;
import com.example.androidphotos87.MainActivity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
public class PictureGridActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_READ_MEDIA_IMAGES = 1;
    private Album selectedAlbum;
    private GridView gridView;
    private PictureGridAdapter adapter;
    private ActivityResultLauncher<Intent> galleryLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picturegrid_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Album intentAlbum = (Album) bundle.getSerializable("selectedAlbum");
            if (intentAlbum != null) {
                for (Album a : AlbumList.getAlbumArrayList()) {
                    if (a.getAlbumName().equals(intentAlbum.getAlbumName())) {
                        selectedAlbum = a;
                        break; // Break once the matching album is found
                    }
                }
            }
        }

        if (selectedAlbum == null) {
            // Handle the case where no matching album is found
            Toast.makeText(this, "Album not found", Toast.LENGTH_LONG).show();
            finish(); // Close the activity or handle appropriately
            return;
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(selectedAlbum.getAlbumName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = findViewById(R.id.picture_grid);
        adapter = new PictureGridAdapter(this, selectedAlbum.getPhotos(), selectedAlbum.getAlbumName());
        gridView.setAdapter(adapter);

        registerForContextMenu(gridView);

        FloatingActionButton addImageButton = findViewById(R.id.addPhotoButton);
        addImageButton.setOnClickListener(view -> {
            if (checkReadMediaImagesPermission()) {
            openGallery();
        } else {
            requestReadMediaImagesPermission();
        }});

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleGalleryResult(result.getData());
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PictureGridAdapter.REQUEST_CODE && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture_grid, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.slideshow) {
            if (selectedAlbum.getPhotos().isEmpty()) {
                // Show a message if there are no photos
                Toast.makeText(this, "No photos in the album to display in slideshow.", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to launch SlideshowActivity
                Intent intent = new Intent(this, SlideshowActivity.class);
                intent.putExtra("albumPictures", selectedAlbum.getPhotos());
                startActivity(intent);
            }
            return true;
        } else if (itemId == R.id.help) {
            // Display an AlertDialog with formatted message
            new AlertDialog.Builder(this)
                    .setTitle("Help")
                    .setMessage("Use the 'Plus' icon to add more photos to your album\n\n" +
                            "Click the Slideshow Button to launch a Slide show. Swipe left and right to view all photos in the album.\n\n" +
                            "Tap a photo to view it and add/edit tags.\n\n" +
                            "Press and hold a photo to delete it or move it to another Album")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Select Action");
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
        menu.add(Menu.NONE, 2, Menu.NONE, "Move to another album");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position; // Position in the adapter

        switch (item.getItemId()) {
            case 1: // Delete option
                deletePicture(position);
                return true;
            case 2: // Other options like "Move to another album"
                movePictureToAlbum(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void requestReadMediaImagesPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                REQUEST_PERMISSION_READ_MEDIA_IMAGES
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                // Permission denied, show a message or handle accordingly
                // For example, you can display a Toast message
                Toast.makeText(this, "Permission denied. Cannot access the gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean checkReadMediaImagesPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }
    private void handleGalleryResult(Intent data) {
        if (data.getClipData() != null) {
            // When multiple images are selected
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                String filePath = getFilePathFromUri(imageUri);
                try {
                    selectedAlbum.getPhotos().add(new Picture(filePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (data.getData() != null) {
            // When a single image is selected
            Uri imageUri = data.getData();
            String filePath = getFilePathFromUri(imageUri);
            try {
                selectedAlbum.getPhotos().add(new Picture(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AlbumList.saveAlbumList(this);
        adapter.notifyDataSetChanged(); // Refresh the grid view
    }
    private String getFilePathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        } else {
            return uri.getPath(); // Fallback to using the Uri's path if cursor is null
        }
    }
    private void deletePicture(int position) {
        // Remove the picture at the specified position
        if (selectedAlbum != null && position >= 0 && position < selectedAlbum.getPhotos().size()) {
            selectedAlbum.getPhotos().remove(position);
            AlbumList.saveAlbumList(this); // Save changes to the album list
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the grid view
        } else {
            Toast.makeText(this, "Error deleting photo", Toast.LENGTH_SHORT).show();
        }
    }
    //TODO - Check for Duplicate in new Album?
    //TODO -
    private void movePictureToAlbum(int position) {
        Picture pictureToMove = selectedAlbum.getPhotos().get(position);

        // Fetch the list of album names excluding the current album
        List<String> albumNames = AlbumList.getAlbumArrayList().stream()
                .map(Album::getAlbumName)
                .filter(name -> !name.equals(selectedAlbum.getAlbumName()))
                .collect(Collectors.toList());

        // Convert to CharSequence array for the dialog
        CharSequence[] albumsArray = albumNames.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move to album");
        builder.setItems(albumsArray, (dialog, which) -> {
            // Move the picture to the selected album
            String targetAlbumName = albumNames.get(which);
            movePicture(pictureToMove, targetAlbumName, position);
        });
        builder.show();
    }

    //TODO - alertdialog to user if no album exists.
    //TODO - Copy TAGS - Not working
    private void movePicture(Picture picture, String targetAlbumName, int position) {
        for (Album album : AlbumList.getAlbumArrayList()) {
            if (album.getAlbumName().equals(targetAlbumName)) {
                album.getPhotos().add(picture);
                break;
            }
        }
        selectedAlbum.getPhotos().remove(position);
        AlbumList.saveAlbumList(this);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        AlbumList.saveAlbumList(this);  // Save data when the app is paused
        super.onPause();
    }
}
