package mytunes.gui.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import mytunes.be.PlayList;
import mytunes.gui.Model.MediaPlayerModel;
import mytunes.gui.Model.ModelException;

/**
 * A controller that controls a NewPlayList window
 *
 * @author sebok
 */
public class NewPlayListController implements Initializable
{

    @FXML
    private TextField txtFieldName;

    private PlayList list;
    private MediaPlayerModel model;
    
    private Mode mode; //Depends on whether the "New" or the "Edit" button was clicked
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //Try saving whent the enter key is pressed
        txtFieldName.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if (event.getCode().equals(KeyCode.ENTER))
                {
                    saveData();
                }
            }
        });
        
        model = MediaPlayerModel.getInstance();     //Get the mode in which the window was opened
        
        try
        {
            if (model.getPlayListMode() == Mode.EDIT)
            {
                mode = Mode.EDIT;
                list = model.getSelectedPlayList();
                setText(list);
            }
            else
            {
                mode = Mode.NEW;
                list = new PlayList();
            }
        }
        catch(ModelException ex)
        {
            Logger.getLogger(NewPlayListController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        } 
        catch (Exception ex)
        {
            Logger.getLogger(NewPlayListController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
    }    

    /**
     * Attempt to save the data
     * @param event 
     */
    @FXML
    private void btnSaveClick(ActionEvent event)
    {
        saveData();
    }

    /**
     * Close the window
     * @param event 
     */
    @FXML
    private void btnCancelClick(ActionEvent event)
    {
        closeWindow();
    }
    
    /**
     * Read the string from the txtFieldName text box, and depending on the mode either create a new play list, or alter an existing one
     * Name can be saved by either clicking the "Save" button, or pressing Enter
     */
    private void saveData()
    {
        try
        {
            String playListName = txtFieldName.getText();
            if (mode == Mode.NEW)   //If the selectedPlayList is null, we are creating a new play list
            {
                model.createNewPlayList(playListName);
            }
            else
            {
                list.setTitle(playListName);
                model.updatePlayList(list);
            }
        } 
        catch (ModelException ex)
        {
            Logger.getLogger(NewPlayListController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
       
        closeWindow();
    }
    
    /**
     * Close the window
     */
    private void closeWindow()
    {
        Stage stage = (Stage) txtFieldName.getScene().getWindow();
        stage.close();
    }

    /**
     * If the "EDIT" mode is selected, set the text field to the title of the edited play list
     * @param list The play list currently being edited
     * @throws Exception If the parameter is null
     */
    public void setText(PlayList list) throws Exception
    {
        try 
        {
            txtFieldName.setText(list.getTitle());
        }
        catch (NullPointerException ex)
        {
            throw new Exception("No list selected!");
        }
    }
    
     /**
     * Show a new alert window, with the text of the error
     * @param ex The exception which will be used to display the message
     */
    private void showAlert(Exception ex)
    {
        Alert a = new Alert(Alert.AlertType.ERROR, "An error occured: " + ex.getMessage(), ButtonType.OK);
        a.show();
    }
}
