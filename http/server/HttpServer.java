package com.galeeva.http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final int port;
    private final ExecutorService pool;
    private boolean stopped;

    public HttpServer(int port, int poolSize) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    public void run() throws IOException {
        try {
            var server = new ServerSocket(port);
            while (!stopped) {
                var socket = server.accept();
                System.out.println("Socked accepted");
                pool.submit(() -> {
                    try {
                        processSocket(socket);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void processSocket(Socket socket) throws InterruptedException {
        try (socket;
             InputStream inputStream = new DataInputStream(socket.getInputStream());
             var outputStream = new DataOutputStream(socket.getOutputStream())) {
            // step 1 handle request
            System.out.println("Request: " + new String(inputStream.readNBytes(400)));
            // step 2 handle response
            Thread.sleep(10000);
            byte[] body = Files.readAllBytes(Path.of("resources", "example.html"));
            var headers = """
                    HTTP/1.1 200 OK
                    content-type: text/html
                    content-length: %s
                    """.formatted(body.length).getBytes(StandardCharsets.UTF_8);
            outputStream.write(headers);
            outputStream.write(System.lineSeparator().getBytes());
            outputStream.write(body);
        } catch (
                IOException e) {
            //TODO: 2/27/21 lod error message
            e.printStackTrace();
        }
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
