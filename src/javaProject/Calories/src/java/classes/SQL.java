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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public static ArrayList<Component> findComponents() {
        connect();
        ArrayList<Component> list = new ArrayList<>();
        try {
            ResultSet res =  stat.executeQuery("SELECT * FROM Components;");
            while(res.next()) {
                list.add(new Component(res.getString("Name"),res.getInt("ComponentID"),res.getInt("Calories")));
            }
            return list;
        } catch (SQLException e) {return null;}
    }
    
    public static Dish findDishById(int id) {
        connect(); 
        try {
            ResultSet res = stat.executeQuery("SELECT * FROM Dishes WHERE DishID = "+id+";");
            res.next();
            return new Dish(res.getInt("DishID"),res.getString("Name"));
        } catch(SQLException e) {return null;}
    }
    
    public static Dish findDishByName(String name) {
        connect();
        try{
            ResultSet res =  stat.executeQuery("SELECT * FROM Dishes WHERE Name LIKE \"%"+name+"%\";");
            res.next();
            return new Dish(res.getInt("DishID"),res.getString("Name"));
        } catch(SQLException e) {return null;}
    }
    
    public static Dish findDishByNameOnly(String name) {
        connect();
        try{
            ResultSet res =  stat.executeQuery("SELECT * FROM Dishes WHERE Name = \""+name+"\";");
            res.next();
            return new Dish(res.getInt("DishID"),res.getString("Name"));
        } catch(SQLException e) {return null;}
    }
    
    public static Component findComponentById(int id) {
        connect();
        try {
            ResultSet res = stat.executeQuery("SELECT * FROM Components WHERE ComponentID = "+id+";");
            res.next();
            return new Component(res.getString("Name"),res.getInt("ComponentID"),res.getInt("Calories"));
        } catch(SQLException e) {return null;}
    }
    
    public static Component findComponentByName(String name) {
        connect();
        try {
            ResultSet res = stat.executeQuery("SELECT * FROM Components WHERE Name LIKE \"%"+name+"%\";");
            res.next();
            return new Component(res.getString("Name"),res.getInt("ComponentID"),res.getInt("Calories"));
        } catch(SQLException e) {return null;}
    }
    
    public static ArrayList<Component> findComponentsByDishId(int id) {
        connect();
        ArrayList<Component> list = new ArrayList<>();
        try {
            ResultSet res = stat.executeQuery("SELECT c.*,f.Weight FROM Components c, DishFormulas f WHERE\n"
                    + "f.DishID = "+id+" AND\n"
                    + "c.ComponentID = f.ComponentID ;");
            while(res.next()) {
                list.add(new Component(res.getString("Name"),res.getInt("ComponentID"),res.getInt("Calories"),res.getInt("Weight")));
            }
            return list;
        } catch(SQLException e) {return null;}
    }
    
    public static ArrayList<Dish> findDishesByNameAndComponents(String dish, ArrayList<String> components) {
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
        ArrayList<Dish> list = new ArrayList<>();
        connect();
        try {
        ResultSet res = stat.executeQuery(url);
            while(res.next()) {
                boolean is = false;
                for(Dish d :list) {
                    if(d.getId() == res.getInt("DishID")){
                        d.addCount();
                        is = true;
                        break;
                    }
                }
                if(!is)
                list.add(new Dish(res.getInt("DishID"),res.getString("Name")));
            }
            return list;
        } catch(SQLException e) {return null;}
    }
    
    public static ArrayList<Dish> findDishesByNameAndComponents(Dish dish) {
        String url = "Select d.* from Dishes d,Components c,DishFormulas f WHERE\n" +
"d.Name LIKE \"%"+dish.getName()+"%\" AND\n" +
"(d.DishID = f.DishID AND\n" +
"c.ComponentID = f.ComponentID)";
        int i = 0;
        for(Component s : dish.getComponents()) {
            if(!s.getName().equals("")) {
                if(i == 0)
                    url+=" AND(\n";
                if(i>0)
                    url += " OR ";
                url+="c.Name = \""+s.getName()+"\"\n";
                i++;
            }
        }
        if(i>0)
            url+=")";
        System.out.println(url);
        ArrayList<Dish> list = new ArrayList<>();
        connect();
        try {
        ResultSet res = stat.executeQuery(url);
            while(res.next()) {
                boolean is = false;
                for(Dish d :list) {
                    if(d.getId() == res.getInt("DishID")){
                        d.addCount();
                        is = true;
                        break;
                    }
                }
                if(!is)
                list.add(new Dish(res.getInt("DishID"),res.getString("Name")));
            }
            return list;
        } catch(SQLException e) {return null;}
    }
    
    public static boolean addDish(Dish dish) {
        connect();
        try {
            System.out.println("1");
            ResultSet res = stat.executeQuery("SELECT DishID from Dishes WHERE \n" +
                    "Name = \""+dish.getName()+"\";");
            res.next();
            try {
                res.getInt("DishID");
                return false;
            } catch(Exception e) {}
            System.out.println("2");
            stat.execute("INSERT INTO Dishes(Name) VALUES(\""+dish.getName()+"\");");
            res = stat.executeQuery("SELECT DishID FROM Dishes WHERE Name = \""+dish.getName()+"\";");
            res.next();
            int id = res.getInt("DishID");
            System.out.println("3");
            Dish dish1 = findDishById(id);
            for(Component c :dish.getComponents()) {
                Component comp = findComponentByName(c.getName());
                System.out.println("4");
                if(!c.getName().equals(""))
                dish1.addComponent(new Component(comp.getName(),comp.getID(),comp.getCalories(),c.getWeight()));
            }
            for(Component comp : dish1.getComponents()) {
                System.out.println("5");
                stat.execute("INSERT INTO DishFormulas(DishID,ComponentID,Weight) VALUES ("+id+","+comp.getID()+","+comp.getWeight()+");");
            }
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
}
