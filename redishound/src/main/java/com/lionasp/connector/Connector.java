package com.lionasp.connector;

import com.lionasp.connector.exceptions.ConnectorException;
import com.lionasp.connector.value.ListValue;
import com.lionasp.connector.value.SetValue;
import com.lionasp.connector.value.StringValue;
import com.lionasp.connector.value.Value;
import redis.clients.jedis.Jedis;

import java.util.Set;

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

    public void set(String key, String value) throws ConnectorException {
        try {
            getConnection().set(key, value);
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            throw new ConnectorException(e);
        }
    }

    public Value getValue(String key) throws ConnectorException {
        String type;
        try {
             type = getConnection().type(key);
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            throw new ConnectorException(e);
        }
        switch (type) {
            case "string":
                return new StringValue(getConnection().get(key));
            case "set":
                return new SetValue(getConnection().smembers(key));
            case "list":
                Long listLength = getConnection().llen(key);
                return new ListValue(getConnection().lrange(key, 0, listLength));
            default:
                return null;
        }

    }

    public void del(String key) throws ConnectorException {
        try {
            getConnection().del(key);
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            throw new ConnectorException(e);
        }
    }

    public String ping() throws ConnectorException {
        try {
            return getConnection().ping();
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            throw new ConnectorException(e);
        }
    }

    public Set<String> keys() throws ConnectorException {
        try {
            return getConnection().keys("*");
        } catch (redis.clients.jedis.exceptions.JedisConnectionException | redis.clients.jedis.exceptions.JedisDataException e) {
            throw new ConnectorException(e);
        }
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
