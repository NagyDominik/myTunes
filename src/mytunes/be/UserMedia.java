package mytunes.be;

import java.io.File;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;

/**
 * Represents a media object, with its associated properties, such as its title, the artist, its length etc.
 * @author Bence
 */
public class UserMedia {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty artist = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty path = new SimpleStringProperty(); //The user-readable path of the media file
    private Media media;
    private double time;
    private String timeString;

    /** 
     * Creates a new UserMedia object 
     * @param id The ID of this object
     * @param title The title of the media
     * @param artist The artist of the media
     * @param category The category of the media
     * @param path The path of the media on the computer on which it was selected
     * @param media The media object (such as an .mp3 file)
     * @param time The length of the media
     */
    public UserMedia(int id, String title, String artist, String category, String path, Media media, double time) {
        this.id.set(id);
        this.title.set(title);
        this.artist.set(artist);
        this.category.set(category);
        this.path.set(path);
        this.media = media;
        this.time = time;
    }

    /**
     * Create an empty UserMedia object
     */
    public UserMedia() {
    }

    /**
     * Creates a media from the path (for example, when loading a UserMedia object from the database)
     * @throws Exception If the path cannot be found. This probably means that the media was selected on another computer, or the file was moved
     */
    public void createMediaFromPath() throws Exception {
        try {
            File f = new File(path.get());
            this.media = new Media(f.toURI().toString());
        }
        catch (Exception ex) {
            //If the save did not occure on the current machine, an error will occur, and the Media object will no be created
            //The data, hovewer, will not be displayed (but it will appear on the tableView)
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Return the title of the media
     * @return The title of the media
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Set the title of the media
     * @param value The title which will be set
     */
    public void setTitle(String value) {
        title.set(value);
    }

    /**
     * Get the path of the underlying file
     * @return The file path of the Media object
     */
    public String getPath() {
        return path.get();
    }

    /**
     * Set the path of the underlying file
     * @param value The path that will be set
     */
    public void setPath(String value) {
        path.set(value);
    }

    /**
     * Return the category of the media
     * @return The category of the media
     */
    public String getCategory() {
        return category.get();
    }

    /**
     * Set the category of the media
     * @param value The category that will be set.
     */
    public void setCategory(String value) {
        category.set(value);
    }

    /**
     * Get the artist of the media
     * @return The artist of the media
     */
    public String getArtist() {
        return artist.get();
    }

    /**
     * Set the artist of the media
     * @param value The value that will be set
     */
    public void setArtist(String value) {
        artist.set(value);
    }

    /**
     * Get the ID of this object
     * @return The ID of this object
     */
    public int getId() {
        return id.get();
    }

    /**
     * Set the ID of this object
     * @param value The value (ID) that will be set
     */
    public void setId(int value) {
        id.set(value);
    }

    /**
     * Set the time of the media
     * @param time The time that will be set (in seconds)
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Get the time (length) of the media
     * @return The time (length) of the media in seconds
     */
    public double getTime() {
        return time;
    }

    /**
     * Return the Media object stored in this instance
     * @return The Media object stored in this instance
     */
    public Media getMedia() {
        return this.media;
    }

     /**
     * Set the media object of this instance
     * @param media The Media object that will be stored in this instance
     */
    public void setMedia(Media media) {
        this.media = media;
    }
    
    /**
     * Return the time (length) of the media formatted as string
     * @return The time (length) of the media formatted as string
     */
    public String getTimeString() {
        long timeInLong = new Double(time).longValue();
        int day = (int) TimeUnit.SECONDS.toDays(timeInLong);
        long hours = TimeUnit.SECONDS.toHours(timeInLong) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(timeInLong) - (TimeUnit.SECONDS.toHours(timeInLong) * 60);
        long second = TimeUnit.SECONDS.toSeconds(timeInLong) - (TimeUnit.SECONDS.toMinutes(timeInLong) * 60);
        timeString = String.format("%02d:%02d:%02d", hours, minute, second);

        return timeString;
    }

    /**
     * The string representation of this object
     * @return The string representation of this object
     */
    @Override
    public String toString() {
        return "Title: " + getTitle() + " Artist: " + getArtist() + " Category: " + getCategory() + " Time: " + getTime();
    }
}
