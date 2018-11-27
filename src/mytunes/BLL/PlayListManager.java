package mytunes.BLL;

import java.util.List;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;
import mytunes.dal.DAException;
import mytunes.dal.DALManager;

/**
 * Handles operations such as saving and retrieving PlayList objects from the database
 *
 * @author sebok
 */
public class PlayListManager {

    private DALManager dalManager;

    /**
     * Creates a new PlayListManagee instance and sets the internal DALManager object using the parameter
     * @param dm The DALManagar that will be used to access the database
     */
    public PlayListManager(DALManager dm) {
        this.dalManager = dm;
    }

    /**
     * Attempt to load the play list from the DB
     * 
     * @return List The list of the play list stored in the database
     * @throws BLLException If an error occurs during loading
     */
    List<PlayList> loadPlayLists() throws BLLException {
        try {
            return dalManager.getAllPlayList();
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Attempt to save a new play list into the DB 
     * 
     * @param newPlayList The play list that will be saved to the database 
     * @throws BLLException If an error occurs during edit
     */
    void saveNewPlayList(PlayList newPlayList) throws BLLException {
        try {
            dalManager.savePlayList(newPlayList);
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }
    
    /**
     * Attempt to update existing play list in the database
     * 
     * @param selectedPlayList The play list that will be updated in the database
     * @throws BLLException If an error occurs during update
     */
    void updatePlayList(PlayList selectedPlayList) throws BLLException {
        try {
            dalManager.editList(selectedPlayList);
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Remove the selected play list from the DB
     * 
     * @param selected The play list that will be deleted from the database
     * @throws BLLException If an error occurs during deletion
     */
    void removePlayList(PlayList selected) throws BLLException {
        try {
            dalManager.deletePlayList(selected);
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Add the selected song to the selected play list
     * 
     * @param selectedMedia The song that will be added from the selected play list
     * @param selectedPlayList The play list to which the selected song will be added
     * @throws BLLException If an error occurs during addition
     */
    void addMediaToPlayList(UserMedia selectedMedia, PlayList selectedPlayList) throws BLLException {
        try {
            dalManager.addMediaToList(selectedPlayList, selectedMedia);
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Remove the selected song from the selected play list
     * 
     * @param selectedMedia The song that will be removed from the selected play list
     * @param selectedPlayList The play list from which the selected song will be removed
     * @throws BLLException If an error occurs during deletion
     */
    void removeSongFromPlayList(UserMedia selectedMedia, PlayList selectedPlayList) throws BLLException {
        try {
            dalManager.deleteMediaFromList(selectedPlayList, selectedMedia);
        }
        catch (DAException ex) {
            throw new BLLException(ex);
        }
    }
}
