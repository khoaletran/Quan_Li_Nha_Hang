package dao;

import connectDB.connectDB;
import entity.Mon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class DAO_MonAn {

    public static ArrayList<Mon> getAllMon() {
        ArrayList<Mon> dsMon = new ArrayList<>();
        try {
            connectDB.getInstance();
            Connection con = connectDB.getConnection();

            String sql = "";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsMon;
    }
}
