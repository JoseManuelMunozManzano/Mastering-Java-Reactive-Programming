package com.jmunoz.sec11.client;

public class ClientError extends RuntimeException {

    public ClientError() {
        super("Bad Request");
    }
}
