package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int PORT = 8081;

    public static void main(String[] args) {

        Server server = new Server();
        server.start();

    }

    /**
     * Starting the web server:
     * .Open Server Socket
     * .Wait for connection from client(browser)
     * .A request from the browser is going to arrive
     * .We'll handle it and close the client socket (It's related to the way the http protocol works.)
     */

    public void start() {

        try {

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Waiting for browser request...");

            while (!serverSocket.isClosed()) {

                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
                clientSocket.close();

            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * The server receives a request.
     * First we check what the request was.
     * Then we respond to it. If the request corresponds to a non existing resource, we throw a 404 status code.
     * If it's a simple request (/), we redirect to the index page.
     * Else, we redirect to the right page, showing the right resource.
     *
     * @param clientSocket, so we can access the socket stream
     * @throws IOException
     */

    private void handleRequest(Socket clientSocket) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        String line = in.readLine();
        String request = "";
        if(line != null){

            request = line.split(" ")[1];
        }

        System.out.println("GET " + request);

        if (request.equals("/")) {
            request = "/index.html";
        }

        File file = new File("www" + request);

        if (!file.exists() || !file.isFile()) {
            file = new File("www/404.html");
        }

        out.writeBytes(generateHeader(file));
        sendFile(file, out);

    }


    /**
     * Reads the bytes from the input stream, and stores it in a byte[].
     * Sends the information out through the DataOutputStream
     *
     * @param file (the file to send)
     * @param out  (the output stream we're using)
     * @throws IOException
     */
    private void sendFile(File file, DataOutputStream out) throws IOException {

        byte[] byteArray = new byte[(int) file.length()];

        InputStream fInputStream = new FileInputStream(file);
        fInputStream.read(byteArray);

        out.write(byteArray);

    }

    /**
     * Generates the http response header
     * This header contains information about the response, like its location, p.e.
     * The header is generated resorting to
     * 1. the status code
     * 2. the MIME type
     * 3. the resource length
     *
     * @param file (the file to send)
     * @return header String
     */
    private String generateHeader(File file) {

        String fileName = file.getName();
        String status = "200 Document Follows";

        if (fileName.equals("404.html")) {
            status = "404 Not Found";
        }

        return "HTTP/1.0 " + status + "\r\n" +
                "Content-Type: " + getMimeType(fileName) + "; charset=UTF-8\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n";

    }

    /**
     * Get the MIME type from the filename (extension).
     * This tells your browser what kind of resource we're sending. Why is this important?
     * Because it helps the browser open the file, using the right extension or plugin.
     *
     * @param fileName (the name of the file we're trying to serve)
     * @return String MIME type
     */

    private String getMimeType(String fileName) {
        String mimeType = "";

        switch (fileName.split("\\.")[1]) {
            case "html":
                mimeType = "text/html";
                break;

            case "jpg":
                mimeType = "image/jpeg";
                break;

            case "ico":
                mimeType = "image/x-icon";
                break;

            case "mp4":
                mimeType = "video/mp4";
                break;

            case "png":
                mimeType = "image/png";
                break;
        }

        return mimeType;
    }
}
