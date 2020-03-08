package com.javarush.task.task30.task3008;

import com.javarush.task.task30.task3008.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread {
        Socket socket;

        public void run(){
           ConsoleHelper.writeMessage(socket.getRemoteSocketAddress().toString());
            try (Connection connection = new Connection(socket)){
                String name=serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED,name));
                notifyUsers(connection,name);
                serverMainLoop(connection,name);
                connectionMap.remove(name);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED,name));
                ConsoleHelper.writeMessage("соединение с удаленным адресом закрыто");
            } catch (IOException | ClassNotFoundException e) {
               ConsoleHelper.writeMessage("произошла ошибка при обмене данными с удаленным адресом");
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            connection.send(new Message(MessageType.NAME_REQUEST,"Введите имя клиента"));
            Message receive = connection.receive();
            if (receive.getType().equals(MessageType.USER_NAME)&&!receive.getData().isEmpty()&&!connectionMap.containsKey(receive.getData())){
                connectionMap.putIfAbsent(receive.getData(),connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED, "имя клиента принято"));
                return receive.getData();
            }
            else {return serverHandshake(connection);}
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for (String name: connectionMap.keySet()){
                if (!name.equals(userName)){
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }

       private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true) {
               Message receive= connection.receive();
                if (receive.getType()==MessageType.TEXT) {
                    String s=userName + ": " + receive.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT,s));
                } else {
                    ConsoleHelper.writeMessage("Нe правильный формат сообщения отличный от \"TEXT\"!");
                }
            }
        }

        public Handler(Socket socket) {
            this.socket = socket;
        }
    }
    public static void main(String[] args){
        try (
            ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt());)
        {System.out.println("Сервер запущен");
        while (true){
            Socket socket = serverSocket.accept();
            Handler handler = new Handler(socket);
            handler.start();
        }}catch (IOException e){e.printStackTrace();}


    }

    public static void sendBroadcastMessage(Message message){
        try{
            for (String name:connectionMap.keySet()){
                connectionMap.get(name).send(message);
            }
        }catch (IOException e){System.out.println("Сообщение не отправилось");}
    }
}