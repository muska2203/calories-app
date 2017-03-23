/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author admin
 */
public class SQL {
    private static final String url = "jdbc:mysql://localhost:3306/calories"; 
    private static final String userName = "root";
    private static final String password = "root";
    static Connection connect = null;
    static Statement stat = null;
    
    private static void connect() {
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager.getConnection(url, userName, password);
            stat = connect.createStatement();
        } catch(Exception e) {
            System.out.println("Error driver");
        }
    }
    
    public static ResultSet findComponents() throws SQLException {
        connect();
        return stat.executeQuery("SELECT * FROM Components;");
    }
    
    public static ResultSet findDishById(int id) throws SQLException{
        connect();
        return stat.executeQuery("SELECT * FROM Dishes WHERE DishID = "+id+";");
    }
    
    public static ResultSet findDishesByName(String name) throws SQLException {
        connect();
        return stat.executeQuery("SELECT * FROM Dishes WHERE Name LIKE \"%"+name+"%\";");
    }
    
    public static ResultSet findComponentById(int id) throws SQLException {
        connect();
        return stat.executeQuery("SELECT * FROM Components WHERE ComponentID = "+id+";");
    }
    
    public static ResultSet findComponentsByName(String name) throws SQLException {
        connect();
        return stat.executeQuery("SELECT * FROM Components WHERE Name LIKE \"%"+name+"%\";");
    }
    
    public static ResultSet findComponentsByDishId(int id) throws SQLException {
        connect();
        return stat.executeQuery("SELECT c.* FROM Components c, DishFormulas f WHERE\n"
                + "f.DishID = "+id+" AND\n"
                + "c.ComponentID = f.ComponentID ;");
    }
    
    public static ResultSet findDishByNameAndComponents(String dish, String[] components) throws SQLException {
        String url = "Select d.* from Dishes d,Components c,DishFormulas f WHERE\n" +
"d.Name LIKE \"%"+dish+"%\" AND\n" +
"(d.DishID = f.DishID AND\n" +
"c.ComponentID = f.ComponentID)";
        int i = 0;
        for(String s: components) {
            if(!s.equals("")) {
                if(i == 0)
                    url+=" AND(\n";
                if(i>0)
                    url += " OR ";
                url+="c.Name = \""+s+"\"\n";
                i++;
            }
        }
        if(i>0)
            url+=")";
        System.out.println(url);
        connect();
        return stat.executeQuery(url);
    }
    
}
