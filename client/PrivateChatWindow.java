package client;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivateChatWindow extends JFrame {

    String otherUser;
    String myUsername;
    PrintWriter out;

    JPanel chatPanel;
    JScrollPane scrollPane;
    JTextField messageField;

    public PrivateChatWindow(String otherUser,String myUsername,PrintWriter out){

        this.otherUser = otherUser;
        this.myUsername = myUsername;
        this.out = out;

        setTitle("Private Chat - "+otherUser);
        setSize(420,500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(20,18,36));

        chatPanel=new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(20,18,36));

        scrollPane=new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        add(scrollPane,BorderLayout.CENTER);

        JPanel bottom=new JPanel(new BorderLayout());
        bottom.setBackground(new Color(32,30,58));

        messageField=new JTextField();
        messageField.setBackground(new Color(52,48,90));
        messageField.setForeground(Color.WHITE);

        JButton sendBtn=new JButton("Send");
        JButton clearBtn=new JButton("Clear");   // ⭐ PRIVATE CLEAR

        JPanel btnPanel=new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(sendBtn);
        btnPanel.add(clearBtn);

        bottom.add(messageField,BorderLayout.CENTER);
        bottom.add(btnPanel,BorderLayout.EAST);

        add(bottom,BorderLayout.SOUTH);

        sendBtn.addActionListener(e->sendMessage());

        clearBtn.addActionListener(e->{
            chatPanel.removeAll();
            chatPanel.revalidate();
            chatPanel.repaint();
        });

        messageField.addActionListener(e->sendMessage());

        setVisible(true);
    }

    void sendMessage(){

        String msg=messageField.getText().trim();
        if(!msg.isEmpty()){

            out.println("@"+otherUser+":"+myUsername+": "+msg);
            addMessage(myUsername+": "+msg);

            messageField.setText("");
        }
    }

    public void addMessage(String full){

        boolean isMe=full.startsWith(myUsername+":");
        String text=full.substring(full.indexOf(":")+1);
        String time=new SimpleDateFormat("HH:mm").format(new Date());

        MessageBubble bubble=new MessageBubble(text,time,isMe);

        JPanel wrap=new JPanel(new FlowLayout(isMe?FlowLayout.RIGHT:FlowLayout.LEFT));
        wrap.setOpaque(false);
        wrap.add(bubble);

        chatPanel.add(Box.createVerticalStrut(8));
        chatPanel.add(wrap);
        chatPanel.revalidate();

        JScrollBar bar=scrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }
}