package mytunes.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;

/**
 * Handles operations such as saving, updating and deleting PlayList objects from the database
 * @author Dominik
 */
public class PlayListDBManager {

    private ConnectionManager cm = new ConnectionManager();

    /**
     * Returns a list of PlayList from the database and fills the list in each PlayList
     * with the connected media.
     * 
     * @return A list of PlayList stored in the database
     * @throws DAException If an error occurs during database access
     */
    public List<PlayList> getAll() throws DAException {
        List<PlayList> playListList = new ArrayList();
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("SELECT * FROM Playlist");
            ResultSet result = pstatement.executeQuery();
            while (result.next()) {
                PlayList tempList = new PlayList();
                tempList.setId(result.getInt("id"));
                tempList.setTitle(result.getString("title"));
                playListList.add(tempList);
            }

            PreparedStatement pstaStatement2 = con.prepareStatement(
                    "SELECT Music.* , MusicInList.listID "
                    + "FROM Music, MusicInList "
                    + "WHERE MusicInList.musicID = Music.id");
            ResultSet result2 = pstaStatement2.executeQuery();
            while (result2.next()) {
                for (PlayList playList : playListList) {
                    if (playList.getId() == result2.getInt("listID")) {
                        UserMedia tempMedia = new UserMedia();
                        tempMedia.setId(result2.getInt("id"));
                        tempMedia.setTitle(result2.getString("title"));
                        tempMedia.setArtist(result2.getString("artist"));
                        tempMedia.setCategory(result2.getString("category"));
                        tempMedia.setTime(result2.getInt("time"));
                        tempMedia.setPath(result2.getString("path"));
                        tempMedia.createMediaFromPath();
                        playList.addMedia(tempMedia);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DAException(e.getMessage());
        }
        return playListList;
    }

    /**
     * Saves the data of a given PlayList to the database.
     * 
     * @param playlist The PlayList object that will be saved to the database
     * @throws DAException If an error occurs during database access
     */
    public void save(PlayList playlist) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement(
                    "INSERT INTO Playlist(title)"
                    + "VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            pstatement.setString(1, playlist.getTitle());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Playlist could not be saved!");
            }
            ResultSet rs = pstatement.getGeneratedKeys();
            if (rs.next()) {
                playlist.setId(rs.getInt(1));
            }
        }
        catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }

    /**
     * Updates and already existing database entry with the data from a given PlayList.
     * 
     * @param playlist The PlayList object with the updated data, that will be saved to the database
     * @throws DAException If an error occurs during database access
     */
    public void edit(PlayList playlist) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("UPDATE Playlist SET title=? WHERE id=?");
            pstatement.setString(1, playlist.getTitle());
            pstatement.setInt(2, playlist.getId());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Playlist could not be edited!");
            }
        }
        catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }

    /**
     * Removes the database entry of the given PlayList from the database.
     * 
     * @param playlist The PlayList that will be removed from the database
     * @throws DAException If an error occurs during database access
     */
    public void delete(PlayList playlist) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement("DELETE FROM Playlist WHERE id=?");
            pstatement.setInt(1, playlist.getId());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Playlist could not be deleted!");
            }
        }
        catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }

    /**
     * Adds a database entry that creates a connection between the given PlayList 
     * and the given UserMedia objects.
     * 
     * @param playlist The selected play list
     * @param media The selected song
     * @throws DAException If an error occurs during database access
     */
    public void addMediaToList(PlayList playlist, UserMedia media) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement(
                    "INSERT INTO MusicInList(listID, musicID)"
                    + "VALUES(?, ?)");
            pstatement.setInt(1, playlist.getId());
            pstatement.setInt(2, media.getId());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Media cannot be added to the playlist!");
            }
        }
        catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }

    /**
     * Deletes the database entry that creates a connection between the given PlayList
     * and the given UserMedia objects.
     * 
     * @param playlist The selected play list
     * @param media The selected song
     * @throws DAException If an error occurs during database access
     */
    public void deleteMediaFromList(PlayList playlist, UserMedia media) throws DAException {
        try (Connection con = cm.getConnection()) {
            PreparedStatement pstatement = con.prepareStatement(
                    "DELETE FROM MusicInList WHERE musicID=? AND listID=?");
            pstatement.setInt(1, media.getId());
            pstatement.setInt(2, playlist.getId());
            int affected = pstatement.executeUpdate();
            if (affected < 1) {
                throw new DAException("Media cannot be deleted from the list!");
            }
        }
        catch (Exception e) {
            throw new DAException(e.getMessage());
        }
    }
}
