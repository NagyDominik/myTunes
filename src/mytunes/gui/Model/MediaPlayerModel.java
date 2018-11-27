package mytunes.gui.Model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import mytunes.BLL.BLLException;
import mytunes.BLL.BLLManager;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;
import mytunes.gui.Controller.Mode;

/**
 * Model class, responsible for separating the data from the display
 * @author sebok
 */
public class MediaPlayerModel {

    private ObservableList<UserMedia> allMedia = FXCollections.observableArrayList();   //Contains all the songs
    private ObservableList<UserMedia> filteredList = FXCollections.observableArrayList();   //Contains the songs that match the current filter (if there is one)
    private ObservableList<PlayList> playlists = FXCollections.observableArrayList();   //Contains the play lists
    private ObservableList<String> categories = FXCollections.observableArrayList();    //Contains the categories

    private final BLLManager bllManager = new BLLManager();
    private static MediaPlayerModel instance;

    private Mode mediaMode;
    private Mode playListMode;

    /**
     * Set up a change listener so that the filtered list can be updated automatically whenever the main list is changed
     */
    public MediaPlayerModel()
    {
        allMedia.addListener(new ListChangeListener<UserMedia>()
        {
            @Override
            public void onChanged(ListChangeListener.Change<? extends UserMedia> c)
            {
                filteredList.clear();
                filteredList.addAll(allMedia);
            }
        });
    }
    
    /**
     * If the model already have an instance return it.
     * Otherwise create a new instance, and return that
     * @return The instance of the MediaPlayerModel
     */
    public static MediaPlayerModel getInstance() {
        if (instance == null) {
            instance = new MediaPlayerModel();
        }

        return instance;
    }

//******************************************************************************************************************************************************************//
//Load data
    /**
     * Attempt to load the information from the DB
     * @throws ModelException If an error occurs during loading
     */
    public void loadDataFromDB() throws ModelException {
        try {
            allMedia.addAll(bllManager.loadMedia());    //Load the songs
            playlists.addAll(bllManager.loadPlayLists());   //Load the play lists
            categories.addAll(bllManager.getCategories());  //Load the categories
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

//******************************************************************************************************************************************************************//
//Save data
    /**
     * Add a new category to the list
     *
     * @param category The category that will be added to the list
     * @throws ModelException If the parameter is an empty string
     */
    public void addNewCategory(String category) throws ModelException {
        category = category.trim(); //Remove tailing and leading whitespaces

        if (category.isEmpty()) //Do not accept an empty string
        {
            throw new ModelException("Nothing to add");
        }

        if (categories.contains(category)) //Do not allow duplicate entries
        {
            throw new ModelException("Category is already in the list!");
        }

        categories.add(category);
    }

    /**
     * Create a new play list with the supplied title, and save it to the list and the DB
     *
     * @param title The title of the new play list
     * @throws ModelException If the title is an empty string, or if an error occurs during saving
     */
    public void createNewPlayList(String title) throws ModelException {
        if (title.isEmpty()) //Do not create a playlist with an empty titly
        {
            throw new ModelException("Empty title!");
        }

        PlayList newPlayList = new PlayList();
        newPlayList.setTitle(title);

        try {
            bllManager.saveNewPlayList(newPlayList);    //Attempt to save the playlist to the DB
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }

        playlists.add(newPlayList);
    }

    /**
     * Attempt saving the media into the DB and the memory
     *
     * @param selectedSong The song that will be saved to the database
     * @throws ModelException If an error occurs during saving
     */
    public void addNewMedia(UserMedia selectedSong) throws ModelException {
        try {
            bllManager.addNewMedia(selectedSong);
            allMedia.add(selectedSong);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Attempt to add the selected media to the selected play list
     *
     * @param selectedMedia The song to be saved to the selected play list
     * @param selectedPlayList The play list which will contain the selected song
     * @throws ModelException If the play list already contains the song, or if an error occurs during saving
     */
    public void addMediaToPlayList(UserMedia selectedMedia, PlayList selectedPlayList) throws ModelException {
        if (selectedMedia == null || selectedPlayList == null) {
            throw new ModelException("Please select a song or a play list!");
        }
        
        for (UserMedia userMedia : selectedPlayList.getMediaList()) {
            if (userMedia.getArtist().equalsIgnoreCase(selectedMedia.getArtist()) && userMedia.getTitle().equalsIgnoreCase(selectedMedia.getTitle())) {
                throw new ModelException("Media is already in the list!");
            }
        }

        try {
            bllManager.addMediaToPlayList(selectedMedia, selectedPlayList);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }

        selectedPlayList.addMedia(selectedMedia);
    }

//******************************************************************************************************************************************************************//
//Edit data
    /**
     * Try to update an already existing play list
     *
     * @param selectedPlayList The play list that will be updated in the database
     * @throws ModelException If an error occurs during update
     */
    public void updatePlayList(PlayList selectedPlayList) throws ModelException {
        try {
            bllManager.updatePlayList(selectedPlayList); //Attempt to update the title of the selected play list in the DB
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Edit an already existing media object
     *
     * @param editMedia The song that will be updated in the database
     * @throws ModelException If an error occurs during update
     */
    public void updateMedia(UserMedia editMedia) throws ModelException {
        try {
            bllManager.updateMedia(editMedia);  //Try to update the database
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
                    
        for (PlayList list : playlists)
        {
            if (list.containsMedia(editMedia))  //Manually update the the list in the model
            {
                list.removeMedia(editMedia);
                list.addMedia(editMedia);
            }
        }
    }

//******************************************************************************************************************************************************************//
//Delete data
    
    /**
     * Attempt to remove the media instance from the list and the DB
     *
     * @param selected The song that will be deleted from the database
     * @throws ModelException If an error occurs during delete
     */
    public void removeMedia(UserMedia selected) throws ModelException {
        for (PlayList list : playlists) {
            if (list.containsMedia(selected)) {
                removeMediaFromPlayList(selected, list);
            }
        }

        try {
            bllManager.deleteMedia(selected);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }

        allMedia.remove(selected);
    }

    /**
     * Attempt to delete the song from the play list
     *
     * @param selectedMedia The song to be deleted from the selected play list
     * @param selectedPlayList The play list from which the selected song will be removed
     * @throws ModelException If an error occurs during delete, or if the selected play list does not contain the selected song
     */
    public void removeMediaFromPlayList(UserMedia selectedMedia, PlayList selectedPlayList) throws ModelException {
        if (!selectedPlayList.containsMedia(selectedMedia)) //This should never occur
        {
            throw new ModelException("This playlist does not contain the selected media!");
        }

        try {
            bllManager.removeMediaFromPlayList(selectedMedia, selectedPlayList);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }

        selectedPlayList.removeMedia(selectedMedia);
    }

    /**
     * Attempt to remove the play list
     *
     * @param selected The play list that will be removed from the database
     * @throws ModelException If an error occurs during delete
     */
    public void removePlayList(PlayList selected) throws ModelException {
        if (selected != null) {
            List<UserMedia> inList = new ArrayList<>();

            inList.addAll(selected.getMediaList());

            for (UserMedia userMedia : inList) //Remove the UserMedia objects found in the play list before deleting it
            {
                removeMediaFromPlayList(userMedia, selected);
            }
        }

        try {
            bllManager.deletePlayList(selected);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }

        playlists.remove(selected);
    }

//******************************************************************************************************************************************************************//
//Getters and setters
    
    /**
     * Returns the ObservableList containing the filtered songs
     *
     * @return An ObservableList containing the songs that match the filter
     */
    public ObservableList<UserMedia> getMedia() {
        return this.filteredList;
    }

    /**
     * Returns the ObservableList containing the play lists
     *
     * @return An ObservableList containing the play lists
     */
    public ObservableList<PlayList> getPlayLists() {
        return this.playlists;
    }

    /**
     * Returns all of the possible categories
     *
     * @return An ObservableList containing the categories
     */
    public ObservableList<String> getCategories() {
        return this.categories;
    }

    /**
     * Get the mode in which an editor window was opened (EDIT or NEW). Used when opening a NewSong window, to decide if we need create a new UserMEdia object, or edit an existing one
     * @return The mode in which the editor window was opened
     */
    public Mode getMediaMode() {
        return mediaMode;
    }

    /**
     * Get the mode in which an editor window was opened (EDIT or NEW). Used when opening a NewPlayList window, to decide if we need create a new PlayList object, or edit an existing one
     * @return The mode in which the editor window was opened
     */
    public Mode getPlayListMode() {
        return playListMode;
    }

    /**
     * Set the mode in which a NewSong editor window will be opened (NEW or EDIT).
     * @param mediaMode The mode in which the editor window will open
     */
    public void setMediaMode(Mode mediaMode) {
        this.mediaMode = mediaMode;
    }

    /**
     * Set the mode in which a NewPlayList editor window will be opened (NEW or EDIT).
     * @param playListMode The mode in which the editor window will open
     */
    public void setPlayListMode(Mode playListMode) {
        this.playListMode = playListMode;
    }

    /**
     * Update the information inside the BLL to contain the latest selection
     *
     * @param selected The selected play list
     * @throws ModelException If an error occurs in the BLL (for example: the selection is null)
     */
    public void setSelectedPlayList(PlayList selected) throws ModelException {
        try {
            bllManager.setSelectedPLayList(selected);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Update the information inside the BLL to contain the latest selection
     *
     * @param selected The selected song
     * @throws ModelException If an error occurs in the BLL (for example: the selection is null)
     */
    public void setSelectedMedia(UserMedia selected) throws ModelException {
        try {
            bllManager.setSelectedPMedia(selected);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Returns the currently selected play list, which is stored in the BLL
     *
     * @return The selected play list, as stored in the BLL
     * @throws ModelException If an error occurs in the BLL
     */
    public PlayList getSelectedPlayList() throws ModelException {
        try {
            return bllManager.getSelectedPlayList();
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Return the currently selected media
     *
     * @return The selected song, as stored in the BLL
     * @throws ModelException If an error occurs in the BLL ( most likely the media was not set before)
     */
    public UserMedia getSelectedMedia() throws ModelException {
        try {
            return bllManager.getSelectedMedia();
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }
    
     /**
     * Indicates whether or not the player is currently playing a song
     * @return A BooleanProperty that indicates if the Player is currently playing a song
     */
    public BooleanProperty isPlaying()
    {
        return bllManager.isPlaying();
    }
    
     /**
     * Get the currentlyPlaying string property
     * @return A StringProperty that contains information about the currently playing song in the Player
     */
    public StringProperty getCurrentlyPlayingString()
    {
        return bllManager.getPlayingString();
    }
//******************************************************************************************************************************************************************//
//Other methods
 
    /**
     * Filter the songs based on the supplied string
     *
     * @param search The string that will be used as a filter
     */
    public void searchString(String search) {
        filteredList.clear();

        if (search.isEmpty()) //If the string is empty, return all media
        {
            filteredList.addAll(allMedia);
            return;
        }

        search = search.toLowerCase();

        for (UserMedia userMedia : allMedia) {
            if (userMedia.getArtist().toLowerCase().contains(search) || userMedia.getTitle().toLowerCase().contains(search)) //If the artis's name or the title of the song contains the string, treat it as a match
            {
                filteredList.add(userMedia);
            }
        }
    }

    /**
     * Attempts to retrieve the meta data of the file associated with the URI
     *
     * @param path The path of the file
     * @return A UserMedia object containing information of the file
     * @throws ModelException If an error occurs in the BLL
     */
    public UserMedia getMetaData(URI path) throws ModelException {
        try {
            return bllManager.getMetaData(path);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Move a song up in a play list
     * @param selected The selected song
     * @param list The play list which contains the selected song
     * @throws ModelException If the play list or song is null, or if the song is already at the top of the list
     */
    public void moveSongUp(UserMedia selected, PlayList list) throws ModelException {
        if (selected == null) {
            throw new ModelException("No song selected!");
        }

        if (list == null) {
            throw new ModelException("No play list selected!");
        }

        int index = list.getIndexOfMedia(selected);

        if (index == 0) {
            throw new ModelException("Media is alredy on the top of the list!");
        }

        list.moveSongUp(index);
    }

    /**
     * Move a song down in a play list
     * @param selected The selected song
     * @param list The play list which contains the selected song
     * @throws ModelException If the play list or song is null, or if the song is already at the bottom of the list
     */
    public void moveSongDown(UserMedia selected, PlayList list) throws ModelException {
        if (selected == null) {
            throw new ModelException("No song selected!");
        }

        if (list == null) {
            throw new ModelException("No play list selected!");
        }

        int index = list.getIndexOfMedia(selected);

        if (index == list.getCount() - 1) {
            throw new ModelException("Media is alredy on the bottom of the list!");
        }

        list.moveSongDown(index);
    }

    /**
     * Start the player
     * @throws ModelException If an error occurs during playback
     */
    public void playMedia() throws ModelException {
        try {
             bllManager.playMedia();
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Set the Player using a UserMedia object
     * @param media The UserMedia object that will be used to set the player
     * @throws ModelException If an error occurs in the BLL
     */
    public void setMedia(UserMedia media) throws ModelException {
        try {
            bllManager.setMedia(media);
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Set the Player using a PlayList object
     * @param selectedPlayList The PlayList object that will be used to set the player
     * @throws ModelException If an error occurs in the BLL
     */
    public void setMedia(PlayList selectedPlayList) throws ModelException
    {
        try 
        {
            bllManager.setMedia(selectedPlayList);
        }
        catch (BLLException ex)
        {
            throw new ModelException(ex);
        }
    }
    
    /**
     * Pause playback
     */
    public void pauseMedia() {
        bllManager.pauseMedia();
    }

    /**
     * Set the volume of the playback
     * @param vol The volume that will be set
     */
    public void setVolume(double vol) {
        bllManager.setVolume(vol);
    }

    /**
     * Set the currently playing media to the next one in the list
     * @throws ModelException If an error occurs in the BLL
     */
    public void setNextMedia() throws ModelException {
        try 
        {
            bllManager.nextMedia();
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }

    /**
     * Set the currently playing media to the previous one in the list
     * @throws ModelException If an error occurs in the BLL
     */
    public void setPreviousMedia() throws ModelException {
        try {
            bllManager.previousMedia();
        }
        catch (BLLException ex) {
            throw new ModelException(ex);
        }
    }
}
