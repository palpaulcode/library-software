
package library.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.memberlist.MemberListController;

public class MemberAddController implements Initializable {

    DatabaseHandler handler;
    
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private AnchorPane rootPane;
    
    private boolean isInEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mID = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();
        
        Boolean flag  = mName.isEmpty()||mID.isEmpty()||mMobile.isEmpty()||mEmail.isEmpty();
        if(flag){
            AlertMaker.showErrorMessage("Cant Process New Member", "Please Enter In All Fields");
            return;
        }
        
        if(isInEditMode){
            handleUpdateMember();
            return;
        }
        
        String str = "INSERT INTO MEMBER VALUES ("
                + "'" + mID+ "',"
                + "'" + mName+ "'," 
                + "'" + mMobile+ "',"
                + "'" + mEmail+ "'"
                +")";
        System.out.println(str);
        if(handler.execAtion(str)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Saved");
            alert.showAndWait();
        } else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error occurred. Could not save");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }
    
    public void inflateUI(MemberListController.Member member ){
        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        
        isInEditMode = Boolean.TRUE;
    }

    private void handleUpdateMember() {
        MemberListController.Member member;
        member = new MemberListController.Member(name.getText(), id.getText(), mobile.getText(), email.getText());
        if(handler.updateMember(member))
        {
            AlertMaker.showSimpleAlert("Success", "Member Successfully Updated");
        }
        else
        {
            AlertMaker.showErrorMessage("Failed", "Could Not Update Member");
        }
        
    }

}