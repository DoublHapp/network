package Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class TCP_Connection extends JFrame {
    private final JTextField ipField;

    public TCP_Connection() {
        //设置窗口属性
        this.setSize(520, 180); // 设置窗口大小
        this.setResizable(false); // 设置窗口大小不可改变
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭操作
        this.setLocationRelativeTo(null); // 让窗口居中显示

        ipField = new JTextField(20);
        ipField.setBounds(100,50,200,40);
        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(320,50,100,40);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIP = ipField.getText();
                int serverPort = 2525;//默认端口号：2525
                try {
                    Socket socket = new Socket(serverIP, serverPort);
                    // 连接成功，可以进行后续操作
                    JOptionPane.showMessageDialog(null, "Connected to Server.");

                    // 实例化用户登录界面
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login(socket);
                        }
                    });

                    dispose();


                } catch (IOException ex) {
                    // 处理连接异常
                    JOptionPane.showMessageDialog(null, "Cannot connect to the server: " + ex.getMessage());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(null); // 不使用布局管理器，便于手动定位组件
        JLabel ipLabel=new JLabel("Server IP:");
        ipLabel.setBounds(10,50,80,40);
        panel.add(ipLabel);
        panel.add(ipField);
        panel.add(connectButton);
        panel.setBounds(0,0,520,380);


        this.add(panel);
        this.setTitle("TCP Connection");
        this.setVisible(true);
    }

    public static void main(String[] args) {
        // 实例化TCP连接界面
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TCP_Connection();
            }
        });

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to the default look and feel.
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
