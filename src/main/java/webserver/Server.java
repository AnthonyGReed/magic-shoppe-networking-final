package main.java.webserver;

import main.java.config.NetworkConstants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * This is the most basic aspect, of the web server. It creates the socket and sets it to accept, creating a new
 * API connection thread whenever someone connects to the socket.
 *
 * @author areed
 */
public class Server implements NetworkConstants {

    /**
     * Main method
     * @param args None required to begin
     * @throws IOException  IOException possible when socket attempts to read webpage files.
     * @throws SQLException SQLException possible when socket attempts to get data from database that throws an error.
     */
    public static void main(String[] args) throws IOException, SQLException {
        ServerSocket serverSocket = new ServerSocket(NetworkConstants.PORT);
        System.out.println("Ready to listen...");

        while(true) {
            Socket connection = serverSocket.accept();
            API api = new API(connection);
            Thread thread = new Thread(api);
            thread.start();
        }
    }
}
