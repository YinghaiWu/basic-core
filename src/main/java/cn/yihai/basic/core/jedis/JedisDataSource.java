package cn.yihai.basic.core.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ShardedJedis;

public abstract class JedisDataSource {

	public abstract Jedis getJedis();

	public abstract ShardedJedis getShardedJedis();

	public abstract void returnResource(JedisCommands paramJedisCommands);

}
