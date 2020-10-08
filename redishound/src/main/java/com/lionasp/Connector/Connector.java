package com.lionasp.Connector;

import redis.clients.jedis.Jedis;

public class Connector {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6378);
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
    }
}
