package library.assistant.ui.listbook;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addbook.BookAddController;
import library.assistant.util.LibraryAssistantUtil;

public class BookListController implements Initializable {

    ObservableList<Book> list = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }

    private void loadData() {
        list.clear();
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM BOOK";
        ResultSet rs = handler.execQuery(qu);
        try {
            while (rs.next()) {
                String titlex = rs.getString("title");
                String id = rs.getString("id");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                boolean avail = rs.getBoolean("isAvail");

                list.add(new Book(titlex, id, author, publisher, avail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tableView.setItems(list);
    }

    @FXML
    private void handleBookDeleteOperation(ActionEvent event) {
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion == null){
            AlertMaker.showErrorMessage("No Book Selected", "Please select a book to delete");
            return;
        }
        
        Boolean del = DatabaseHandler.getInstance().isBookAlreadyIssued(selectedForDeletion);
        if(del){
            AlertMaker.showErrorMessage("Book Cannot Be Deleted", "Book Is Issued And Cannot Be Deleted");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Book");
        alert.setContentText("Are You Sure You Want To Delete Book " + selectedForDeletion.getTitle() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK){
            Boolean result = DatabaseHandler.getInstance().deletebook(selectedForDeletion);
            if(result){
                AlertMaker.showSimpleAlert("Delete Book", selectedForDeletion.getTitle() + " Successfuly Deleted");
                list.remove(selectedForDeletion);
            } else {
                AlertMaker.showSimpleAlert("Failed", selectedForDeletion.getTitle() + " Could Not Be Deleted");
            }
        }else {
            AlertMaker.showErrorMessage("Deletion Cancelled", "Book Delete Operation Cancelled");
        }
    }

    @FXML
    private void handleBookEditOperation(ActionEvent event) {
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null){
            AlertMaker.showErrorMessage("No Book Selected", "Please select a book to delete");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addbook/add_book.fxml"));
            Parent parent = loader.load();
            
            BookAddController controller = (BookAddController)loader.getController();
            controller.inflateUI(selectedForEdit);
            
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
            
            stage.setOnCloseRequest((e)->{
                handleRefresh(new ActionEvent());
            });
            
        } catch (IOException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    public static class Book {

        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability;

        public Book(String title, String id, String author, String pub, Boolean avail) {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(pub);
            this.availability = new SimpleBooleanProperty(avail);
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public boolean getAvailability() {
            return availability.get();
        }

    }
}
