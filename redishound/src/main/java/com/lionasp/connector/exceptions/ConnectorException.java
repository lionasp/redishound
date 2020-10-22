package com.lionasp.connector.exceptions;

public class ConnectorException extends Throwable {
    public ConnectorException(Throwable cause) {
        super(cause);
    }

    public ConnectorException(String message) {
        super(message);
    }
}
