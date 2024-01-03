package model;



import java.io.*;
import java.util.ArrayList;

public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;
    private String path;
    private String name;
    private ArrayList<String> people;
    private String location;
    public Picture(String uri) {
        this.path = uri;
        this.name = path.substring(path.lastIndexOf('/') + 1);
        this.people = new ArrayList<>();
    }
    public String getPath() {
            return path;
        }
    public String getName(){
            return name;
        }
    public ArrayList<String> getPeople() {
            return people;
        }
    public void addPerson(String person) {
            if (people == null) {
                people = new ArrayList<>();
            }
            people.add(person);
        }
    public String getLocation() {
            return location;
        }
    public void setLocation(String newLocation) {
            location = newLocation;
        }
}




