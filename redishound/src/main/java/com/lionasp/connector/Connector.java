package com.lionasp.connector;

import redis.clients.jedis.Jedis;

public class Connector {
    private String host = "localhost";
    private int port = 6379;
    private int timeout = 10;
    private int dbNumber = 0;
    private Jedis connection;

    public Connector() {
    }

    public Connector(String host) {
        this.host = host;
    }

    public Connector(String host, int port) {
        this(host);
        this.port = port;
    }

    public Connector(String host, int port, int dbNumber) {
        this(host, port);
        this.dbNumber = dbNumber;
    }

    public void set(String key, String value) {
        getConnection().set(key, value);
    }

    public String get(String key) {
        return getConnection().get(key);
    }

    public String ping() {
        return getConnection().ping();
    }

    private Jedis getConnection() {
        if (connection == null) {
            System.out.println("Trying to connect to redis. Hostname: " + host + " Port: " + port + " DB number: " + dbNumber);
            connection = new Jedis(host, port, timeout);
            connection.select(dbNumber);
        }
        return connection;
    }

}
