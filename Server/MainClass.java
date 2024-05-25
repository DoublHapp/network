package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class MainClass{
    private static final int PORT = 2525;
    private static int step;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接成功");

                // 处理客户端数据的逻辑
                String message=null;
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream  out = new DataOutputStream(clientSocket.getOutputStream());
                /*waiting for
                 * 0 HELO
                 * 1 PASS
                 * 2 BALA
                 * 3 WDRA
                 * 4 BYE
                 * */
                step=0;
//                System.out.println("step");
                String userid=null;
                while(true){
//                    System.out.println("while");
                    try {
                        message=in.readLine();
                    } catch (IOException e) {
                        System.out.println("客户端链接断开");
                        clientSocket.close();
                        break;
                    }
                    if(message==null) continue;;
                    System.out.println("message is "+message);
                    switch (step){
                        case 0:
                            System.out.println("step 0");
                            userid=MessageHandle.HELO_handle(message);
                            if(userid!=null){
                                step++;
                                out.writeBytes("500 AUTH REQUIRED!"+'\n');
                            }else{
//                                out.writeBytes("your mom");
                                out.writeBytes("401 ERROR!"+'\n');
                            }

                            break;
                        case 1:
                            System.out.println("step 1");
                            if(MessageHandle.PASS_handle(userid,message)) {
                                step++;
                                out.writeBytes("525 OK!" + '\n');
                            }else{
//                                out.writeBytes("your mom");
                                out.writeBytes("401 ERROR!"+'\n');
                            }
                            break;
                        case 2:
                            System.out.println("step 2");
                            //先假定为查询指令
                            switch (message.substring(0,3)){
                                case "BAL":
                                    double balance=MessageHandle.BALA_handle(userid,message);
                                    if(balance>=0){
                                        out.writeBytes("AMNT:"+balance+'\n');
                                        System.out.println("the amount is "+balance);
                                    }else{
                                        out.writeBytes("401 ERROR!"+'\n');
                                        MessageHandle.insertErrorRecord(401,userid);
                                    }
                                    break;
                                case "WDR":
                                    double wd=MessageHandle.WDRA_handle(userid,message);
                                    if(wd>0){
                                        //sql执行取款操作，检查余额是否充足并扣除金额
                                        try (Connection conn = DatabaseConnection.getConnection()) {
                                            conn.setAutoCommit(false); // 开启事务
                                            PreparedStatement stmt = conn.prepareStatement("UPDATE users SET balance=balance-? WHERE userid=?");
                                            stmt.setString(1, String.valueOf(wd));
                                            stmt.setString(2, userid);
                                            int rowsUpdated = stmt.executeUpdate();
                                            if(rowsUpdated>0){
                                                // 取款成功，向withdrawrecord表中插入取款记录
                                                MessageHandle.insertWithdrawRecord(userid, wd);
                                                //发送525
                                                out.writeBytes("525 OK!" + '\n');

                                                conn.commit(); // 提交事务
                                                System.out.println("the wd is "+wd);

                                            }
                                            else{
                                                conn.rollback(); // 回滚事务
                                                out.writeBytes("401 ERROR!" + '\n');
                                                MessageHandle.insertErrorRecord(401,userid);
                                            }
                                        } catch (Exception e) {
                                            out.writeBytes("401 ERROR!" + '\n');
                                            MessageHandle.insertErrorRecord(401,userid);
                                            e.printStackTrace();
                                        }
                                    }else{
                                        out.writeBytes("401 ERROR!"+'\n');
                                        MessageHandle.insertErrorRecord(401,userid);
                                    }
                                    break;
                                case "BYE":
                                    out.writeBytes("BYE"+'\n');
                                    System.out.println(" bye ");
                                    clientSocket.close();
                                    step=-1;
                                    break;
                                default:
//                                    out.writeBytes("401 ERROR!"+'\n');
                            }
                    }
                    if(step==-1) break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error: step "+step);
        }
    }


}
