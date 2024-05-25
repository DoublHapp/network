package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MessageHandle {
    static public String HELO_handle(String HELOMessage) {
        //切分处理HELO报文，提取userid,验证是否合法，返回true或false
        String[] split = HELOMessage.split(" ");
        if (!split[0].equals("HELO")) return null;
        if(isValidUserid(split[1])) return split[1];
        return null;
    }

    static public boolean PASS_handle(String userid, String PASSMessage) {
        //切分处理PASS报文，提取password,验证是否与userid匹配，返回true或false
        String[] split = PASSMessage.split(" ");
        if (!split[0].equals("PASS")) return false;
        return isValidPassword(userid, split[1]);
    }

    static public double BALA_handle(String userid, String BALAMessage) {
        //处理BALA报文，查看userid的余额，返回数值
        if (!BALAMessage.equals("BALA")) return -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE userid=? ");
            stmt.setString(1, userid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static public double WDRA_handle(String userid, String WDRAMessage) {
        //切分处理WDRA报文，提取amount,查看userid的余额是否充足，返回true或false
        String[] split = WDRAMessage.split(" ");
        if (!split[0].equals("WDRA")) return -1;
        double amnt = 0;
        try {
            amnt = Double.parseDouble(split[1]);
            String str=amnt+"";
            if(str.length() - (str.indexOf(".") + 1)>2) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("客户端输入了非法数字");
            return -1;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE userid=? ");
            stmt.setString(1, userid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getDouble(1) >= amnt) return amnt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    static private boolean isValidUserid(String userid) {
        // 连接数据库并验证用户名
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE userid=?");
            stmt.setString(1, userid);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // 如果查询结果存在，则返回true，否则返回false
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static private boolean isValidPassword(String userid, String password) {
        // 连接数据库并验证用户名和密码
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE userid=? AND password=?");
            stmt.setString(1, userid);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // 如果查询结果存在，则返回true，否则返回false
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    static public void insertWithdrawRecord(String userid, double amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO withdrawrecord (userid, wdamount, wdtime) VALUES (?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userid);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void insertErrorRecord(int type,String userid) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO errorrecord (errortype, userid, errortime) VALUES (?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, type);
            stmt.setString(2, userid);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
