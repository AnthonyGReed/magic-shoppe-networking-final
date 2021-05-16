package main.java.webserver;

import com.google.gson.Gson;
import main.java.config.NetworkConstants;
import main.java.models.*;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

/**
 * This class has a lot of the logic that handles the incoming HTTP requests and determines what needs to be done.
 * Ultimately, this is not a very secure API as it will spit data to anyone; however, the only input a user can do
 * is submit a string ID that we query the database with. I've taken some precautions to sanitize those inputs but I
 * bet someone could still figure out a way to do some damage. Otherwise, there is a lot of logic inside the process
 * request method so I will be doing line comments to point people in the right direction.
 * @author areed
 */
final class API implements Runnable, NetworkConstants {
    final static String CRLF = "\r\n";
    final static String FILE_BASE = "src/main/java/webserver/pages/frontend/build";
    Socket socket;

    public API(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This is the primary method of the API and handles routing incoming HTTP requests.
     */
    private void processRequest() {
        //We are using a try with resources to make sure if things go awry these resources get closed appropriately.
        try (
                InputStream is = socket.getInputStream();
                DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is))
        ) {
            //We grab the next line from the reader and turn it into a HttpRequest object for easier parsing.
            String requestLine = br.readLine();
            boolean fileExists = true;
            HttpRequest httpRequest = new HttpRequest(requestLine);
            //We strip away the leading "/" and split the path on all subsequent "/" giving us a distinction between
            //the parts of the URL that were sent.
            String[] splitPath = httpRequest.getPath().replaceFirst("^/", "").split("/");
            String fileName = FILE_BASE + httpRequest.getPath();
            Gson gson = new Gson();
            //This was to correct for the situations here our replace first and split above returns an empty string.
            if (httpRequest.getPath().equals("/")) {
                fileName = FILE_BASE + "/index.html";
            }
            FileInputStream file = null;
            //We attempt to find the file
            try {
                file = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                fileExists = false;
            }
            String statusLine;
            String contentTypeLine;
            String entityBody = null;
            /* If it was a page they were looking for, we return the page. This also applies to css and js files which
            * are needed for displaying the page properly.*/
            if (fileExists) {
                statusLine = "HTTP/1.1 200 OK" + CRLF;
                contentTypeLine = contentType(fileName);
                //Here we are going to check if the api was invoked and then look at whatever comes after the api
                //This means the url looks like "[host]:[port]/api/[command]"
            } else if (splitPath[0].equals("api") && httpRequest.getRequestType().equals("GET")) {
                switch (splitPath[1]) {
                    case "newShop":
                        statusLine = "HTTP/1.1 200 OK" + CRLF;
                        contentTypeLine = "Content-Type: application/json" + CRLF;
                        entityBody = gson.toJson(new Shop());
                        break;
                    case "shopID":
                        try {
                            statusLine = "HTTP/1.1 200 OK" + CRLF;
                            contentTypeLine = "Content-Type: application/json" + CRLF;
                            entityBody = gson.toJson(new Shop(httpRequest.getQueryParams().get("id").toUpperCase()));
                        } catch (SQLException|ArrayIndexOutOfBoundsException e) {
                            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                            contentTypeLine = "Content-Type: application/json" + CRLF;
                            entityBody = "File not found at ID";
                        }
                        break;
                    case "itemInfo":
                        try {
                            statusLine = "HTTP/1.1 200 OK" + CRLF;
                            contentTypeLine = "Content-Type: application/json" + CRLF;
                            Integer id = Integer.parseInt(httpRequest.getQueryParams().get("id"));
                            switch (httpRequest.getQueryParams().get("type")) {
                                case "potion":
                                    PotionInfo potion = new PotionInfo(id);
                                    entityBody = gson.toJson(potion);
                                    break;
                                case "scroll":
                                    ScrollInfo scroll = new ScrollInfo(id);
                                    entityBody = gson.toJson(scroll);
                                    break;
                                case "wonder":
                                    WonderInfo wonder = new WonderInfo(id);
                                    entityBody = gson.toJson(wonder);
                                    break;
                                default:
                                    entityBody = "Item not found";
                            }
                        } catch(SQLException e) {
                            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                            contentTypeLine = "Content-Type: application/json" + CRLF;
                            entityBody = "No data found at API endpoint " + splitPath[1];
                        }
                        break;
                    default:
                        statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                        contentTypeLine = "Content-Type: application/json" + CRLF;
                        entityBody = "No data found at API endpoint " + splitPath[1];
                }
            } else {
                statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                contentTypeLine = "Content-Type: text/html" + CRLF;
                entityBody = "<HTML><HEAD><TITLE>NOT FOUND</TITLE></HEAD><BODY><H1>404 - File Not Found - " +
                        "could not find file at " + fileName + " </H1></BODY></HTML>";
            }
            os.writeBytes(statusLine);
            os.writeBytes(contentTypeLine);
            os.writeBytes(CRLF);
            if (fileExists) {
                sendBytes(file, os);
                file.close();
            } else {
                os.writeBytes(entityBody);
            }
        } catch (IOException | SQLException e) {
            System.out.println("Illegal Operation Performed - IO Exception");
        }
    }

    /**
     * This function breaks down the file into bytes to send over the output stream
     * @param file the page requested by the client and found by the server
     * @param os the output stream to the client
     * @throws IOException  when trying to access the file but the file is null
     */
    private static void sendBytes(FileInputStream file, DataOutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = file.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    /**
     * This function looks at the file type of the requested resource and returns the appropriate string to setting
     * the content type to match the content requested.
     * @param fileName the name of the file requested by the client
     * @return String of the correct content type to return
     */
    private static String contentType(String fileName) {
        String result = "Content-Type: ";
        if(fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            result += "text/html";
        } else if(fileName.endsWith(".css")) {
            result += "text/css";
        } else if(fileName.endsWith(".js")) {
            result += "text/js";
        } else if(fileName.endsWith(".jpg")) {
            result += "image/jpeg";
        } else if(fileName.endsWith(".png")) {
            result += "image/png";
        }
        result += CRLF;
        return result;
    }
}