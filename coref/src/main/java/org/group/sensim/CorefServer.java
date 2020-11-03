package org.group.sensim;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class CorefServer {
    static boolean serverShutDown = false;
    private static ServerSocket server;
    private static int port = 9876;

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        connectToCorefServer();
    }

    public static void connectToCorefServer() throws IOException, ClassNotFoundException, InterruptedException {
        BufferedReader br;
        PrintWriter outputWriter;
        CorefResolutor corefResolutor = new CorefResolutor();

        server = new ServerSocket(port);
        String outputMessageCoref = "";
        while (true) {
            Thread.sleep(200);
            System.out.println("Waiting for the client request");
            Socket socket = server.accept();

            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            System.out.println("Message Received: " + message);
            outputMessageCoref = corefResolutor.getCoref(message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(outputMessageCoref);


            ois.close();
            oos.close();
            socket.close();

            if (message.equalsIgnoreCase("exit") || serverShutDown ) break;
        }

        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();

    }

    public void stopCorefServer() {
        serverShutDown = true;
    }

}