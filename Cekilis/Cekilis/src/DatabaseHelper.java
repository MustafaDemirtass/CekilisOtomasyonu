import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cekilis_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";     
    private static final String PASS = "admin";     

    
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    
    public void addParticipant(String name) {
        String sql = "INSERT INTO participants(name) VALUES(?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public List<Participant> getAllParticipants() {
        List<Participant> list = new ArrayList<>();
        String sql = "SELECT id, name FROM participants";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Participant(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("Listeleme hatası: " + e.getMessage());
        }
        return list;
    }

    
    public void clearParticipants() {
        String sql = "TRUNCATE TABLE participants"; 
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Silme hatası: " + e.getMessage());
        }
    }
    public void deletParticipant(int id){
        String sql = "Delete from participants where id= ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }catch (SQLException e){
            System.out.println("Silme Hatası: "+e.getMessage());
        }
    }
}
