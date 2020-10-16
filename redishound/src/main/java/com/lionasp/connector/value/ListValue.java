package com.lionasp.connector.value;

import java.util.List;

public class ListValue implements Value {
    private final List<String> value;

    public ListValue(List<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.join(", ", value);
    }
}
