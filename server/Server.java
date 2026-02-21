package server;

import java.net.*;
import java.util.*;

public class Server {

    public static Vector<ClientHandler> clients = new Vector<>();

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("ConnectDesk Server Started...");

        while(true){
            Socket socket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(socket);
            clients.add(handler);
            new Thread(handler).start();
        }
    }

    public static void broadcast(String msg){
        for(ClientHandler c : clients){
            c.sendMessage(msg);
        }
    }

    public static void updateOnlineUsers(){
        StringBuilder users = new StringBuilder("ONLINE:");
        for(ClientHandler c : clients){
            users.append(c.username).append(",");
        }
        broadcast(users.toString());
    }
}