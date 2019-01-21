
package library.assistant.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javax.swing.JOptionPane;
import library.assistant.ui.listbook.BookListController.Book;
import library.assistant.ui.memberlist.MemberListController.Member;

public class DatabaseHandler {
    String DATABASE = "LIBRARYSYS"; //to be changed to LIBRARYSYS
    
    private static DatabaseHandler handler = null;
   
    String db_url = "jdbc:mysql://localhost:3306/";
    String userPass = "?user=root&password="; //mysql?zeroDateTimeBehavior=convertToNull";
    String username = "root";
    String password = "";
   
    private static Connection conn = null;
    private static Statement stmt = null;
    
    private DatabaseHandler()
    {
      createDatabase();
      createConnection();
      setupBookTable();
      setupMemberTable();
      setupIssueTable();
      setupMessageInTable();
      setupMessageOutTable();
      setupMessageLogTable();
    }

    public static DatabaseHandler getInstance()
    {
        if(handler == null){
           handler = new DatabaseHandler();
        }
        return handler;
    }
    
    void createConnection(){
       try {
           Class.forName("com.mysql.jdbc.Driver").newInstance();
           conn = DriverManager.getConnection(db_url+DATABASE,username,password);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e){
                JOptionPane.showMessageDialog(null, "Cant Load Database","Database error",JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
    }
    void createDatabase(){
        try {
            //System.out.println("ATTEMPTING CREATING DATABASE " + DATABASE);
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(db_url+userPass);
            stmt= conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE );
         } catch(ClassNotFoundException | SQLException e){
             System.out.println("UNABLE TO CREATE DATABASE");
             e.printStackTrace();
         }
    }
    
    void setupBookTable() {
        String TABLE_NAME = "BOOK"; //to be changed to BOOK
         try{
            System.out.println("Checking if table " +TABLE_NAME + " exists");
            
            stmt= conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            System.out.println("Initializing . . .");
            if (tables.next()){
                System.out.println("Table " +TABLE_NAME + " already exists!");
             } 
            else {
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + " id varchar(200) primary key,\n"
                + " title varchar(200),\n"
                + " author varchar(200),\n"
                + " publisher varchar(200),\n"
                + " isAvail boolean default true"
                + " )" );
               System.out.println("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY");
             }
         } catch (SQLException e){
            System.err.println(e.getMessage() + " - - - - >> SetupDatabase");
        } finally {
            
        }
    } 
    void setupMemberTable() {
        String TABLE_NAME = "MEMBER"; 
         try{
            System.out.println("Checking if table " +TABLE_NAME + " exists");
            
            stmt= conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            System.out.println("Initializing . . .");
            if (tables.next()){
                System.out.println("Table " +TABLE_NAME + " already exists!");
             } 
            else {
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + " id varchar(200) primary key,\n"
                + " name varchar(200),\n"
                + " mobile varchar(20),\n"
                + " email varchar(100)\n"
                + " )");
               System.out.println("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY");
             }
         } catch (SQLException e){
            System.err.println(e.getMessage() + " - - - - >> SetupDatabase");
        } finally {
            
        }
    } 
    
    void setupIssueTable() {
        String TABLE_NAME = "ISSUE"; 
         try{
            System.out.println("Checking if table " +TABLE_NAME + " exists");
            
            stmt= conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            System.out.println("Initializing . . .");
            if (tables.next()){
                System.out.println("Table " +TABLE_NAME + " already exists!");
             } 
            else {
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + " bookID varchar(200) primary key,\n"
                + " memberID varchar(200),\n"
                + " issueTime timestamp default CURRENT_TIMESTAMP,\n"
                + " renew_count integer default 0,\n"
                + " FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                + " FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                + " )" );
               System.out.println("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY");
             }
         } catch (SQLException e){
            System.err.println(e.getMessage() + " - - - - >> SetupDatabase");
        } finally {
            
        }
    } 
    
    void setupMessageOutTable() {
        String TABLE_NAME = "MESSAGEOUT"; 
         try{
            System.out.println("Checking if table " +TABLE_NAME + " exists");
            
            stmt= conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            System.out.println("Initializing . . .");
            if (tables.next()){
                System.out.println("Table " +TABLE_NAME + " already exists!");
             } 
            else {
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + " Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + " MessageTo VARCHAR(80),\n"
                + " MessageFrom VARCHAR(80),\n"
                + " MessageText VARCHAR(800),\n" 
                + " MessageType VARCHAR(80),\n"
                + " Gateway VARCHAR(80),\n"
                + " UserId VARCHAR(80),\n"
                + " UserInfo TEXT,\n"
                + " Priority INT,\n"
                + " Scheduled DATETIME,\n"
                + " ValidityPeriod INT,\n"
                + " IsSent TINYINT(1) NOT NULL DEFAULT 0,\n"
                + " IsRead TINYINT(1) NOT NULL DEFAULT 0"
                + " )" );
                
                if(stmt.execute("CREATE INDEX IDX_IsRead ON MessageOut (IsRead)")){
                    System.out.println("Index created Successfully");
                }else{
                    System.out.println("failed to create index");
                }
                
               System.out.println("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY");
             }
         } catch (SQLException e){
            System.err.println(e.getMessage() + " - - - - >> SetupDatabase");
        } finally {
            
        }
    } 
    
    void setupMessageInTable() {
        String TABLE_NAME = "MESSAGEIN"; 
         try{
            System.out.println("Checking if table " +TABLE_NAME + " exists");
            
            stmt= conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            System.out.println("Initializing . . .");
            if (tables.next()){
                System.out.println("Table " +TABLE_NAME + " already exists!");
             } 
            else {
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + " Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + " SendTime DATETIME,\n"
                + " ReceivedTime DATETIME,\n"
                + " MessageFrom VARCHAR(80),\n"
                + " MessageTo VARCHAR(80),\n"
                + " SMSC VARCHAR(80),\n"
                + " MessageText TEXT,\n"
                + " MessageType VARCHAR(80),\n"
                + " MessageParts INT,\n"
                + " MessagePDU TEXT,\n"
                + " Gateway VARCHAR(80),\n"
                + " UserId VARCHAR(80)"
                + " )" );
               System.out.println("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY");
             }
         } catch (SQLException e){
            System.err.println(e.getMessage() + " - - - - >> SetupDatabase");
        } finally {
            
        }
    } 
    
    void setupMessageLogTable() {
        String TABLE_NAME = "MESSAGELOG"; 
         try{
            System.out.println("Checking if table " +TABLE_NAME + " exists");
            
            stmt= conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            System.out.println("Initializing . . .");
            if (tables.next()){
                System.out.println("Table " +TABLE_NAME + " already exists!");
             } 
            else {
                stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + " Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + " SendTime DATETIME,\n"
                + " ReceivedTime DATETIME,\n"
                + " StatusCode INT,\n"
                + " StatusText VARCHAR(80),\n"
                + " MessageTo VARCHAR(80),\n"
                + " MessageFrom VARCHAR(80),\n"
                + " MessageText TEXT,\n"
                + " MessageType VARCHAR(80),\n"
                + " MessageId VARCHAR(80),\n"
                + " ErrorCode VARCHAR(80),\n"
                + " ErrorText TEXT,\n"
                + " Gateway VARCHAR(80),\n"
                + " MessagePDU TEXT,\n"
                + " Connector VARCHAR(80),\n"
                + " UserId VARCHAR(80),\n"
                + " UserInfo TEXT"
                + " )" );
                
                if(stmt.execute("CREATE INDEX IDX_MessageId ON MessageLog (MessageId,SendTime)")){
                    System.out.println("index 2 created successfully");
                }else{
                    System.out.println("fialed to create index 2");
                }
                
               System.out.println("TABLE " + TABLE_NAME + " CREATED SUCCESSFULLY");
             }
         } catch (SQLException e){
            System.err.println(e.getMessage() + " - - - - >> SetupDatabase");
        } finally {
            
        }
    } 
    
    public ResultSet execQuery(String query){
        ResultSet result;
        try{
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
           } catch (SQLException ex){
            System.out.println("exception at execQuery:datahandler 1 " + ex.getLocalizedMessage());
            return null;
           } finally {
        } 
        return result;
    }
    
    public boolean execAtion(String qu){
        try{
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "error:" + ex.getMessage(),"error occurred",JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler 2" + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }
    
    public boolean deletebook(Book book){
        try {
            String deleteStatment = "DELETE FROM BOOK WHERE ID = ?";
            PreparedStatement stmt  = conn.prepareStatement(deleteStatment);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if (res == 1){
            return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            return false;
    }
    
    public boolean isBookAlreadyIssued(Book book){
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookID = ?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1 , book.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                System.out.println(count);
                return (count>0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateBook(Book book){
        try {
            String update = "UPDATE BOOK SET title = ?, author = ?, publisher = ? WHERE id = ?";
            PreparedStatement stmt =conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return (res>0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateMember(Member member){
        try {
            String update = "UPDATE MEMBER SET name = ?, mobile = ?, email = ? WHERE id = ?";
            PreparedStatement stmt =conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getMobile());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return (res>0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean deletemember(Member member){
        try {
            String deleteStatment = "DELETE FROM MEMBER WHERE id = ?";
            PreparedStatement stmt  = conn.prepareStatement(deleteStatment);
            stmt.setString(1, member.getId());
            int res = stmt.executeUpdate();
            if (res == 1){
            return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            return false;
    }
    
    public boolean isMemberIssuedWithBook(Member member){
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE memberID = ?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1 , member.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                System.out.println(count);
                return (count>0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public ObservableList<PieChart.Data> getBookGraphStatistics(){
        ObservableList<PieChart.Data> data =FXCollections.observableArrayList();
        
        try{
            String qu1 = "SELECT COUNT(*) FROM BOOK";
            String qu2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                    int count = rs.getInt(1);
                    data.add(new PieChart.Data("Total Books(" + count + ")" , count));
                }
                rs = execQuery(qu2);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    data.add(new PieChart.Data("Issued Books(" + count + ")", count));
                }
            }catch(Exception e){
                e.printStackTrace();
        }
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphStatistics(){
        ObservableList<PieChart.Data> data =FXCollections.observableArrayList();
        
        try{
            String qu1 = "SELECT COUNT(*) FROM MEMBER";
            String qu2 = "SELECT COUNT(DISTINCT memberID) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                    int count = rs.getInt(1);
                    data.add(new PieChart.Data("Total Members(" + count + ")" , count));
                }
                rs = execQuery(qu2);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    data.add(new PieChart.Data("Members With Books(" + count + ")", count));
                }
            }catch(Exception e){
                e.printStackTrace();
        }
        return data;
    }
}

