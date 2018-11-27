package mytunes.dal;

import java.net.URI;
import java.util.List;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;

/**
 * This class provides an interface to the database operations
 * @author Dominik
 */
public class DALManager {

    private MediaDBManager mediaM = new MediaDBManager();
    private MetaReader metaR = new MetaReader();
    private PlayListDBManager listM = new PlayListDBManager();

    /**
     * Get the songs from the database
     * @return A list of song stored in the database 
     * @throws DAException If an error occurs during database access
     */
    public List<UserMedia> getAllMedia() throws DAException {
        List<UserMedia> mediaList = mediaM.getAll();       
        return mediaList;
    }
    
    /**
     * Get all the play list stored in the database
     * @return A list of play list stored in the database 
     * @throws DAException If an error occurs during database access
     */
    public List<PlayList> getAllPlayList() throws DAException {
        List<PlayList> playlistList = listM.getAll();
        return playlistList;
    }

    /**
     * Save a the data of a media object to the database
     * @param media The object that will be saved to the database
     * @throws DAException If an error occurs during database access
     */
    public void saveMedia(UserMedia media) throws DAException {
        mediaM.save(media);
    }

    /**
     * Save the data of a play list to the database
     * @param playlist The object that will be saved to the database
     * @throws DAException If an error occurs during database access
     */
    public void savePlayList(PlayList playlist) throws DAException {
        listM.save(playlist);
    }

    /**
     * Update an already existing UserMedia object in the database
     * @param media The updated object
     * @throws DAException If an error occurs during database access
     */
    public void editMedia(UserMedia media) throws DAException {
        mediaM.edit(media);
    }

    /**
     * Update an already existing PlayList object in the database
     * @param playlist The updated object
     * @throws DAException If an error occurs during database access
     */
    public void editList(PlayList playlist) throws DAException {
        listM.edit(playlist);
    }

    /**
     * Delete a UserMedia object from the database
     * @param media The object that will be deleted
     * @throws DAException If an error occurs during database access
     */
    public void deleteMedia(UserMedia media) throws DAException {
        mediaM.delete(media);
    }

    /**
     * Delete a PlayList object from the database
     * @param playlist The object that will be deleted
     * @throws DAException If an error occurs during database access
     */
    public void deletePlayList(PlayList playlist) throws DAException {
        listM.delete(playlist);
    }

    /**
     * Add a song to a play list
     * @param playlist The play list that will be updated
     * @param media The song that will be added to the play list
     * @throws DAException If an error occurs during database access
     */
    public void addMediaToList(PlayList playlist, UserMedia media) throws DAException {
        listM.addMediaToList(playlist, media);
    }
    
    /**
     * Delete a song from a play list
     * @param playlist The play list that will be updated
     * @param media The song that will be deleted from the play list
     * @throws DAException If an error occurs during database access
     */
    public void deleteMediaFromList(PlayList playlist, UserMedia media) throws DAException {
        listM.deleteMediaFromList(playlist, media);
    }
    
    /**
     * Get the meta data of the file represented by the URI
     * @param path The path of the file
     * @return An UserMedia object containing meta data from the file
     * @throws DAException If an error occurs during file access
     */
    public UserMedia getMetaData(URI path) throws DAException {
        return metaR.getMetaData(path);
    }
}
