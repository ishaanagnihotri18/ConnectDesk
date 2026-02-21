package client;

import javax.swing.*;
import java.awt.*;

public class MessageBubble extends JPanel {

    String message;
    String time;
    boolean isMe;

    public MessageBubble(String message, String time, boolean isMe){
        this.message = message;
        this.time = time;
        this.isMe = isMe;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
    }

    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 28;
        int w = getWidth()-1;
        int h = getHeight()-1;

        g2.setColor(new Color(0,0,0,70));
        g2.fillRoundRect(3,3,w-2,h-2,arc,arc);

        GradientPaint gp;

        if(isMe){
            gp = new GradientPaint(0,0,new Color(167,139,250),
                    w,h,new Color(124,58,237));
        }else{
            gp = new GradientPaint(0,0,new Color(96,165,250),
                    w,h,new Color(37,99,235));
        }

        g2.setPaint(gp);
        g2.fillRoundRect(0,0,w-3,h-3,arc,arc);

        super.paintComponent(g2);
        g2.dispose();
    }

    protected void paintChildren(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();

        g2.setFont(new Font("Segoe UI",Font.PLAIN,14));
        g2.setColor(Color.WHITE);

        FontMetrics fm=g2.getFontMetrics();
        int paddingX = 14;
        int y=18;

        String[] lines = wrapText(message,fm,260);

        for(String line:lines){
            g2.drawString(line,paddingX,y);
            y+=fm.getHeight();
        }

        g2.setFont(new Font("Segoe UI Semibold",Font.BOLD,11));
        g2.setColor(new Color(140,255,170));

        String status = isMe ? "  ✓✓" : "";
        String finalTime = time + status;

        int timeWidth=g2.getFontMetrics().stringWidth(finalTime);
        g2.drawString(finalTime,getWidth()-timeWidth-12,getHeight()-8);

        g2.dispose();
    }

    public Dimension getPreferredSize(){
        FontMetrics fm=getFontMetrics(new Font("Segoe UI",Font.PLAIN,14));
        String[] lines = wrapText(message,fm,260);

        int height = 20 + (lines.length * fm.getHeight()) + 18;
        int width = Math.min(300, fm.stringWidth(message) + 40);

        return new Dimension(width,height);
    }

    private String[] wrapText(String text, FontMetrics fm, int maxWidth){

        java.util.List<String> list = new java.util.ArrayList<>();

        String[] words=text.split(" ");
        String line="";

        for(String w:words){
            String test=line.isEmpty()?w:line+" "+w;

            if(fm.stringWidth(test)>maxWidth){
                list.add(line);
                line=w;
            }else{
                line=test;
            }
        }

        if(!line.isEmpty()) list.add(line);

        return list.toArray(new String[0]);
    }
}