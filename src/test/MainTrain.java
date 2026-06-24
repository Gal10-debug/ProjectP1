package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;



public class MainTrain { // RequestParser


    private static void testParseRequest() {
        // Test data
        String request = "GET /api/resource?id=123&name=test HTTP/1.1\n" +
                "Host: example.com\n" +
                "Content-Length: 5\n" +
                "\n" +
                "filename=\"hello_world.txt\"\n" +
                "\n" +
                "hello world!\n" +
                "\n";

        BufferedReader input = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        try {
            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(input);

            // Test HTTP command
            if (!requestInfo.getHttpCommand().equals("GET")) {
                System.out.println("HTTP command test failed (-5)");
            }

            // Test URI
            if (!requestInfo.getUri().equals("/api/resource?id=123&name=test")) {
                System.out.println("URI test failed (-5)");
            }

            // Test URI segments
            String[] expectedUriSegments = {"api", "resource"};
            if (!Arrays.equals(requestInfo.getUriSegments(), expectedUriSegments)) {
                System.out.println("URI segments test failed (-5)");
                for (String s : requestInfo.getUriSegments()) {
                    System.out.println(s);
                }
            }
            // Test parameters
            Map<String, String> expectedParams = new HashMap<>();
            expectedParams.put("id", "123");
            expectedParams.put("name", "test");
            expectedParams.put("filename", "\"hello_world.txt\"");
            if (!requestInfo.getParameters().equals(expectedParams)) {
                System.out.println("Parameters test failed (-5)");
            }

            // Test content
            byte[] expectedContent = "hello world!\n".getBytes();
            if (!Arrays.equals(requestInfo.getContent(), expectedContent)) {
                System.out.println("Content test failed (-5)");
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred during parsing: " + e.getMessage() + " (-5)");
        }
    }

//================================Start of testServer========================================//
    public static void testServer() throws Exception {

        MyHTTPServer server = new MyHTTPServer(8080, 1);

        /* ---------- register servlets ---------- */

        server.addServlet("GET", "/hello", new Servlet() {
            @Override
            public void handle(RequestParser.RequestInfo ri, OutputStream out) throws IOException {
                String body = "HELLO";
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: " + body.length() + "\r\n\r\n" +
                                body
                ).getBytes());
            }

            @Override
            public void close() {}
        });

        server.addServlet("GET", "/hello/world", new Servlet() {
            @Override
            public void handle(RequestParser.RequestInfo ri, OutputStream out) throws IOException {
                String body = "HELLO WORLD";
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: " + body.length() + "\r\n\r\n" +
                                body
                ).getBytes());
            }

            @Override
            public void close() {}
        });

        server.start();
        Thread.sleep(200);

        /* ---------- longest prefix ---------- */
        String r1 = send(
                "GET /hello/world HTTP/1.1\r\nHost: localhost\r\n\r\n"
        );
        assert r1.contains("HELLO WORLD");

        /* ---------- fallback ---------- */
        String r2 = send(
                "GET /hello HTTP/1.1\r\nHost: localhost\r\n\r\n"
        );
        assert r2.contains("HELLO");

        /* ---------- no servlet ---------- */
        String r3 = send(
                "GET /nope HTTP/1.1\r\nHost: localhost\r\n\r\n"
        );
        assert r3.isEmpty();

        server.close();
        System.out.println("testServer PASSED");
    }

    /* ---------- helper ---------- */
    private static String send(String req) throws Exception {
        try (Socket s = new Socket("localhost", 8080)) {
            OutputStream out = s.getOutputStream();
            InputStream in = s.getInputStream();

            out.write(req.getBytes());
            out.flush();
            s.shutdownOutput();

            byte[] buf = new byte[2048];
            int n = in.read(buf);
            return n == -1 ? "" : new String(buf, 0, n);
        }
    }

//    ----- This main is for the first project's part -----
//    public static void main(String[] args) {
//        testParseRequest(); // 40 points
//        try{
//            testServer(); // 60
//        }catch(Exception e){
//            System.out.println("your server threw an exception (-60)");
//        }
//        System.out.println("done");
//    }


    // Threads safe add on starts from here

    static String tn=null;

    public static class TestAgent1 implements Agent{

        public void reset() {
        }
        public void close() {
        }
        public String getName(){
            return getClass().getName();
        }

        @Override
        public void callback(String topic, Message msg) {
            tn=Thread.currentThread().getName();
        }

    }
    public static void main(String[] args) {
        TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();
        int tc=Thread.activeCount();
        ParallelAgent pa=new ParallelAgent(new TestAgent1(), 10);
        tm.getTopic("A").subscribe(pa);

        if (Thread.activeCount()!=tc+1){
            System.out.println("your ParallelAgent does not open a thread (-10)");
        }


        tm.getTopic("A").publish(new Message("a"));
        try { Thread.sleep(100);} catch (InterruptedException e) {}
        if(tn==null){
            System.out.println("your ParallelAgent didn't run the wrapped agent callback (-20)");
        }else{
            if(tn.equals(Thread.currentThread().getName())){
                System.out.println("the ParallelAgent does not run the wrapped agent in a different thread (-10)");
            }
            String last=tn;
            tm.getTopic("A").publish(new Message("a"));
            try { Thread.sleep(100);} catch (InterruptedException e) {}
            if(!last.equals(tn))
                System.out.println("all messages should be processed in the same thread of ParallelAgent (-10)");
        }

        pa.close();


        System.out.println("done");
    }

}
