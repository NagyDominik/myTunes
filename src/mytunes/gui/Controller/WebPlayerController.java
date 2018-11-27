package mytunes.gui.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * A controller that controls a WebPlayer window !!!WIP!!!
 *
 * @author Dominik
 */
public class WebPlayerController implements Initializable {

    @FXML
    private WebView webView;
    @FXML
    private TextField fieldURL;
    @FXML
    private Button loadURL;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setListener();
    }

    /**
     * Load a video using the entered URL
     */
    private void urlLoad() {
        String url = fieldURL.getText();
        String[] spliturl = url.split(" ");
        if (url.startsWith("h")) {
            webView.getEngine().load(url);
        } else {
            for (int i = 0; i < spliturl.length; i++) {
                if (spliturl[i].contains("height=")) {
                    spliturl[i] = "height=\"97%\"";
                }
                if (spliturl[i].contains("width=")) {
                    spliturl[i] = "width=\"100%\"";
                }
            }
            String correctedURL = "";
            for (String string : spliturl) {
                correctedURL += (string + " ");
            }
            webView.getEngine().loadContent(correctedURL);
        }
    }

    /**
     * Press enter to load the content
     */
    private void setListener() {
        fieldURL.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    urlLoad();
                }
            }
        });
    }

    /**
     * Load a video on press
     */
    @FXML
    private void clickLoadURL(ActionEvent event) {
        urlLoad();
    }

    /**
     * Close the window
     */
    @FXML
    private void clickClose(ActionEvent event) {
        webView.getEngine().load(null);
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}
