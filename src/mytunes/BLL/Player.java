package mytunes.BLL;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;

/**
 * A class responsible for playing either a single song or all songs of a play list
 * @author sebok
 */
class Player {
    private MediaPlayer player;
    private PlayList list;
    private final BooleanProperty isPlaying = new SimpleBooleanProperty();
    private final StringProperty currentlyPlayingString = new SimpleStringProperty();
    private UserMedia currentMedia;
    
    /**
     * Create a new Player instance using a Media object
     * @param media An instance of MediaPlayer will be created using this object
     */
    public Player(Media media) {
        player = new MediaPlayer(media);
    }
    
    /**
     * Empty constructor
     */
    public Player() {
    }
    
    
    /**
     * Returns a StringProperty containing information about the currently playing song
     * @return A StringProperty containing information about the currently playing song
     */
    public StringProperty currentlyPlayingStringProperty()
    {
        return currentlyPlayingString;
    }
    
    /**
     * Returns the BooleanProperty indicating if a song is being played
     * @return The BooleanProperty indicating if a song is being played
     */
    public BooleanProperty isPlayingProperty()
    {
        return isPlaying;
    }

    /**
     * Sets the volume of the player
     * @param value 
     */
    public void setVolume(double value) {
        if (player != null) {
            player.setVolume(value);
        }
    }

    /**
     * Attempts to play the selected song.
     * @throws BLLException If the song cannot be played, an exception is thrown
     */
    public void play() throws BLLException {
        try {
            player.play();
            isPlaying.set(true);
            setPlayingString(currentMedia);
        }
        catch (Exception ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Pauses the currently playing song, and updates the properties
     */
    public void pause() {
        player.pause();
        currentlyPlayingString.set("PAUSED");
        isPlaying.set(false);
    }

    /**
     * Set the player using a single song, that will be played once
     * @param media This song will be played once
     * @throws BLLException If the parameter is null, an exception is thrown
     */
    public void setMedia(UserMedia media) throws BLLException {
        try {
            player = new MediaPlayer(media.getMedia());
            currentMedia = media;
        }
        catch (NullPointerException ex) {
            throw new BLLException("You are trying to play a not existing media! Maybe the path of this song is not located on this computer?");
        }
        
        player.setOnEndOfMedia(() ->    //Update the properties, once the song has finished playing
        {
            isPlaying.set(false);
            currentlyPlayingString.set("");
            player.stop();
        });
    }

    /**
     * Set the player using a play list
     * @param selectedPlayList The player will play all songs in this play list
     * @throws BLLException If one song cannot be played, an error is thrown
     */
    public void setMedia(PlayList selectedPlayList) throws BLLException
    {
        list = selectedPlayList;
        player = new MediaPlayer(list.getCurrentlyPlaying().getMedia());
        currentMedia = list.getCurrentlyPlaying();
        
        player.setOnEndOfMedia(() ->        //After one song has ended, play the next one
        {
            try
            {
                this.playNextSong();
            } catch (BLLException ex)
            {
                System.out.println(ex.getMessage());
            }
        });
    }

    /**
     * Play the next song on the play list
     * @throws BLLException If the play list is null (not selected), an exception is thrown
     */
    public void playNextSong() throws BLLException
    {
        if (list == null)
        {
            throw new BLLException("No play list");
        }
        
        player.stop();
        list.setNextIndex();
        setMedia(list.getCurrentlyPlaying());
        setPlayingString(list.getCurrentlyPlaying());
        player.play();
        
    }

    /**
     * Play the previous song on the play list
     * @throws BLLException If the play list is null (not selected), an exception is thrown
     */
    public void playPreviousSong() throws BLLException
    {
        if (list == null)
        {
            throw new BLLException("No play list");
        }
        
        player.stop();
        list.setPreviousIndex();
        setMedia(list.getCurrentlyPlaying());
        setPlayingString(list.getCurrentlyPlaying());
        player.play();
    }
    
    /**
     * Updates the currentlyPlaying StringProperty, to contain information of the currently playing song
     * @param media The StringProperty will be set using the values of this object
     */
    private void setPlayingString(UserMedia media)
    {
        currentlyPlayingString.setValue("Currently playing: " +  media.getArtist() + ": " + media.getTitle() );
    } 

    /**
     * Sets the next song in the play list
     * @throws BLLException If the play list has not been set, an exception is thrown
     */
    public void setNextSong() throws BLLException
    {
        if (list == null)
        {
            throw new BLLException("No play list");
        }
        
        list.setNextIndex();;
        setMedia(list.getCurrentlyPlaying());
    }
    
    /**
     * Sets the previous song in the play list
     * @throws BLLException If the play list has not been set, an exception is thrown
     */
    public void setPreviousSong() throws BLLException
    {
        if (list == null)
        {
            throw new BLLException("No play list");
        }
        
        list.setPreviousIndex();
        setMedia(list.getCurrentlyPlaying());
    }
}
