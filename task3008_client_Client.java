package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress(){
        ConsoleHelper.writeMessage("Enter server address:");
return ConsoleHelper.readString();
    }

    protected int getServerPort(){
        ConsoleHelper.writeMessage("Enter server port:");
        return ConsoleHelper.readInt();
    }

    protected String getUserName(){
        ConsoleHelper.writeMessage("Enter your name");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    protected SocketThread getSocketThread(){
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try{connection.send(new Message(MessageType.TEXT,text));}
        catch (IOException e){clientConnected=false;}
    }

    public class SocketThread extends Thread{
        public void run() {
            String s = getServerAddress();
            int n = getServerPort();
            try {
                Socket socket = new Socket(s,n);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();

            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }

        }

        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("участник с именем "+userName+" присоединился к чату");
        }

        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("участник с именем "+userName+" покинул чат");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected=clientConnected;
            synchronized (Client.this){
            Client.this.notify();}
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException{
            while(true){
                Message receive = connection.receive();
            if (receive.getType()==MessageType.NAME_REQUEST){
           connection.send(new Message(MessageType.USER_NAME, getUserName()));}
            else if (receive.getType()==MessageType.NAME_ACCEPTED){
                notifyConnectionStatusChanged(true);
            break;}
            else {throw new IOException("Unexpected MessageType");}
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException{

            while(true){
                Message receive = connection.receive();
                if (receive.getType()==MessageType.TEXT){
                    processIncomingMessage(receive.getData());
                }
                else if (receive.getType()==MessageType.USER_ADDED){
                    informAboutAddingNewUser(receive.getData());
                }
                else if(receive.getType()==MessageType.USER_REMOVED){
                    informAboutDeletingNewUser(receive.getData());
                }
                else{
                    throw new IOException("Unexpected MessageType");}
            }
        }
    }

    public void run() throws InterruptedException {
        SocketThread socketThread =getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        synchronized (this){
        wait();}
        while (clientConnected){
            String a=ConsoleHelper.readString();
            if (a.equals("exit")){
                break;
            }
            if (shouldSendTextFromConsole()){
                sendTextMessage(a);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        client.run();
    }
}
