package mytunes.be;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Represent a collection of songs
 * @author Mape
 */
public class PlayList {

    private ObservableList<UserMedia> mediaList = FXCollections.observableArrayList();  //The collection of songs
    
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final IntegerProperty count = new SimpleIntegerProperty();
    
    private double totalTimeInSeconds;
    private final StringProperty timeFormattedAsString = new SimpleStringProperty();
    private int currentlyPlayingIndex;
    
    /**
     * Construct a new PlayList object and set the variables to their default values, and add a change listener to the list of songs, so that the count IntegerProperty can be updated automatically
     */
    public PlayList()
    {
        totalTimeInSeconds = 0;
        currentlyPlayingIndex = 0;
        
        mediaList.addListener(new ListChangeListener<UserMedia>()
        {
            @Override
            public void onChanged(ListChangeListener.Change<? extends UserMedia> c)
            {
                count.set(mediaList.size());
            }
        });
    }
    
     /**
     * Construct a new play list with the specified id and title
     * @param id The ID of the play list
     * @param title the title of the play list
     */
    public PlayList(int id, String title) {
        this.id.set(id);
        this.title.set(title);
    }

    /**
     * Return a StringProperty containing the total time of the songs in the play list, formatted as a string
     * @return A StringProperty containing the total time of the songs in the play list, formatted as a string
     */
    public StringProperty timeFormattedAsStringProperty()
    {
        return timeFormattedAsString;
    }
    
    /**
     * Update the timeFormattedAsString StringProperty to contain the total length of the songs in the play list
     */
    private void updateStringTime()
    {
        long timeInLong = new Double(totalTimeInSeconds).longValue();
        
        int day = (int)TimeUnit.SECONDS.toDays(timeInLong);        
        long hours = TimeUnit.SECONDS.toHours(timeInLong) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(timeInLong) - (TimeUnit.SECONDS.toHours(timeInLong)* 60);
        long second = TimeUnit.SECONDS.toSeconds(timeInLong) - (TimeUnit.SECONDS.toMinutes(timeInLong) *60);

        timeFormattedAsString.setValue(String.format("%02d:%02d:%02d", hours, minute, second));
    }
    
    /**
     * Returns the number of songs in the play list
     * @return The number of songs in the play list
     */
    public int getCount()
    {
        return count.get();
    }

    /**
     * Return the IntegerProperty containing the number of songs in the play list
     * @return The IntegerProperty containing the number of songs in the play list
     */
    public IntegerProperty countProperty()
    {
        return count;
    }

    /**
     *Return the title of the play list
     * @return The title of the play list
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Set the title of the play list
     * @param value The title of the play list
     */
    public void setTitle(String value) {
        title.set(value);
    }

    /**
     * Get the id of the play list
     * @return The id of the play list
     */
    public int getId() {
        return id.get();
    }

    /**
     * Set the id of the play list
     * @param value The id that will be set
     */
    public void setId(int value) {
        id.set(value);
    }

    /**
     * Get the list of songs that are in this play list
     * @return The list of songs that are found in this play list
     */
    public ObservableList<UserMedia> getMediaList() {
        return mediaList;
    }


    /**
     * Add a song to the play list
     * @param selectedMedia the song that will be added to the play list
     */
    public void addMedia(UserMedia selectedMedia) 
    {
        mediaList.add(selectedMedia);
        totalTimeInSeconds += selectedMedia.getTime();
        updateStringTime();
    }

    /**
     * Remove a song from the play list
     * @param mediaToDelete The song that will be removed
     */
    public void removeMedia(UserMedia mediaToDelete) {
        Iterator<UserMedia> i = mediaList.iterator();
        
        while(i.hasNext())  //Iterate through the media list
        {
            if (i.next().getId() == mediaToDelete.getId()) //If the media is in the list remove it
            {
                i.remove();
                break;
            }
        }
        
        totalTimeInSeconds -= mediaToDelete.getTime();
        updateStringTime();
    }

    /**
     * Check if a song is already in the play list
     * @param media The song that will be checked
     * @return True id the play list contains the song, false otherwise
     */
    public boolean containsMedia(UserMedia media) {
        for (UserMedia userMedia : mediaList)
        {
            if (userMedia.getId() == media.getId())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the play list contains at least one song
     * @return True if the play list does not contain any songs, false otherwise
     */
    public boolean isEmpty() {
        return mediaList.isEmpty();
    }

    /**
     * Deletes all songs from this play list
     */
    public void clearMediaList() {
        mediaList.clear();
    }

    /**
     * Get the index of a song in the list 
     * @param selected The song which we are checking
     * @return If the song is in the list, returns the index, otherwise returns -1
     */
    public int getIndexOfMedia(UserMedia selected) {
        int i = -1;
        for (UserMedia media : mediaList) {
            i++;
            if (selected.equals(media)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Move the song with the specified index up in the list
     * @param index The index of the song which will be moved up in the list
     */
    public void moveSongUp(int index) 
    {
        UserMedia switchSong = mediaList.get(index - 1);
        mediaList.set(index - 1, mediaList.get(index));
        mediaList.set(index, switchSong);
    }

    /**
     * Move the song with the specified index down in the list
     * @param index The index of the song which will be moved down in the list
     */
    public void moveSongDown(int index) 
    {
        UserMedia switchSong = mediaList.get(index + 1);
        mediaList.set(index + 1, mediaList.get(index));
        mediaList.set(index, switchSong);
    }

    /**
     * Return the media that is currently being played
     * @return The media that is currently being played
     */
    public UserMedia getCurrentlyPlaying()
    {
        return mediaList.get(currentlyPlayingIndex);
    }

    /**
     * Set the index to the next UserMedia object in the list
     * If the end of the list is reached, loop around to the first one
     */
    public void setNextIndex()
    {
        if (currentlyPlayingIndex < mediaList.size() - 1)
        {
            this.currentlyPlayingIndex++;
        }
        else
        {
            currentlyPlayingIndex = 0;
        }
    }

    /**
     * Set the index to the previous UserMedia object in the list
     * If the beginning of the list is reached, loop around to the last one
     */
    public void setPreviousIndex()
    {
        if (currentlyPlayingIndex > 0)
        {
            this.currentlyPlayingIndex--;
        }
        else
        {
            currentlyPlayingIndex = mediaList.size() - 1;
        }
    }
    
    /**
     * Return the string representation of this object
     * @return The string representation of this object
     */
    @Override
    public String toString() 
    {
        return "PlayList{ id=" + id + ", title=" + title + '}';
    }
}
