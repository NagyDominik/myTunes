package mytunes.BLL;

import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;
import mytunes.dal.DAException;
import mytunes.dal.DALManager;

/**
 *
 * @author sebok
 */
public class BLLManager {

    private PlayList selectedPlayList; //The currently selected play list
    private UserMedia selectedMedia; //The currently selected media
    private DALManager dalManger = new DALManager();
    private MediaObjectManager mediaObjectManager = new MediaObjectManager(dalManger); 
    private PlayListManager playListManager = new PlayListManager(dalManger);
    private Player player = new Player();

//******************************************************************************************************************************************************************//
//Load data
    /**
     * Load the information of the stored media from the DB
     * 
     * @return A list of songs, after they have been retrieved from the database
     * @throws BLLException If an error happens in the MediaObjectManager
     */
    public List<UserMedia> loadMedia() throws BLLException {
        return mediaObjectManager.getMedia();
    }

    /**
     * Attempt to retrieve the play list stores in the DB
     * 
     * @return List A list of play lists, after they have been retrieved from the database
     * @throws BLLException If an error happens in the PlayListManager
     */
    public List<PlayList> loadPlayLists() throws BLLException {
        return playListManager.loadPlayLists();
    }

    /**
     * Attempt to get the categories from the DB
     * 
     * @return A list of the categories of the songs stored in the database
     * @throws BLLException If an error happens in the MediaObjectManager
     */
    public List<String> getCategories() throws BLLException {
        return mediaObjectManager.getCategories();
    }

//******************************************************************************************************************************************************************//
//Save data
    /**
     * Save a new song to the DB
     * 
     * @param newMedia This object will be saved to the database 
     * @throws BLLException If an error happens in the MediaObjectManager
     */
    public void addNewMedia(UserMedia newMedia) throws BLLException {
        try {
            mediaObjectManager.addNew(newMedia);
        }
        catch (BLLException ex) {
            Logger.getLogger(BLLManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new BLLException(ex);
        }
    }

    /**
     * Attempt to save a play list to the DB
     * 
     * @param newPlayList This play list will be saved to the database
     * @throws BLLException If an error happens in the PlayListManager
     */
    public void saveNewPlayList(PlayList newPlayList) throws BLLException {
        if (newPlayList == null) {
            throw new BLLException("Play list does not exists!");
        }
        playListManager.saveNewPlayList(newPlayList);
    }

    /**
     * Save the selected media to the selected play list
     * 
     * @param selectedMedia This song will be saved to the play list
     * @param selectedPlayList This play list will now contain the selected song
     * @throws BLLException If an error happens in the PlayListManager
     */
    public void addMediaToPlayList(UserMedia selectedMedia, PlayList selectedPlayList) throws BLLException {
        if (selectedMedia == null) {
            throw new BLLException("No media selected");
        }
        if (selectedPlayList == null) {
            throw new BLLException("No play list selected!");
        }
        playListManager.addMediaToPlayList(selectedMedia, selectedPlayList);
    }
//******************************************************************************************************************************************************************//
//Edit data

    /**
     * Attempt to update a UserMedia object in the DB
     * 
     * @param selectedMedia The data of this object will be altered in the database
     * @throws BLLException If an error happens in the MediaObjectManager
     */
    public void updateMedia(UserMedia selectedMedia) throws BLLException {
        if (selectedMedia == null) {
            throw new BLLException("No media selected!");
        }
        mediaObjectManager.updateMedia(selectedMedia);
    }

    /**
     * Attempt to update a play list in the DB
     * 
     * @param selectedPlayList The data of this play list will be altered in the database
     * @throws BLLException If an error happens in the PlayListManager
     */
    public void updatePlayList(PlayList selectedPlayList) throws BLLException {
        if (selectedPlayList == null) {
            throw new BLLException("No playlist selected");
        }
        playListManager.updatePlayList(selectedPlayList);
    }

//******************************************************************************************************************************************************************//
//Delete data
    
    /**
     * Attempt to delete a UserMedia object
     * 
     * @param selectedMedia This object will be deleted from the database
     * @throws BLLException If an error happens in the MediaObjectManager
     */
    public void deleteMedia(UserMedia selectedMedia) throws BLLException {
        if (selectedMedia == null) {
            throw new BLLException("No song selected song!");
        }
        mediaObjectManager.remove(selectedMedia);
    }

    /**
     * Remove the selected media from the selected play list
     * 
     * @param selectedMedia This object will be removed from the selected play list
     * @param selectedPlayList This play list will no longer contain the selected song
     * @throws BLLException If an error happens in the PlayListManager
     */
    public void removeMediaFromPlayList(UserMedia selectedMedia, PlayList selectedPlayList) throws BLLException {
        if (selectedPlayList == null) {
            throw new BLLException("No play list selected!");
        }
        playListManager.removeSongFromPlayList(selectedMedia, selectedPlayList);
    }

    /**
     * Attempt to remove a play list. If the play list contains media objects, it
     * removes them first
     *
     * @param selected This play list will be removed from the database
     * @throws BLLException If an error happens in the PlayListManager
     */
    public void deletePlayList(PlayList selected) throws BLLException {
        if (selected == null) {
            throw new BLLException("No playlist selected!");
        }
        if (!selected.isEmpty()) {  
            for (UserMedia media : selected.getMediaList()) {   //Remove the song found in the play list firs
                removeMediaFromPlayList(media, selected);
            }
            selected.clearMediaList();
        }
        playListManager.removePlayList(selected);
    }

//******************************************************************************************************************************************************************//
//Getters and setters
    
    /**
     * Set the selected play list to the new selection (for example, when the
     * selection inside the plasListTableView changes)
     *
     * @param selected The currently selected play list
     * @throws BLLException If the parameter is null
     */
    public void setSelectedPLayList(PlayList selected) throws BLLException {
        if (selected == null) {
            throw new BLLException("No playlist selected!");
        }
        this.selectedPlayList = selected;
    }

    /**
     * Set the selected media to the new selection (for example, when the
     * selection inside the songsTableView changes)
     *
     * @param selected The currently selected song
     * @throws BLLException If the parameter is null
     */
    public void setSelectedPMedia(UserMedia selected) throws BLLException {
        if (selected == null) {
            throw new BLLException("No song  selected!");
        }
        this.selectedMedia = selected;
    }

    /**
     * Return the selected play list
     *
     * @return The selected play list
     * @throws BLLException If the selectedPlayList has not been set
     */
    public PlayList getSelectedPlayList() throws BLLException {
        if (selectedPlayList == null) {
            throw new BLLException("No playlist selected!");
        }
        return selectedPlayList;
    }

    /**
     * Return the selected media
     *
     * @return The selected song
     * @throws BLLException If the selectedMedia has not been set
     */
    public UserMedia getSelectedMedia() throws BLLException {
        if (selectedMedia == null) {
            throw new BLLException("No media selected!");
        }
        return this.selectedMedia;
    }

    /**
     * Indicates whether or not the player is currently playing a song
     * @return A BooleanProperty indicating if the Player is currently playing a song
     */
    public BooleanProperty isPlaying()
    {
        return player.isPlayingProperty();
    }
    
    /**
     * Get the currentlyPlaying string property
     * @return A StringProperty containing information about the currently playing song
     */
    public StringProperty getPlayingString()
    {
        return player.currentlyPlayingStringProperty();
    }
//******************************************************************************************************************************************************************//
//MediaPlayer control methods
    
    /**
     * Start playing the media
     * @throws BLLException If an error occurs during playback
     */
    public void playMedia() throws BLLException {
         player.play();
    }
    /**
     * Sets the song in the Player to the parameter
     * @param media This song will be set inside the Player 
     * @throws BLLException If no media has been selected
     */
    public void setMedia(UserMedia media) throws BLLException {
        if (media == null)
        {
            throw new BLLException("No media selected");
        }
        
        player.setMedia(media);
    }

    /**
     * Set the Player using a play list
     * @param selectedPlayList The player will be set using this play list
     * @throws BLLException If no play list has been selected 
     */
    public void setMedia(PlayList selectedPlayList) throws BLLException
    {
        if (selectedPlayList == null)
        {
            throw new BLLException("No playlist selected!");
        }
        
        player.setMedia(selectedPlayList);
    }
    
    /**
     * Sets the volume of the player
     * @param vol The volume will be set using this number
     */
    public void setVolume(double vol) {
        player.setVolume(vol);
    }

    /**
     * Pauses the play back
     */
    public void pauseMedia() {
        player.pause();
    }

//******************************************************************************************************************************************************************//
//Other methods
    
    /**
     * Attempts to retrieve the meta data of the file associated with the URI
     *
     * @param path The path of the file
     * @return UserMedia A UserMedia object containing information of the file
     * @throws BLLException If an error occurs in the DAL.
     */
    public UserMedia getMetaData(URI path) throws BLLException {
        try {
            return dalManger.getMetaData(path);
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Switch to the next song in the play list
     * @throws BLLException If an error occurs in the Player
     */
    public void nextMedia() throws BLLException 
    {
        if (player.isPlayingProperty().get())
        {
             player.playNextSong(); //Play the next song in the list
        }
        else
        {
            player.setNextSong(); //Switch to the next song, but don't play it
        }
    }

    /**
     * Switch to the previous song in the play list
     * @throws BLLException If an error occurs in the Player
     */
    public void previousMedia() throws BLLException {
        if (player.isPlayingProperty().get())
        {
             player.playPreviousSong(); //Play the previous song
        }
        else
        {
            player.setPreviousSong(); //Switch to the previous song, but don't play it
        }
    }
}
