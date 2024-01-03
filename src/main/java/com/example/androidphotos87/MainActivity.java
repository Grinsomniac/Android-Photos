package com.example.androidphotos87;

import static com.example.androidphotos87.AlbumAdapter.MENU_DELETE;
import static com.example.androidphotos87.AlbumAdapter.MENU_RENAME;

import static AlbumList.AlbumList.albumArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import AlbumList.AlbumList;
import model.Album;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnItemClickListener {
//TODO - Validate Albumname not empty
    private AlertDialog alertDialog;
    private RecyclerView albumListRecycler;
    private AlbumAdapter albumAdapter;
    private List<Album> userAlbums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        AlbumList.loadAlbumList(this);
        userAlbums = AlbumList.getAlbumArrayList();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumListRecycler = findViewById(R.id.albumList);
        albumAdapter = new AlbumAdapter(userAlbums, this);
        albumListRecycler.setAdapter(albumAdapter);
        albumListRecycler.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(albumListRecycler);
    }
    @Override
    public void onItemClick(Album album) {
        // Handle item click, for example, open a new activity with the selected album
       Bundle bundle = new Bundle();
       bundle.putSerializable("selectedAlbum", album);
       Intent intent = new Intent(this,PictureGridActivity.class);
       intent.putExtras(bundle);
        startActivity(intent);
    }

    //tool bar handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.search) {
            Intent intent = new Intent(this, SearchActivity.class);
            // Start the new activity
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.help) {
            // Display an AlertDialog with formatted message
            new AlertDialog.Builder(this)
                    .setTitle("How-To")
                    .setMessage("Tap the 'Plus' icon to add a new album\n\n" +
                            "Tap an album to view\n\n" +
                            "Press and hold an album to rename or delete\n\n" +
                            "Use the search icon to search all photos across all albums")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //end tool bar handling
    public void handleCreateAlbum(View v){

            // Inflate the popup layout
            LayoutInflater inflater = getLayoutInflater();
            View popupView = inflater.inflate(R.layout.createalbum, null);

            // Create an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(popupView);

            // Access the EditText and Buttons in the popup layout
            EditText editText = popupView.findViewById(R.id.newAlbumName);
            Button submitButton = popupView.findViewById(R.id.submitButton);
            Button cancelButton = popupView.findViewById(R.id.cancelButton);

            // Set a click listener for the submit button
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the text from the EditText
                    String enteredText = editText.getText().toString();

                    if(!Album.albumExists(enteredText)){
                        userAlbums.add(new Album(enteredText));
                        albumAdapter.notifyDataSetChanged();
                        // Do something with the entered text (e.g., display in a Toast)
                        Toast.makeText(MainActivity.this, "Created Album: " + enteredText, Toast.LENGTH_SHORT).show();
                        AlbumList.saveAlbumList(MainActivity.this);
                        // Dismiss the popup
                        alertDialog.dismiss();
                    }else{
                            showAlbumExistsErrorDialog(MainActivity.this);

                        }
                    }
            });

            // Set a click listener for the cancel button
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Dismiss the popup
                    alertDialog.dismiss();
                }
            });

            // Create and show the AlertDialog
            alertDialog = builder.create();
            alertDialog.show();

    }

    // Method to show an error dialog when the album already exists
    private void showAlbumExistsErrorDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error")
                .setMessage("Album already exists. Please choose a different name.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // You can handle the "OK" button click if needed
                    }
                })
                .show();
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = albumAdapter.getCurrentPosition();

        if (position != RecyclerView.NO_POSITION) {
            Album selectedAlbum = albumArrayList.get(position);

            switch (item.getItemId()) {
                case MENU_RENAME:
                    // Handle rename action
                    handleRenameAlbum(position);
                    return true;

                case MENU_DELETE:
                    // Handle delete action
                    userAlbums.remove(position);
                    AlbumList.saveAlbumList(MainActivity.this);
                    albumAdapter.notifyDataSetChanged();
                    return true;

                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }
    private void handleRenameAlbum(int position) {
        // Inflate the popup layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.createalbum, null);

        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);

        // Access the EditText and Buttons in the popup layout
        EditText editText = popupView.findViewById(R.id.newAlbumName);
        Button submitButton = popupView.findViewById(R.id.submitButton);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText
                String enteredText = editText.getText().toString();

                Boolean check = Album.albumExists(enteredText);
                if (!check) {
                    userAlbums.get(position).renameAlbum(enteredText);
                    albumAdapter.notifyDataSetChanged();
                    // Do something with the entered text (e.g., display in a Toast)
                    Toast.makeText(MainActivity.this, "Renamed Album to: " + enteredText, Toast.LENGTH_SHORT).show();
                    AlbumList.saveAlbumList(MainActivity.this);
                    // Dismiss the popup
                    alertDialog.dismiss();
                }else{
                    showAlbumExistsErrorDialog(MainActivity.this);
                }
            }
        });

        // Set a click listener for the cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the popup
                alertDialog.dismiss();
            }
        });
        // Create and show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        AlbumList.saveAlbumList(this);  // Save data when the app is paused
        super.onPause();

    }

    @Override
    protected void onStop() {
        AlbumList.saveAlbumList(this);  // Save data when the app is paused
        super.onStop();

    }
}





