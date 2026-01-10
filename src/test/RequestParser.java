package test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String httpCommand = null;
        String uri = null;
        String[] uriSegment = null;
        Map<String,String> parameters = new HashMap<>();
        byte[] content = null;

        String firstLine = reader.readLine();
        if (firstLine == null || firstLine.isEmpty()) {
            return null;
        }

        String[] parts = firstLine.split(" ");
        httpCommand = parts[0];
        uri = parts[1];

        //Handle the header of the request
        String line;
        int contentLength = -1;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }

            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                //Header parameters such as HOST and content length are not included in parameters map

                if (key.equalsIgnoreCase("Content-Length")) {
                    try {
                        contentLength = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        contentLength = -1;
                    }
                }
            }
        }

        //Handle uri segments and other values
        String path = uri;
        String query = null;

        int qIndex = uri.indexOf('?');
        if (qIndex >= 0) {
            path = uri.substring(0, qIndex);
            query = uri.substring(qIndex + 1);
        }

        String[] rawSegments = path.split("/");
        List<String> segmentsList = new ArrayList<>();

        for (String seg : rawSegments) {
            if (!seg.isEmpty()) {
                segmentsList.add(seg);
            }
        }

        uriSegment = segmentsList.toArray(new String[0]);

        //Parameters's key and value extracting into the hashmap from uri
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int eqIndex = pair.indexOf('=');
                if (eqIndex > 0 && eqIndex < pair.length() - 1) {
                    String key = pair.substring(0, eqIndex);
                    String value = pair.substring(eqIndex + 1);
                    parameters.put(key, value);
                }
            }
        }

        //Body content reading and more params such as filename
        String otherLine;
        while ((otherLine = reader.readLine()) != null) {
            if (otherLine.isEmpty()) {
                break;
            }

            int eqIndex = otherLine.indexOf('=');
            if (eqIndex > 0) {
                String key = otherLine.substring(0, eqIndex);
                String value = otherLine.substring(eqIndex + 1);
                parameters.put(key, value);
            }
        }


        ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();

        String contentLine;
        while ((contentLine = reader.readLine()) != null) {
            if (contentLine.isEmpty()) {
                break;
            }
            contentBuffer.write(contentLine.getBytes());
            contentBuffer.write('\n');
        }

        content = contentBuffer.toByteArray();


        return new RequestInfo(
                httpCommand,
                uri,
                uriSegment,
                parameters,
                content
        );
    }
	
	// RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
