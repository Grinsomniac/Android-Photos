package com.example.androidphotos87;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import AlbumList.AlbumList;
import model.Album;
import model.Picture;

public class SearchActivity extends AppCompatActivity {

    Button searchButton;
    Spinner topSpinner;
    Spinner bottomSpinner;
    AutoCompleteTextView topTextField;
    AutoCompleteTextView bottomTextField;
    Spinner logicSpinner;
    Album searchResults;
    private GridView gridView;
    private PictureGridAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchButton = findViewById(R.id.searchButton);
        topSpinner = findViewById(R.id.spinner1);
         bottomSpinner = findViewById(R.id.spinner2);
         topTextField = findViewById(R.id.autoComplete1);
         bottomTextField = findViewById(R.id.autoComplete2);
         logicSpinner = findViewById(R.id.logicSpinner);
         searchResults = new Album("Search Results");

        bottomTextField.setAdapter(updateAutoCompleteText(bottomSpinner.getSelectedItem().toString()));
        topTextField.setAdapter(updateAutoCompleteText(topSpinner.getSelectedItem().toString()));

        gridView = findViewById(R.id.picture_grid);
        adapter = new PictureGridAdapter(this, searchResults.getPhotos(), searchResults.getAlbumName());
        gridView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Search Photos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click here
                handleSearch();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                topTextField.clearFocus();
                bottomTextField.clearFocus();

            }
        });

        bottomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update AutoCompleteTextView data based on the selected item in the Spinner
                bottomTextField.setAdapter(updateAutoCompleteText(bottomSpinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        topSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update AutoCompleteTextView data based on the selected item in the Spinner
                topTextField.setAdapter(updateAutoCompleteText(topSpinner.getSelectedItem().toString()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

    private ArrayAdapter updateAutoCompleteText(String tag) { // for dynamically updating the autocompletion fields
        ArrayAdapter<String> autoCompleteAdapter;
        ArrayList<String> locationList = new ArrayList<>();
        ArrayList<String> personList = new ArrayList<>();

        for (Album a : AlbumList.albumArrayList) {
            for (Picture p : a.getPhotos()) {
                if ((!locationList.contains(p.getLocation()) && p.getLocation() != null)) {
                    locationList.add(p.getLocation());
                }
                for (String s : p.getPeople()) {
                    if (!personList.contains(s)) {
                        personList.add(s);
                    }
                }
            }
        }

        if (tag.equals("Person")) { // Person selected
            autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, personList);
        } else {// Location selected
            autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locationList);
        }

            return autoCompleteAdapter;

        }


    //tool bar handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.search) {
            // Handle the options button click

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    //end tool bar handling

    public void handleSearch(){

        searchResults.getPhotos().clear();
        String topText = topTextField.getText().toString();
        String bottomText = bottomTextField.getText().toString();
        String topTag = topSpinner.getSelectedItem().toString();
        String bottomTag = bottomSpinner.getSelectedItem().toString();

        if((bottomText.isEmpty() && !topText.isEmpty())) { // if only searching with top text field
            if (topTag.equals("Location")) {
                allLocationSearch(topText);
            } else {
                allPersonSearch(topText);
            }
        }

        if((!bottomText.isEmpty() && topText.isEmpty())){ // if only searching with bottom text field
            if(bottomTag.equals("Location")){
                allLocationSearch(bottomText);
            }else{
                allPersonSearch(bottomText);
            }
        }

        if((!bottomText.isEmpty() && !topText.isEmpty())){ // if searching with both text fields
            if(!logicSpinner.getSelectedItem().toString().isEmpty()) {
                twoTagSearch(topText, bottomText, topTag, bottomTag);
            }else{

                Toast.makeText(this, "You must select AND/OR for multi-tag search", Toast.LENGTH_SHORT).show();
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void allLocationSearch(String value){

        for(Album a : AlbumList.albumArrayList){
            for(Picture p : a.getPhotos()){
                    if(p.getLocation() != null && p.getLocation().equalsIgnoreCase(value)){
                        searchResults.addPicture(p);
                }
            }
        }
    }

    public void allPersonSearch(String person){
        for(Album a : AlbumList.albumArrayList){
            for(Picture p : a.getPhotos()){
                for(String s : p.getPeople()){
                    if(s.equalsIgnoreCase(person) && !searchResults.contains(p)){
                        searchResults.addPicture(p);
                    }
                }
            }
        }
    }

    public void twoTagSearch(String topText, String bottomText, String topTag, String bottomTag) {

        String logicOperator = logicSpinner.getSelectedItem().toString();


        for (Album a : AlbumList.albumArrayList) {
            for (Picture p : a.getPhotos()) {
                Boolean match1 = false;
                Boolean match2 = false;
                if (topTag.equals("Location")) {
                    if (p.getLocation() != null && p.getLocation().equalsIgnoreCase(topText)) {
                        match1 = true;
                    }
                }else {
                        for (String s : p.getPeople()) {
                            if (s.equalsIgnoreCase(topText)) {
                                match1 = true;
                            }
                        }
                    }

                if (bottomTag.equals("Location")) {
                    if (p.getLocation() != null && p.getLocation().equalsIgnoreCase(bottomText)) {
                        match2 = true;
                    }
                }else {
                        for (String s : p.getPeople()) {
                            if (s.equalsIgnoreCase(bottomText)) {
                                match2 = true;
                            }
                        }
                    }

                // add photo to album
                if (logicOperator.equals("AND") && match1 && match2) {
                    if (!searchResults.contains(p)) {
                        searchResults.addPicture(p);
                    }
                } else if (logicOperator.equals("OR") && (match1 || match2)) {
                    if (!searchResults.contains(p)) {
                        searchResults.addPicture(p);
                    }


                }
            }

        }


    }
}