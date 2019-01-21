
package library.assistant.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValues();
    }    

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        int ndays = Integer.parseInt(nDaysWithoutFine.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        String uname = username.getText();
        String pass = password.getText();
        
        Preferences preference = new Preferences();
        preference.setnDaysWithoutFine(ndays);
        preference.setFinePerDay(fine);
        preference.setUsername(uname);
        preference.setPassword(pass);
        
        Preferences.writePreferencesToConfigFile(preference);
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        ((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }

    private void initDefaultValues() {
        Preferences preference = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preference.getFinePerDay()));
        finePerDay.setText(String.valueOf(preference.getFinePerDay()));
        username.setText(String.valueOf(preference.getUsername()));
        password.setText(String.valueOf(preference.getPassword()));
    }
        
    
}
