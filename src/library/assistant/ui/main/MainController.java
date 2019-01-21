package library.assistant.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;

public class MainController implements Initializable {

    DatabaseHandler databaseHandler;
    PieChart bookChart;
    PieChart memberChart;
    
    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
    @FXML
    private TextField bookIDinput;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;
    @FXML
    private TextField memberIDinput;
    @FXML
    private Text memberName;
    @FXML
    private Text mobile;
    private ListView<String> issueDataList;
    @FXML
    private StackPane rootPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;

    Boolean isReadyForSubmission = false;
    @FXML
    private Text memberNameHolder;
    @FXML
    private Text memberEmailHolder;
    @FXML
    private Text memberContactHolder;
    @FXML
    private Text bookNameHolder;
    @FXML
    private Text bookAuthorHolder;
    @FXML
    private Text bookPublisherHolder;
    @FXML
    private Text issueDateHolder;
    @FXML
    private Text daysNumberHolder;
    @FXML
    private Text fineInfoHolder;
    @FXML
    private JFXTextField InputBookID;
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private JFXButton renewButton;
    @FXML
    private JFXButton submitButton;
    @FXML
    private HBox submissionDataContainer;
    @FXML
    private StackPane bookInfoContainer;
    @FXML
    private StackPane memberInfoContainer;
    @FXML
    private Tab bookIssueTab;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(book_info, 2);
        JFXDepthManager.setDepth(member_info, 2);

        databaseHandler = DatabaseHandler.getInstance();
        
        initDrawer();
        initGraphs();
    }

    
    void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();
        enableDisableGraphs(false);

        String id = bookIDinput.getText();
        String qu = "SELECT * FROM BOOK WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus) ? "Available " : "Not Available ";
                bookStatus.setText(status);

                flag = true;
            }
            if (!flag) {
                bookName.setText("No such book");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void clearMemberCahe() {
        memberName.setText("");
        mobile.setText("");
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCahe();
        enableDisableGraphs(false);
        
        String id = memberIDinput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;

        try {
            while (rs.next()) {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");

                memberName.setText(mName);
                mobile.setText(mMobile);

                flag = true;
            }
            if (!flag) {
                memberName.setText("No Such Member");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberId = memberIDinput.getText();
        String bookId = bookIDinput.getText();

        JFXButton yesButton = new JFXButton("YES");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1)->{
            String str = "INSERT INTO ISSUE (bookID,memberID) VALUES ( "
                    + "'" + bookId + "',"
                    + "'" + memberId + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE ID = '" + bookId + "'";
            System.out.println(str + " and " + str2);
            if (databaseHandler.execAtion(str) && databaseHandler.execAtion(str2)) {
                JFXButton button = new JFXButton("Done");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issued Successfuly", null);
                refreshGraphs();
            } else {
                JFXButton button = new JFXButton("Okay, I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Failed", null);
            }
            clearIssueEntries();
        });
        
        JFXButton noButton = new JFXButton("NO");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1)->{
            JFXButton button = new JFXButton("Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue cancelled", null);
            clearIssueEntries();
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Issue", ""
                    + "Are You Sure You Want To Issue Book " + bookName.getText() + " to " + memberName.getText() + " ?");
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) {
        clearEntries();
        isReadyForSubmission = false;
        String id = InputBookID.getText();
        
        try{
            String mySqlQuery = "SELECT ISSUE.bookID, ISSUE.memberID, ISSUE.issueTime, ISSUE.renew_count,\n"
                + "MEMBER.name, MEMBER.mobile, MEMBER.email,\n"
                + "BOOK.title, BOOK.author, BOOK.publisher\n"
                + "FROM ISSUE\n"
                + "LEFT JOIN MEMBER\n"
                + "ON ISSUE.memberID = MEMBER.ID\n"
                + "LEFT JOIN BOOK\n"
                + "ON ISSUE.bookID = BOOK.id\n"
                + "WHERE ISSUE.bookID =  '" + id + "'";
        
        ResultSet rs = databaseHandler.execQuery(mySqlQuery);
        
          if(rs.next()){
            memberNameHolder.setText(rs.getString("name"));
            memberContactHolder.setText(rs.getString("mobile"));
            memberEmailHolder.setText(rs.getString("email"));
            
            bookNameHolder.setText(rs.getString("title"));
            bookAuthorHolder.setText(rs.getString("author"));
            bookPublisherHolder.setText(rs.getString("publisher"));
            
            Timestamp mIssueTime = rs.getTimestamp("issueTime");
            Date dateOfIssue = new Date(mIssueTime.getTime());
            issueDateHolder.setText(dateOfIssue.toString());
            Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
            Long daysElapsed = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS);
            daysNumberHolder.setText(daysElapsed.toString());
            fineInfoHolder.setText("Not supported yet");
                       
            isReadyForSubmission = true;
            disableEnableControls(true);
            submissionDataContainer.setOpacity(1);
          }
          else{
              JFXButton button =new JFXButton("Okay, I'll check");
              AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "No such Book Exists In Issue Database",null);
          }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void loadSubmissionOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "PLease selcet a book to submit", "Cannot submit a null book ;-)");
            return;
        }
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            String id = InputBookID.getText();
            String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET isAvail = true WHERE id = '" + id + "'";

            if (databaseHandler.execAtion(ac1) && databaseHandler.execAtion(ac2)) {
                JFXButton btn = new JFXButton("Done");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has Been Submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(1);
            } else {
                JFXButton btn = new JFXButton("Okay, I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book Submission Has Failed", null);
            }
        });
        JFXButton noButton = new JFXButton("No");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev)->{
            JFXButton btn = new JFXButton("Okay");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book Submission Has Been Cancelled", null);
        });
        
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Book Submission", "Are You Sure You Want To Return The Book ?");
    }

    @FXML
    private void loadRenewOp(ActionEvent event) {
        if (!isReadyForSubmission) {
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("Failed");
            alert1.setHeaderText(null);
            alert1.setContentText("Please Select a Book To Renew");
            alert1.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Book Submission");
        alert.setHeaderText(null);
        alert.setContentText("Are You Sure You Want To Renew The Book ?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String act = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE BOOKID = '" + InputBookID.getText() + "'";
            System.out.println(act);
            if (databaseHandler.execAtion(act)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Renewed Successfully");
                alert1.showAndWait();
                loadBookInfo2(null);
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Renew Has Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Renew Cancelled");
            alert1.showAndWait();
        }

    }

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"), "Add New Book",null);
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"), "Add New Member",null);
    }

    @FXML
    private void handleMenuViewBook(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/listbook/book_list.fxml"), "Books Table",null);
    }

    @FXML
    private void handleMenuViewMember(ActionEvent event) {
        LibraryAssistantUtil.loadWindow(getClass().getResource("/library/assistant/ui/memberlist/member_list.fxml"), "Members Table",null);
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void initDrawer() {
        try {
            VBox toolbar = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/toolbar/toolbar.fxml"));
            drawer.setSidePane(toolbar);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {
            task.setRate(task.getRate()*-1);
            task.play();
            
            if(drawer.isHidden()){
                drawer.open();
            }
            else{
                drawer.close();
            }
        });
    }

    private void clearEntries() {
        memberNameHolder.setText("");
        memberContactHolder.setText("");
        memberEmailHolder.setText("");
        
        bookNameHolder.setText("");
        bookAuthorHolder.setText("");
        bookPublisherHolder.setText("");
        
        issueDateHolder.setText("");
        daysNumberHolder.setText("");
        fineInfoHolder.setText("");
        
        disableEnableControls(false);
        submissionDataContainer.setOpacity(0);
    }
    
    private void disableEnableControls(Boolean enableFlag){
        if(enableFlag){
            renewButton.setDisable(false);
            submitButton.setDisable(false);
        }
        else{
            renewButton.setDisable(true);
            submitButton.setDisable(true);
        }
    }

    private void clearIssueEntries() {
        bookIDinput.clear();
        memberIDinput.clear();
        
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
        
        memberName.setText("");
        mobile.setText("");
        enableDisableGraphs(true);
    }

    private void initGraphs() {
        bookChart = new PieChart(databaseHandler.getBookGraphStatistics());
        memberChart = new PieChart(databaseHandler.getMemberGraphStatistics());
        bookInfoContainer.getChildren().add(bookChart);
        memberInfoContainer.getChildren().add(memberChart);
        
        bookIssueTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                clearIssueEntries();
                if(bookIssueTab.isSelected()){
                    refreshGraphs();
                }
            }
        });
    }

    private void enableDisableGraphs(Boolean status){
        if(status){
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        }else{
            bookChart.setOpacity(0);
            memberChart.setOpacity(0);
        }
    }
    
    private void refreshGraphs(){
        bookChart.setData(databaseHandler.getBookGraphStatistics());
        memberChart.setData(databaseHandler.getMemberGraphStatistics());
    }
}
