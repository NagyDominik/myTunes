package mytunes.gui.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mytunes.be.PlayList;
import mytunes.be.UserMedia;
import mytunes.gui.Model.MediaPlayerModel;
import mytunes.gui.Model.ModelException;

/**
 * The controller that controls the MainWindow, and handles the events
 *
 * @author Mape
 */
public class MainController implements Initializable {

    @FXML
    private ImageView next;
    @FXML
    private ImageView previous;
    @FXML
    private ImageView play;
    @FXML
    private ImageView search;
    @FXML
    private ImageView addArrow;
    @FXML
    private ImageView upArrow;
    @FXML
    private ImageView downArrow;
    @FXML
    private Slider volumeController;
    @FXML
    private Label songName;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<PlayList> playlistTableView;
    @FXML
    private ListView<UserMedia> playlistSongsListView;
    @FXML
    private TableView<UserMedia> songsTableView;
    @FXML
    private TableColumn<PlayList, String> playListColumnName;
    @FXML
    private TableColumn<PlayList, String> playListColumnSongsCount;
    @FXML
    private TableColumn<PlayList, String> playListColumnTotalTime;
    @FXML
    private TableColumn<UserMedia, String> songsColumnTitle;
    @FXML
    private TableColumn<UserMedia, String> songsColumArtist;
    @FXML
    private TableColumn<UserMedia, String> songsColumnTime;
    @FXML
    private TableColumn<UserMedia, String> songsColumnCategory;

    private final Image img_next = new Image("file:images/next.png");
    private final Image img_previous = new Image("file:images/previous.png");
    private final Image img_play = new Image("file:images/play.png");
    private final Image img_pause = new Image("file:images/pause.png");
    private final Image img_search = new Image("file:images/search.png");
    private final Image img_down = new Image("file:images/down.png");
    private final Image img_up = new Image("file:images/up.png");
    private final Image img_addArrow = new Image("file:images/previous.png");
    private final Image img_clearSeach = new Image("file:images/clear_search.png");
    
    private MediaPlayerModel model;
    private UserMedia currentMedia;

    private boolean isFilterActive;
    
    public void initialize(URL url, ResourceBundle rb)
    {
        next.setImage(img_next);
        previous.setImage(img_previous);
        play.setImage(img_play);
        search.setImage(img_search);
        upArrow.setImage(img_up);
        downArrow.setImage(img_down);
        addArrow.setImage(img_addArrow);
        model = MediaPlayerModel.getInstance();
        setUpPlayListCellFactories();
        setUpSongsCellFactories();
        loadMedia();
        playlistTableView.setItems(model.getPlayLists());
        songsTableView.setItems(model.getMedia());
        model.setVolume(volumeController.getValue());
        setListenersAndEventHandlers();
        isFilterActive = false;

    }

    /**
     * Sets up the list cell factories to correctly display the data of a play
     * list
     */
    private void setUpPlayListCellFactories() {
        //Set up cell factories
        playListColumnName.setCellValueFactory(new PropertyValueFactory("title"));
        playListColumnSongsCount.setCellValueFactory(new PropertyValueFactory("count"));
        playListColumnTotalTime.setCellValueFactory(new PropertyValueFactory("timeFormattedAsString"));

        //Set the width of the columns
        playlistTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        playListColumnName.prefWidthProperty().bind(playlistTableView.widthProperty().divide(2));
        playListColumnSongsCount.prefWidthProperty().bind(playlistTableView.widthProperty().divide(4));
        playListColumnTotalTime.prefWidthProperty().bind(playlistTableView.widthProperty().divide(4));
    }

    /**
     * Sets up the list cell factories to correctly display the data of a
     * UserMedia object
     */
    private void setUpSongsCellFactories() {
        //Set up cell factories
        songsColumnTitle.setCellValueFactory(new PropertyValueFactory("title"));
        songsColumArtist.setCellValueFactory(new PropertyValueFactory("artist"));
        songsColumnCategory.setCellValueFactory(new PropertyValueFactory("category"));
        songsColumnTime.setCellValueFactory(new PropertyValueFactory("timeString"));

        //Set the width of the columns
        songsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        songsColumnTitle.prefWidthProperty().bind(songsTableView.widthProperty().divide(4));
        songsColumArtist.prefWidthProperty().bind(songsTableView.widthProperty().divide(4));
        songsColumnCategory.prefWidthProperty().bind(songsTableView.widthProperty().divide(4));
        songsColumnTime.prefWidthProperty().bind(songsTableView.widthProperty().divide(4));
    }

    /**
     * Updates the list view that contains the songs found in play list whenever
     * a new play list is selected
     */
    private void updateListView() {
        PlayList selectedPlayList = playlistTableView.getSelectionModel().getSelectedItem();
        if (selectedPlayList == null) {
            return;
        }
        playlistSongsListView.setItems(selectedPlayList.getMediaList());
        try {
            model.setSelectedPlayList(selectedPlayList);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Load data from the database
     */
    private void loadMedia() {
        try {
            model.loadDataFromDB();
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Set up listeners and event handles for different events
     */
    private void setListenersAndEventHandlers() {
        //Update the list view containing the songs in the selected play list, whenever a new play list is selected
        playlistTableView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                updateListView();
            }
        }
        );

        //Add a new event handler, so that search can be performed by pressing enter
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    String searchString = searchField.getText();
                    searchForString(searchString);
                }
            }
        });

        //Change the image of the play button based on wheter or not a media is playing
        model.isPlaying().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!model.isPlaying().get()) {
                    play.setImage(img_play);
                } else {
                    play.setImage(img_pause);
                }
            }
        });

        //Bind the currentlyPlayingStringProperty to the songName label, to alwys display the currently playing media
        model.getCurrentlyPlayingString().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                songName.setText(model.getCurrentlyPlayingString().get());
            }
        });
    }

//******************************************************************************************************************************************************************//
//GUI controls events
    /**
     * Delete the selected song
     */
    @FXML
    private void deleteSongClicked(ActionEvent event) {
        if (showConfirmationDialog("Are you sure you want to delete this song?")) {
            return;
        }
        UserMedia selected = songsTableView.getSelectionModel().getSelectedItem();
        try {
            model.removeMedia(selected);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Jump to the next song in the play list
     */
    @FXML
    private void nextArrowClicked(MouseEvent event) {
        PlayList selectedPlayList = playlistTableView.getSelectionModel().getSelectedItem();
        try {
            model.setNextMedia();
            currentMedia = selectedPlayList.getCurrentlyPlaying();
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Jump to the previous song in the play list
     */
    @FXML
    private void previousArrowClicked(MouseEvent event) {
        PlayList selectedPlayList = playlistTableView.getSelectionModel().getSelectedItem();
        try {
            model.setPreviousMedia();
            currentMedia = selectedPlayList.getCurrentlyPlaying();
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Attempt to play either a single song or a play list, or if the playback
     * was paused continue playing.
     */
    @FXML
    private void playArrowClicked(MouseEvent event) {
        UserMedia selectedMedia = songsTableView.getSelectionModel().getSelectedItem();
        PlayList selectedPlayList = playlistTableView.getSelectionModel().getSelectedItem();
        try {
            if (selectedPlayList == null) {
                if (!model.isPlaying().get()) {
                    if (selectedMedia != currentMedia) //If we did not select a new media, continue playing the old one
                    {
                        model.setMedia(selectedMedia);
                        model.playMedia();
                        currentMedia = selectedMedia;
                    } else {
                        model.playMedia();
                    }
                } else {
                    model.pauseMedia();
                    play.setImage(img_play);
                }
            } else //We have a selected play list, play all the songs
            {
                if (!model.isPlaying().get()) {
                    if (selectedPlayList.getCurrentlyPlaying() != currentMedia) {
                        model.setMedia(selectedPlayList);
                        model.playMedia();
                        currentMedia = selectedPlayList.getCurrentlyPlaying();
                    } else {
                        model.playMedia();
                    }
                } else {
                    model.pauseMedia();
                }
            }
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }

    }

    /**
     * Set a new volume
     */
    @FXML
    private void volumeClicked(MouseEvent event) {
        model.setVolume(volumeController.getValue());
    }

    /**
     * Filter the list of songs using the string entered into the searchField
     */
    @FXML
    private void searchClicked(MouseEvent event) {
        if (isFilterActive)
        {
            searchField.setText("");
        }
        
        String searchString = searchField.getText();
        searchForString(searchString);
    }

    /**
     * Add the selected song to the selected play list
     */
    @FXML
    private void addArrowClicked(MouseEvent event) {
        PlayList selectedPlayList = playlistTableView.getSelectionModel().getSelectedItem();
        UserMedia selectedMedia = songsTableView.getSelectionModel().getSelectedItem();
        try {
            model.addMediaToPlayList(selectedMedia, selectedPlayList);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Delete the selected play list
     */
    @FXML
    private void deletePlaylistClicked(ActionEvent event) {
        PlayList selected = playlistTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmationDialog("Are you sure you want to delete this play list?")) {
                return;
            }
        }
        try {
            model.removePlayList(selected);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Delete the selected song from the selected play list
     */
    @FXML
    private void deleteSongFromPlaylistClicked(ActionEvent event) {
        if (showConfirmationDialog("Are you sure you want to delete this song from the play list?")) {
            return;
        }
        PlayList selectedPlayList = playlistTableView.getSelectionModel().getSelectedItem();
        UserMedia selectedMedia = playlistSongsListView.getSelectionModel().getSelectedItem();
        try {
            model.removeMediaFromPlayList(selectedMedia, selectedPlayList);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Move the selected media up in the selected list (UI only)
     */
    @FXML
    private void upArrowClicked(MouseEvent event) {
        UserMedia selected = playlistSongsListView.getSelectionModel().getSelectedItem();
        PlayList list = playlistTableView.getSelectionModel().getSelectedItem();
        try {
            model.moveSongUp(selected, list);
            playlistSongsListView.getSelectionModel().select(selected);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Move the selected media down in the selected list (UI only)
     */
    @FXML
    private void downArrowClicked(MouseEvent event) {
        UserMedia selected = playlistSongsListView.getSelectionModel().getSelectedItem();
        PlayList list = playlistTableView.getSelectionModel().getSelectedItem();

        try {
            model.moveSongDown(selected, list);
            playlistSongsListView.getSelectionModel().select(selected);
        }
        catch (ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

//******************************************************************************************************************************************************************//
//New windows
    /**
     * Open a new window where we can add a new song
     */
    @FXML
    private void addNewSongClicked(ActionEvent event) {
        try {
            model.setMediaMode(Mode.NEW);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mytunes/gui/View/NewSong.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Add/Edit song");
            stage.show();
        }
        catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Open an new window where we can edit an existing song
     */
    @FXML
    private void editSongClicked(ActionEvent event) {
        try {
            model.setMediaMode(Mode.EDIT);
            model.setSelectedMedia(songsTableView.getSelectionModel().getSelectedItem());
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mytunes/gui/View/NewSong.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Add/Edit media");
            stage.setScene(new Scene(root1));
            stage.showAndWait();
            
            songsTableView.refresh();   //Udate the songs table view
            playlistTableView.refresh();    //Update the play list (in case some play lists contain the edited song)
        }
        catch (IOException | ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Open a new window where we can add a new play list
     */
    @FXML
    private void addNewPlaylistClicked(ActionEvent event) {
        try {
            model.setPlayListMode(Mode.NEW);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mytunes/gui/View/NewPlayList.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Add/Edit playlist");
            stage.show();
        }
        catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Open a new window where we can edit an existing play list
     */
    @FXML
    private void editPlaylistClicked(ActionEvent event) {
        try {
            model.setPlayListMode(Mode.EDIT);
            model.setSelectedPlayList(playlistTableView.getSelectionModel().getSelectedItem());

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/mytunes/gui/View/NewPlayList.fxml"));

            Parent root1 = (Parent) fxmlLoader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.showAndWait();
            
            playlistTableView.refresh();
        }
        catch (IOException | ModelException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

    /**
     * Open a new window where we can play a vide from an URL
     */
    @FXML
    private void clickPlayURL(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mytunes/gui/View/WebPlayer.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("WebPlayer");
            stage.show();
        }
        catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }

//******************************************************************************************************************************************************************//
//Helper methods
    /**
     * Set the image on the search button, and search the list in the model for the give string
     * @param search The string which will be used as a filter
     */
    private void searchForString(String searchString)
    {
        if (!searchString.isEmpty())
        {
            search.setImage(img_clearSeach);
            model.searchString(searchString);
            isFilterActive = true;
        }
        else
        {
            search.setImage(img_search);
            model.searchString(searchString);
            isFilterActive = false;
        }
    }

    /**
     * Show a new alert window, with the text of the error
     *
     * @param ex The exception which will be used to display the message
     */
    private void showAlert(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, "An error occured: " + ex.getMessage(), ButtonType.OK);
        a.show();
    }

    /**
     * Close the window
     */
    @FXML
    private void closeAppClicked(ActionEvent event) {
        Stage stage = (Stage) play.getScene().getWindow();
        stage.close();
    }

    /**
     * Show a window with the specified prompt text Returns with the value the
     * user selected (continue or not)
     *
     * @param prompt The text that will be showed on the window
     * @return The values selected by the user
     */
    private boolean showConfirmationDialog(String prompt) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, prompt, ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait();
        return confirmation.getResult() == ButtonType.NO;
    }
}
