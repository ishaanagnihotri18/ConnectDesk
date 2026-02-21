package client;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ClientGUI extends JFrame {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    JPanel chatPanel;
    JPanel onlinePanel;
    JTextField messageField;
    JScrollPane scrollPane;

    JLabel typingLabel;

    String username;

    Map<String, PrivateChatWindow> privateWindows = new HashMap<>();

    Timer typingAnim;
    int dotCount=0;

    public ClientGUI(String username){

        this.username = username;

        setTitle("ConnectDesk");
        setSize(920,680);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        getContentPane().setBackground(new Color(20,18,36));

        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0,50));
        header.setBackground(new Color(24,22,44));

        JLabel headerTitle = new JLabel("  ConnectDesk");
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(new Font("Segoe UI",Font.BOLD,16));
        header.add(headerTitle,BorderLayout.WEST);
        add(header,BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(20,18,36));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);

        JScrollBar bar = scrollPane.getVerticalScrollBar();
        bar.setUI(new BasicScrollBarUI(){
            protected void configureScrollBarColors(){
                this.thumbColor=new Color(140,120,220);
            }
        });

        onlinePanel = new JPanel();
        onlinePanel.setPreferredSize(new Dimension(220,600));
        onlinePanel.setBackground(new Color(28,26,52));
        onlinePanel.setLayout(new BoxLayout(onlinePanel,BoxLayout.Y_AXIS));
        onlinePanel.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));

        JLabel title = new JLabel("Online Users");
        title.setForeground(new Color(134,239,172));
        title.setFont(new Font("Segoe UI",Font.BOLD,14));
        onlinePanel.add(title);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                scrollPane,
                onlinePanel
        );
        splitPane.setDividerLocation(730);
        splitPane.setBorder(null);

        add(splitPane,BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(32,30,58));
        bottom.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        messageField = new JTextField();
        messageField.setBackground(new Color(52,48,90));
        messageField.setForeground(Color.WHITE);
        messageField.setCaretColor(Color.WHITE);

        JButton sendBtn = new JButton("Send");
        JButton clearBtn = new JButton("Clear");   // ⭐ GROUP CLEAR BUTTON

        typingLabel = new JLabel(" ");
        typingLabel.setForeground(new Color(200,200,240));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(sendBtn);
        btnPanel.add(clearBtn);

        bottom.add(typingLabel,BorderLayout.NORTH);
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

        messageField.addKeyListener(new java.awt.event.KeyAdapter(){
            public void keyTyped(java.awt.event.KeyEvent e){
                startTypingAnimation();
            }
        });

        connectToServer();

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    JLabel createAvatar(String name){

        JLabel avatar = new JLabel(name.substring(0,1).toUpperCase(),SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(30,30));
        avatar.setOpaque(true);

        Color[] colors={
                new Color(96,165,250),
                new Color(167,139,250),
                new Color(34,197,94),
                new Color(251,146,60)
        };

        avatar.setBackground(colors[Math.abs(name.hashCode())%colors.length]);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI",Font.BOLD,14));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(167,139,250),2,true));

        return avatar;
    }

    PrivateChatWindow openPrivateWindow(String user){

        if(privateWindows.containsKey(user))
            return privateWindows.get(user);

        PrivateChatWindow win = new PrivateChatWindow(user,username,out);
        privateWindows.put(user,win);
        return win;
    }

    void startTypingAnimation(){

        if(typingAnim!=null && typingAnim.isRunning()) return;

        typingAnim = new Timer(300,e->{
            dotCount=(dotCount+1)%4;
            typingLabel.setText(username+" typing "+ ".".repeat(dotCount));
        });
        typingAnim.start();

        new Timer(1200,e->{
            typingLabel.setText(" ");
            typingAnim.stop();
        }).start();
    }

    void connectToServer(){
        try{
            socket=new Socket("localhost",1234);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream(),true);

            out.println(username);

            new Thread(()->{
                try{
                    String msg;
                    while((msg=in.readLine())!=null){
                        if(msg.startsWith("ONLINE:")){
                            updateOnline(msg);
                        }else{
                            handleIncomingMessage(msg);
                        }
                    }
                }catch(Exception e){}
            }).start();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void sendMessage(){
        String msg=messageField.getText().trim();
        if(!msg.isEmpty()){
            out.println(msg);
            messageField.setText("");
            typingLabel.setText(" ");
        }
    }

    void handleIncomingMessage(String msg){

        if(msg.startsWith("@")){

            int idx = msg.indexOf(":");
            if(idx==-1) return;

            String target = msg.substring(1,idx);
            String realMsg = msg.substring(idx+1);

            if(target.equals(username)){
                String sender = realMsg.substring(0,realMsg.indexOf(":"));
                openPrivateWindow(sender).addMessage(realMsg);
            }
        }
        else{

            String sender = msg.substring(0,msg.indexOf(":"));
            String text = msg.substring(msg.indexOf(":")+1);

            addGroupMessage(sender,text);
        }
    }

    void addGroupMessage(String sender,String text){

        boolean isMe = sender.equals(username);
        String time=new SimpleDateFormat("HH:mm").format(new Date());

        MessageBubble bubble=new MessageBubble(text,time,isMe);

        JPanel wrap=new JPanel(new FlowLayout(isMe?FlowLayout.RIGHT:FlowLayout.LEFT,8,2));
        wrap.setOpaque(false);

        JLabel avatar=createAvatar(sender);

        if(isMe){
            wrap.add(bubble);
            wrap.add(avatar);
        }else{
            wrap.add(avatar);
            wrap.add(bubble);
        }

        chatPanel.add(Box.createVerticalStrut(8));
        chatPanel.add(wrap);
        chatPanel.revalidate();

        JScrollBar bar=scrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    void updateOnline(String data){

        onlinePanel.removeAll();

        JLabel title = new JLabel("Online Users");
        title.setForeground(new Color(134,239,172));
        title.setFont(new Font("Segoe UI",Font.BOLD,14));
        onlinePanel.add(title);

        String[] users=data.replace("ONLINE:","").split(",");

        for(String u:users){
            if(!u.isEmpty()){

                JPanel card=new JPanel(new BorderLayout());
                card.setMaximumSize(new Dimension(180,40));
                card.setBackground(new Color(40,38,70));
                card.setBorder(BorderFactory.createEmptyBorder(8,18,8,10));

                JLabel name=new JLabel(u);
                name.setForeground(new Color(134,239,172));
                name.setFont(new Font("Segoe UI",Font.BOLD,14));

                card.add(name,BorderLayout.CENTER);

                card.addMouseListener(new java.awt.event.MouseAdapter(){
                    public void mouseClicked(java.awt.event.MouseEvent e){
                        openPrivateWindow(u);
                    }
                });

                onlinePanel.add(card);
                onlinePanel.add(Box.createVerticalStrut(8));
            }
        }

        onlinePanel.revalidate();
        onlinePanel.repaint();
    }
}