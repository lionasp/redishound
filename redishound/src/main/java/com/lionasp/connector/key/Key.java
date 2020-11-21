package com.lionasp.connector.key;

public class Key {
    final private String name;
    private String type;

    public Key(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + (type != null ? " (" + type + ")" : "");
    }
}
