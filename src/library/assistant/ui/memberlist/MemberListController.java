package library.assistant.ui.memberlist;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addmember.MemberAddController;
import library.assistant.util.LibraryAssistantUtil;

public class MemberListController implements Initializable {

    ObservableList<Member> list = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadData() {
        list.clear();
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM MEMBER";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                String id = rs.getString("id");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");

                list.add(new Member(name, id, mobile, email));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    private void handleMemberDeleteOperation(ActionEvent event) {
        Member delMember = tableView.getSelectionModel().getSelectedItem();
        if(delMember == null){
            AlertMaker.showErrorMessage("No Member Selected", "Please select a Member to delete");
            return;
        }
        
        if(DatabaseHandler.getInstance().isMemberIssuedWithBook(delMember)){
            AlertMaker.showErrorMessage("Member Cannot Be Deleted", "Member Is Issued With Book.\nCannot Be Deleted");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setContentText("Are You Sure You Want To Delete Member " + delMember.getName() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK){
            Boolean result = DatabaseHandler.getInstance().deletemember(delMember);
            if(result){
                AlertMaker.showSimpleAlert("Delete Member", delMember.getName() + " Successfuly Deleted");
                list.remove(delMember);
            } else {
                AlertMaker.showSimpleAlert("Failed", delMember.getName() + " Could Not Be Deleted");
            }
        }else {
            AlertMaker.showErrorMessage("Deletion Cancelled", "Member Delete Operation Cancelled");
        }
    }

    @FXML
    private void handleMemberRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleMemberEdit(ActionEvent event) {
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null){
            AlertMaker.showErrorMessage("No Memebr Selected", "Please select a Member to Edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();
            
            MemberAddController controller = (MemberAddController)loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
            
            stage.setOnCloseRequest((e)->{
                handleRefresh(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleRefresh(ActionEvent actionEvent) {
        loadData();
    }

    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;

        public Member(String name, String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

    }
}
