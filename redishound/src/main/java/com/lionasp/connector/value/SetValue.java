package com.lionasp.connector.value;

import java.util.Set;

public class SetValue implements Value {
    private final Set<String> value;

    public SetValue(Set<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.join(", ", value);
    }
}
