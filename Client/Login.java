package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Login extends JFrame {
    private JTextField useridField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private final Socket socket;

    public Login(Socket socket) {
        setLocationRelativeTo(null);//居中显示
        this.socket=socket;
        useridField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userid = useridField.getText();
                String password = new String(passwordField.getPassword());
                // 验证用户id
                try {
                    //发送HELO
                    DataOutputStream OutToServer = new DataOutputStream(socket.getOutputStream());
                    OutToServer.writeBytes("HELO"+" "+userid+'\n');
                    //读取500
                    BufferedReader inFromServer=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String responseToHelo= inFromServer.readLine();
                    System.out.println(responseToHelo);
                    if(MessageHandle.AuthRequire_Handle(responseToHelo)){
                        //发送PASS
                            OutToServer.writeBytes("PASS"+" "+password+'\n');
                        //读取525
                        String responseToPass= inFromServer.readLine();
                        System.out.println(responseToPass);
                        if(MessageHandle.OK_Handle(responseToPass)){
                            //实例化主界面
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        new MainClass(socket);
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }

                                }
                            });
                            //关闭登录界面
                            dispose();
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Server  response Error.");
                        }

                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Server  response Error.");
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Username: "));
        panel.add(useridField);
        panel.add(new JLabel("Password: "));
        panel.add(passwordField);
        panel.add(loginButton);

        this.add(panel);
        this.setTitle("User Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }


}
