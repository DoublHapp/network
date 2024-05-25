package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class MainClass extends JFrame {
    private final JLabel balanceLabel;
    private final JButton checkBalanceButton;
    private final JButton withdrawButton;

    private final JButton exitButton;
    private final JTextField withdrawAmountField;
    private final Socket socket;
    private final DataOutputStream OuttoServer;
    private final BufferedReader infromServer;
    private double balance=0;

    public MainClass(Socket socket) throws IOException {
        setLocationRelativeTo(null);//居中显示
        this.socket = socket;
        this.OuttoServer = new DataOutputStream(socket.getOutputStream());
        this.infromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        balanceLabel=new JLabel("余额:未知");
        checkBalanceButton = new JButton("查询余额");
        withdrawButton = new JButton("取款");
        exitButton=new JButton("退卡");
        withdrawAmountField = new JTextField(20);


        checkBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 发送BALA
                    OuttoServer.writeBytes("BALA"+'\n');
                    //读取AMNT
                    String responseToBala = infromServer.readLine();
                    System.out.println(responseToBala);
                     balance=MessageHandle.AMNT_Handle(responseToBala);
                    if(balance!=-1){
                    //显示余额
                       balanceLabel.setText("余额"+balance);

                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Server  response Error.");
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Server  response Error: " + ex.getMessage());
                }
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountStr = withdrawAmountField.getText();
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(null, "请输入有效的取款金额！");
                        return;
                    }

                    // 发送WDRA
                    OuttoServer.writeBytes("WDRA"+" "+amountStr+'\n');
                    //读取525
                    String responseToWdra = infromServer.readLine();
                    System.out.println(responseToWdra);
                    if (MessageHandle.OK_Handle(responseToWdra)) {
                        //刷新余额显示
                        balance=balance-amount;
                        balanceLabel.setText("余额"+balance);

                        JOptionPane.showMessageDialog(null, "取款成功！");
                    } else {
                        JOptionPane.showMessageDialog(null, "取款失败: ");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "请输入有效数字金额！");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Server  response Error: " + ex.getMessage());
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //发送BYE
                try {
                    OuttoServer.writeBytes("BYE"+'\n');
                    String responseToBye=infromServer.readLine();
                    System.out.println(responseToBye);
                    if(MessageHandle.BYE_Handle(responseToBye)){
                        //关闭TCP连接
                        socket.close();
                        JOptionPane.showMessageDialog(null, "成功退卡");
                        //关闭主界面
                        dispose();
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Server  response Error.");
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        // 设置GUI布局
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("取款金额: "));
        panel.add(withdrawAmountField);
        panel.add(withdrawButton);
        panel.add(balanceLabel);
        panel.add(checkBalanceButton);
        panel.add(exitButton);

        this.add(panel);
        this.setTitle("银行操作");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
}
