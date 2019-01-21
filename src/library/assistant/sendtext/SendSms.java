/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.assistant.sendtext;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import library.assistant.database.DatabaseHandler;

public class SendSms {
    
    DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
    
    //databaseHandler = DatabaseHandler.getInstance();

    public void sendText(){
        Long daysElapsed;
        try{
        String getInfo = "SELECT ISSUE.bookID, ISSUE.memberID, ISSUE.issueTime, ISSUE.renew_count,\n"
            + "MEMBER.name, MEMBER.mobile, MEMBER.email,\n"
            + "BOOK.title, BOOK.author, BOOK.publisher\n"
            + "FROM ISSUE\n"
            + "LEFT JOIN MEMBER\n"
            + "ON ISSUE.memberID = MEMBER.ID\n"
            + "LEFT JOIN BOOK\n"
            + "ON ISSUE.bookID = BOOK.id\n"
            + "WHERE ISSUE.renew_count =  " + 0 + "";
        
        ResultSet rs1 = databaseHandler.execQuery(getInfo);
        
          while(rs1.next()){
              String sName,bTitle,sMobile;
              
                sName=rs1.getString("name");
                bTitle=rs1.getString("title");
                sMobile=rs1.getString("mobile");
                
                Timestamp mIssueTime = rs1.getTimestamp("issueTime");
                Date dateOfIssue = new Date(mIssueTime.getTime());
                Long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
                daysElapsed = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MILLISECONDS);

            if(daysElapsed>=9){
                String sms="Dear " + sName + ",you are requested to return the book " +bTitle+ " to the library. "
                        + "Your lease time expires today.";
                
                String smsQuery="INSERT INTO messageout(messageto,messagetext) VALUES (" +
                "'" + sMobile +"'," +
                "'" + sms +"'" +
                ")";
                
                System.out.println(sName+" "+bTitle+" "+sMobile);
                
                databaseHandler.execAtion(smsQuery);
            }
          }
        
    }catch(Exception ex){
        ex.printStackTrace();
    }     
  }
}



                
     

