package client;

import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {

    public LoginGUI(){

        setTitle("ConnectDesk");
        setSize(320,220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel mainPanel = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(22,22,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),22,22);
            }
        };

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,25,20,25));
        getContentPane().setBackground(new Color(18,18,30));

        JLabel title = new JLabel("ConnectDesk");
        title.setFont(new Font("Segoe UI",Font.BOLD,18));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your name to continue");
        subtitle.setFont(new Font("Segoe UI",Font.PLAIN,12));
        subtitle.setForeground(new Color(170,170,200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        nameField.setBackground(new Color(36,36,60));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        nameField.setFont(new Font("Segoe UI",Font.PLAIN,13));

        JButton enterBtn = new JButton("Enter Chat");
        enterBtn.setFocusPainted(false);
        enterBtn.setBackground(new Color(120,80,255));
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFont(new Font("Segoe UI",Font.BOLD,13));
        enterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(6));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(18));
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(enterBtn);

        add(mainPanel,BorderLayout.CENTER);

        enterBtn.addActionListener(e->{
            String name = nameField.getText().trim();
            if(!name.isEmpty()){
                new ClientGUI(name);
                dispose();
            }
        });

        nameField.addActionListener(e->enterBtn.doClick());

        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args){
        new LoginGUI();
    }
}