package main.java.webserver;

import main.java.config.NetworkConstants;

import java.util.HashMap;

public class HttpRequest implements NetworkConstants {
    String requestType;
    String URI;
    String dataType;
    String path;
    HashMap<String, String> queryParams;

    /**
     * This object stores the incoming request and breaks it up so we can use the pieces.
     * @param httpRequest incoming request from client
     */
    public HttpRequest(String httpRequest) {
        String[] requestSegments = httpRequest.split(" ");
        this.requestType = requestSegments[0];
        this.URI = "https://" + NetworkConstants.HOST + requestSegments[1];
        this.dataType = requestSegments[2];
        String[] splitPath = requestSegments[1].split("[?]");
        this.path = splitPath[0];
        HashMap<String, String> parameters = new HashMap<>();
        if(splitPath.length > 1) {
            String[] attributes = splitPath[1].split("&");
            for(String attribute : attributes) {
                String[] kvp = attribute.split("=");
                parameters.put(kvp[0], kvp[1]);
            }
        }
        this.queryParams = parameters;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HashMap<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(HashMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
