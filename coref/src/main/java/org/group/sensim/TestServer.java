package org.group.sensim;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestServer {

    //TODO ostanuva logikata od MAIN da ga implementiram u proekto kako "client" na coref-server.
    //Cekam odobrenie od Rene, dali bi mozelo da implementiram coref. Ako da, togaj ke implemtiram odma.
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        //establish socket connection to server
        socket = new Socket(host.getHostName(), 9876);
        oos = new ObjectOutputStream(socket.getOutputStream());

        System.out.println("Sending request to Socket Server");
        oos.writeObject("He is 43 years old. Obama is the president of USA.  But now he is not a president anymore.");

        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());

        String message = (String) ois.readObject();
        System.out.println("Message: " + message);
        //close resources
        ois.close();
        oos.close();

    }
}
