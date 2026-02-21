package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String username="";

    public ClientHandler(Socket socket) throws Exception{
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);
    }

    public void sendMessage(String msg){
        out.println(msg);
    }

    public void run(){
        try{

            // USERNAME RECEIVED FIRST
            username = in.readLine();
            Server.updateOnlineUsers();

            String msg;

            while((msg = in.readLine())!=null){

                // ==================================================
                // 🔐 PRIVATE CHAT ROUTING (NEW — SAFE ADDITION)
                // GUI sends:  @targetUser:username: message
                // ==================================================
                if(msg.startsWith("@")){

                    int firstColon = msg.indexOf(":");
                    if(firstColon==-1) continue;

                    String targetUser = msg.substring(1,firstColon);
                    String rest = msg.substring(firstColon+1);

                    for(ClientHandler client : Server.clients){

                        if(client.username.equalsIgnoreCase(targetUser)){

                            // send only to target
                            client.sendMessage("@"+targetUser+":"+rest);
                            break;
                        }
                    }

                    // ALSO show sender their own private message
                    sendMessage("@"+targetUser+":"+rest);

                }
                else{

                    // ==================================================
                    // 🌐 GROUP CHAT (OLD FUNCTIONALITY PRESERVED)
                    // ==================================================
                    Server.broadcast(username + ":" + msg);
                }
            }

        }catch(Exception e){
            System.out.println("Client Disconnected");
        }finally{
            Server.clients.remove(this);
            Server.updateOnlineUsers();
        }
    }
}