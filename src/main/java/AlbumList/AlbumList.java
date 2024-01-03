package AlbumList;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.Album;

public class AlbumList implements Serializable {
    public static List<Album> getAlbumArrayList() {
        return albumArrayList;
    }
    public static List<Album> albumArrayList = new ArrayList<>();
    public static void saveAlbumList(Context context){

        System.out.println("Saving albumList");
        try {
            FileOutputStream fos = context.openFileOutput("albumData.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(albumArrayList);
            oos.close();
        } catch (IOException e) {

        }
    }

    public static void loadAlbumList(Context context) {

        try {
            FileInputStream fis = context.openFileInput("albumData.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            albumArrayList = (List<Album>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException | ClassNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
