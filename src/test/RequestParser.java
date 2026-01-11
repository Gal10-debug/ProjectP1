package test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {

        String httpCommand;
        String uri;
        String[] uriSegment;
        Map<String, String> parameters = new HashMap<>();
        byte[] content = new byte[0];

        /* ==================== request line ==================== */
        String firstLine = reader.readLine();
        if (firstLine == null || firstLine.isEmpty()) {
            return null;
        }

        String[] parts = firstLine.split(" ");
        httpCommand = parts[0];
        uri = parts[1];

        /* ==================== headers ==================== */
        String line;
        int contentLength = 0;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();

                if (key.equalsIgnoreCase("Content-Length")) {
                    try {
                        contentLength = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                        contentLength = 0;
                    }
                }
            }
        }

        /* ==================== URI parsing ==================== */
        String path = uri;
        String query = null;

        int qIndex = uri.indexOf('?');
        if (qIndex >= 0) {
            path = uri.substring(0, qIndex);
            query = uri.substring(qIndex + 1);
        }

        List<String> segmentsList = new ArrayList<>();
        for (String seg : path.split("/")) {
            if (!seg.isEmpty()) {
                segmentsList.add(seg);
            }
        }
        uriSegment = segmentsList.toArray(new String[0]);

        if (query != null && !query.isEmpty()) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0) {
                    parameters.put(
                            pair.substring(0, eq),
                            pair.substring(eq + 1)
                    );
                }
            }
        }

        /*==================== Read extra params ====================*/
        if (reader.ready()) {
            String otherLine = reader.readLine();
            if (otherLine != null && !otherLine.isEmpty()) {
                int eqIndex = otherLine.indexOf('=');
                if (eqIndex > 0) {
                    parameters.put(
                            otherLine.substring(0, eqIndex),
                            otherLine.substring(eqIndex + 1)
                    );
                }
            }
        }

        /* ==================== body ==================== */
        ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();

        if (reader.ready()) {
            String contentLine = reader.readLine();
            if (contentLine != null && !contentLine.isEmpty()) {
                contentBuffer.write(contentLine.getBytes());
                contentBuffer.write('\n');
            }
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
