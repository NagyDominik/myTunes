package mytunes.BLL;

import java.util.ArrayList;
import java.util.List;
import mytunes.be.UserMedia;
import mytunes.dal.DAException;
import mytunes.dal.DALManager;

/**
 * Handles operations such as saving and retrieving UserMedia objects from the database
 * @author sebok
 */
public class MediaObjectManager {

    private List<String> categories;
    private DALManager dalManager;

    /**
     * Creates a new MediaObjectManager instance and sets the internal DALManager object using the parameter
     * @param dm The DALManagar that will be used to access the database
     */
    public MediaObjectManager(DALManager dm) {
        this.dalManager = dm;
    }

    /**
     * Load the data found in the database
     *
     * @return List The list of songs saved in the database
     * @throws mytunes.BLL.BLLException If an error occurs during loading
     */
    List<UserMedia> getMedia() throws BLLException {
        try {
            List<UserMedia> uMediaList = dalManager.getAllMedia();
            categories = new ArrayList<>();
            for (UserMedia userMedia : uMediaList) //Filter out the categories
            {
                if (!categories.contains(userMedia.getCategory())) {
                    categories.add(userMedia.getCategory());
                }
            }
            return uMediaList;
        } catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Get the categories
     *
     * @return List A list of categories, collected during the retrieval of songs
     * @throws mytunes.BLL.BLLException If the categories list has not been filled yet
     *
     */
    public List<String> getCategories() throws BLLException {
        if (categories == null) {
            throw new BLLException("No data has been red in!");
        }
        return this.categories;
    }

    /**
     * Save the data of the selected media to the DB
     *
     * @param selectedSong The song which will be saved to the database
     * @throws mytunes.BLL.BLLException If an error occurs during saving
     */
    void addNew(UserMedia selectedSong) throws BLLException {
        try {
            dalManager.saveMedia(selectedSong);
        } catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Attempt to remove the selected object from the DB
     *
     * @param selected The song that will be deleted from the database
     * @throws mytunes.BLL.BLLException If an error occurs during deletion
     */
    void remove(UserMedia selected) throws BLLException {
        try {
            dalManager.deleteMedia(selected);
        } catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

    /**
     * Update an existing media object
     *
     * @param selectedMedia The song which will be updated in the database
     * @throws mytunes.BLL.BLLException If an error occurs during update
     */
    void updateMedia(UserMedia selectedMedia) throws BLLException {
        try {
            dalManager.editMedia(selectedMedia);
        } catch (DAException ex) {
            throw new BLLException(ex);
        }
    }

}
