package model;

import static AlbumList.AlbumList.albumArrayList;

import android.widget.Toast;

import com.example.androidphotos87.MainActivity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import AlbumList.AlbumList;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Picture> album = new ArrayList<>();
    private String albumName;
    private int photoCount;

    public Album(String albumName) {
        this.albumName = albumName;
        this.photoCount = 0;
    }

    public int getPhotoCount() {
        return album.size();
    }

    public String getAlbumName() {
        return albumName;
    }

    public void addPicture(Picture picture) {
        album.add(picture);
    }

    public ArrayList<Picture> getPhotos() {
        return album;
    }

    public static Boolean albumExists(String newName) {
        Boolean exists = false;
        for (Album a : albumArrayList) {
            if (a.getAlbumName().equals(newName)) {
                exists = true;
            }
        }
        return exists;
    }

    public Boolean contains(Picture p){
        Boolean check = false;
        for(Picture photo : album){
            if(photo.equals(p)){
                check = true;
            }
        }
        return check;
    }

    public void renameAlbum(String newName) {
        this.albumName = newName;
    }
}
