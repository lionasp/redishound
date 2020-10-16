package com.lionasp.connector.value;

public class StringValue implements Value {
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
