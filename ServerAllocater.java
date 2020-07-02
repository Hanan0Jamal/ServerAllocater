/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hanan
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.management.Query.value;

public class ServerAllocater extends Thread {
   private int neededExtraSpace;
   private  double neededExtraServers;
   private int freeSpace; 
   public final static int maxSpinned = 100; 
   private  boolean isActive = true; 
   private int AllocatingSpace;
   private int spinnedSpaces;
   public Server server;
   private static HashMap <ServerAllocater,Integer> hashrunning;
   
   
    public ServerAllocater(int AllocatingSpace) {
        this.AllocatingSpace = AllocatingSpace;
    }
    
   @Override
    public void run(){
        try {
            hashrunning.put(this, this.AllocatingSpace);
            allocate(AllocatingSpace);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerAllocater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
           Logger.getLogger(ServerAllocater.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

    
    private synchronized void allocate(int AllocatingSpace) throws InterruptedException, SQLException{
        if(isActive){ 
            boolean isFound = getFreeServer(AllocatingSpace);
            if(isFound){
                System.out.println("Allocated"+ " " + AllocatingSpace);
            }
            else if (isFound == false){ 
                neededExtraSpace = calculateNeededSpace(AllocatingSpace);
              
                spinning(neededExtraSpace);     
            }   
        }
    }  
    
    private synchronized void spinning(double neededExtraSpace) throws InterruptedException, SQLException{  
       isActive = false;
       updateFreeServers(neededExtraSpace);
       System.out.println("Sipnned"+" "+neededExtraSpace);
       
       allocate(AllocatingSpace);    
    }
    
    private int calculateNeededSpace(int AllocatingSpace){
        int neededSpace = 0;
        Iterator hmIterator = hashrunning.entrySet().iterator(); 
        while (hmIterator.hasNext()) { 
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            if((int)mapElement.getKey()>=AllocatingSpace){
                neededSpace+=1;   
            }  
         } 
        return neededSpace;   
    } 
    private void updateFreeSpace(int new_value) throws SQLException {
        try{
        Class.forName("com.mysql.jdbc.Driver"); 
        Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/ServerAllocater","",""); 
        Statement Statement =con.createStatement();  
        ResultSet ResultSet=Statement.executeQuery("UPDATE `Spaces` SET `FreeSpace` = `new_value'");  
            con.close();  
        
    }  catch (ClassNotFoundException ex) {
           Logger.getLogger(ServerAllocater.class.getName()).log(Level.SEVERE, null, ex);
       }     
    }

 
    private boolean getFreeServer(int AllocatingSpace) throws SQLException {
        int newSpace =0;
        ResultSet ResultSet=null;
        try{
        Class.forName("com.mysql.jdbc.Driver"); 
        Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/ServerAllocater","Hanan","123"); 
        Statement Statement =con.createStatement();  
        ResultSet=Statement.executeQuery("select Server from Servers where 'space'> = 'AllocatingSpace' AND 'isAllocated' ='False'");  
        ResultSet ResultSet1=Statement.executeQuery("UPDATE `Server` SET `isAllocated` = `true' AND 'space' = 'space-AllocatingSpace'"); 
        
        con.close();  
         
        
    }  catch (ClassNotFoundException ex) {
           Logger.getLogger(ServerAllocater.class.getName()).log(Level.SEVERE, null, ex);
           
       }   
           if(ResultSet != null){
            return true;
            }
            else 
                return false;
    }

    private void updateFreeServers(double neededExtraSpace) throws SQLException {
        while(neededExtraSpace !=0){
        try{
        Class.forName("com.mysql.jdbc.Driver"); 
        Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/ServerAllocater","Hanan","123"); 
        Statement Statement =con.createStatement();  
        ResultSet ResultSet1=Statement.executeQuery("select Server-id from Servers ORDER BY ID DESC LIMIT 1'");
        long id = ResultSet1.getLong(1);
        ResultSet ResultSet=Statement.executeQuery("INSERT INTO Server VALUES (id+1, false, 100);");  
            con.close();  
        
    }  catch (ClassNotFoundException ex) {
           Logger.getLogger(ServerAllocater.class.getName()).log(Level.SEVERE, null, ex);
       }
        neededExtraSpace--;}
    
}

}

