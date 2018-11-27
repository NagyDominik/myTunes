package mytunes.gui.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mytunes.gui.Model.MediaPlayerModel;
import mytunes.gui.Model.ModelException;

/**
 * A controller that controls a CategoryAdd window.
 *
 * @author sebok
 */
public class CategoryAddController implements Initializable
{
    private MediaPlayerModel model;
    @FXML
    private TextField txtFieldName;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        model = MediaPlayerModel.getInstance();
    }    
    
    /**
     * Save the entered string as a new category. 
     */
    @FXML
    private void btnSaveClick(ActionEvent event)
    {
        String category = txtFieldName.getText();
        try
        {
            model.addNewCategory(category);
        } 
        catch (ModelException ex)
        {
            Logger.getLogger(CategoryAddController.class.getName()).log(Level.SEVERE, null, ex);
            showAlert(ex);
        }
        closeWindow();
    }
    
    /**
     * Close the window
     */
    @FXML
    private void btnCancelClick(ActionEvent event)
    {
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
     * Show a new alert window, with the text of the error
     * @param ex The exception which will be used to display the message
     */
    private void showAlert(ModelException ex)
    {
        Alert a = new Alert(Alert.AlertType.ERROR, "An error occured: " + ex.getMessage(), ButtonType.OK);
        a.show();
    }
    
}
