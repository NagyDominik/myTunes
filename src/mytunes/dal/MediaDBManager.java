package mytunes.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.UserMedia;

/**
 * Handles operations such as saving, updating and deleting UserMedia objects from the database
 * @author Bence
 */
public class MediaDBManager {

    private ConnectionManager cm = new ConnectionManager();

    /**
     * Returns a list of UserMedia from the database.
     * 
     * @return A list of UserMedia objects saved in the database.
     * @throws DAException If an error occurs during database access
     */
    public List<UserMedia> getAll() throws DAException {
        List<UserMedia> mediaList = new ArrayList();
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("SELECT * FROM Music");
            ResultSet result = pstatement.executeQuery();
            while (result.next()) {
                UserMedia tempMedia = new UserMedia();
                tempMedia.setId(result.getInt("id"));
                tempMedia.setTitle(result.getString("title"));
                tempMedia.setArtist(result.getString("artist"));
                tempMedia.setCategory(result.getString("category"));
                tempMedia.setTime(result.getInt("time"));
                tempMedia.setPath(result.getString("path"));
                tempMedia.createMediaFromPath();
                mediaList.add(tempMedia);
            }
        } catch (Exception e) {
            throw new DAException(e.getMessage());
        }
        return mediaList;
    }
    
    /**
     * Saves the data of a given UserMedia to the database.
     * 
     * @param media The UserMEdia object that will be saved
     * @throws DAException If an error occurs during database access
     */
    public void save(UserMedia media) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("INSERT INTO Music(title, artist, category, time, path)"
                    + "VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstatement.setString(1, media.getTitle());
            pstatement.setString(2, media.getArtist());
            pstatement.setString(3, media.getCategory());
            pstatement.setDouble(4, media.getTime());
            pstatement.setString(5, media.getPath());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Media could not be added!");
            }
            ResultSet rs = pstatement.getGeneratedKeys();
            if (rs.next()) {
                media.setId(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }
    
    /**
     * Updates and already existing database entry with the data from a given UserMedia.
     * 
     * @param media The UserMedia object that will be updated in the database
     * @throws DAException If an error occurs during database access
     */
    public void edit(UserMedia media) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("UPDATE Music SET title=?, artist=?, category=?, time=?, path=? WHERE id=?");
            pstatement.setString(1, media.getTitle());
            pstatement.setString(2, media.getArtist());
            pstatement.setString(3, media.getCategory());
            pstatement.setDouble(4, media.getTime());
            pstatement.setString(5, media.getPath());
            pstatement.setInt(6, media.getId());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Media could not be edited!");
            }
        } catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }
    
    /**
     * Removes the database entry of the given UserMedia from the database.
     * 
     * @param media The UserMedia object that will be removed from the database
     * @throws DAException If an error occurs during database access
     */
    public void delete(UserMedia media) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("DELETE FROM Music WHERE id=?");
            pstatement.setInt(1, media.getId());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Media could not be deleted!");
            }
        } catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }
}
