package test;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

public class MyHTTPServer extends Thread implements HTTPServer {
    final private int port;
    private volatile boolean running = true;
    private final int nThreads;
    ServerSocket serverSocket;

    Map<String, Servlet> getServlets = new HashMap<String, Servlet>();
    Map<String, Servlet> postServlets = new HashMap<String, Servlet>();
    Map<String, Servlet> deleteServlets = new HashMap<String, Servlet>();

    //private method
    //This method finds the longest prefix of paths
    private Servlet findServlet(Map<String, Servlet> map, String uri) {
        Servlet best = null;
        int longest = -1;

        for (String prefix : map.keySet()) {
            if (uri.startsWith(prefix)) {
                if (prefix.length() > longest) {
                    longest = prefix.length();
                    best = map.get(prefix);
                }
            }
        }

        return best;
    }


    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.nThreads = nThreads;
    }

    public void addServlet(String httpCommanmd, String uri, Servlet s) {
        String httpCommand = httpCommanmd.toUpperCase();
        switch (httpCommand) {
            case "GET":
                getServlets.put(uri, s);
                break;
            case "POST":
                postServlets.put(uri, s);
                break;
            case "DELETE":
                deleteServlets.put(uri, s);
                break;
            default:
                break;
        }
    }

    public void removeServlet(String httpCommanmd, String uri) {
        String httpCommand = httpCommanmd.toUpperCase();
        switch (httpCommand) {
            case "GET":
                getServlets.remove(uri);
                break;
            case "POST":
                postServlets.remove(uri);
                break;
            case "DELETE":
                deleteServlets.remove(uri);
                break;
            default:
                break;
        }
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();

                InputStream inFromClient = clientSocket.getInputStream();
                OutputStream outToClient = clientSocket.getOutputStream();

                BufferedReader inFromClientReader = new BufferedReader(new InputStreamReader(inFromClient));
                RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(inFromClientReader);


                Servlet servlet = null;
                if(requestInfo!=null) {
                    switch (requestInfo.getHttpCommand()) {
                        case "GET":
                            servlet = findServlet(getServlets, requestInfo.getUri());
                            break;
                        case "POST":
                            servlet = findServlet(postServlets, requestInfo.getUri());
                            break;

                        case "DELETE":
                            servlet = findServlet(deleteServlets, requestInfo.getUri());
                            break;

                        default:
                            break;
                    }
                    if(servlet!=null) {
                        servlet.handle(requestInfo,outToClient);
                    }
                }
                outToClient.close();
                inFromClientReader.close();
                clientSocket.close();

            }catch (SocketTimeoutException e) {
            }
            catch (IOException e) {
                break;
            }
        }
    }


    public void close(){
        running = false;

        for (Servlet s : getServlets.values()) {
            try { s.close(); } catch (IOException ignored) {}
        }
        for (Servlet s : postServlets.values()) {
            try { s.close(); } catch (IOException ignored) {}
        }
        for (Servlet s : deleteServlets.values()) {
            try { s.close(); } catch (IOException ignored) {}
        }

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
        }
    }

}
