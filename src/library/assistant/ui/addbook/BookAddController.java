
package library.assistant.ui.addbook;

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
import library.assistant.ui.listbook.BookListController;

public class BookAddController implements Initializable {

    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField publisher;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;

    DatabaseHandler databaseHandler;
    @FXML
    private AnchorPane rootPane;
    
    private boolean isInEditMode = Boolean.FALSE;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler = DatabaseHandler.getInstance();
    }   

    @FXML
    private void addbook(ActionEvent event) {
        String bookID = id.getText();
        String bookName = title.getText();
        String bookAuthor = author.getText();
        String bookPublisher = publisher.getText();
        
        if(bookID.isEmpty()||bookName.isEmpty()||bookAuthor.isEmpty()||bookPublisher.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all fields");
            alert.showAndWait();
            return;
        }
        
        if(isInEditMode)
        {
            handleEditOperation();
            return;
        }
        
        String qu = "INSERT INTO BOOK VALUES (" +
                "'" + bookID +"'," +
                "'" + bookName +"'," +
                "'" + bookAuthor +"'," +
                "'" + bookPublisher +"'," +
                "" + true +"" +
                ")";
        System.out.println(qu);
        if(databaseHandler.execAtion(qu)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage)rootPane.getScene().getWindow();
        stage.close();
    }
    
    public void inflateUI(BookListController.Book book){
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }

    private void handleEditOperation() {
        BookListController.Book book;
        book = new BookListController.Book(title.getText(), id.getText(), author.getText(), publisher.getText(), true);
        if(databaseHandler.updateBook(book))
        {
            AlertMaker.showSimpleAlert("Success", "Book Successfully Updated");
        }
        else
        {
            AlertMaker.showErrorMessage("Failed", "Could Not Update Book");
        }
    }
}
