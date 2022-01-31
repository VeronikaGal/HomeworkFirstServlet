package com.galeeva.http.server;

import java.io.IOException;

public class HttpServerRunner {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(9000,100);
        httpServer.run();
    }
}
