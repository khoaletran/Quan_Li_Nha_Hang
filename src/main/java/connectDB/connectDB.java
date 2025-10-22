package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectDB {
    private static Connection con = null;
    private static connectDB instance = new connectDB();

    private connectDB() {}

    public static connectDB getInstance() {
        return instance;
    }

    public void connect() throws SQLException {
        if (con == null || con.isClosed()) {
            String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=QL_NhaHangCrabKing_Nhom02;"
                    + "encrypt=true;trustServerCertificate=true;";
            String user = "nhanvien_app";
            String password = "123456";

            con = DriverManager.getConnection(url, user, password);
            System.out.println("Kết nối thành công tới QL_NhaHangCrabKing_Nhom02");
        }
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                con = null;
                System.out.println("Đã ngắt kết nối");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection() {
        return con;
    }
}
