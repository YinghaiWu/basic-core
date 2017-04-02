package cn.yihai.basic.core.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ShardedJedis;

public class JedisTemplate {

	private JedisDataSource jedisDataSource;

	public void setJedisDataSource(JedisDataSource jedisDataSource) {
		this.jedisDataSource = jedisDataSource;
	}

	public Jedis getJedis() {
		return this.jedisDataSource.getJedis();
	}

	public ShardedJedis getShardedJedis() {
		return this.jedisDataSource.getShardedJedis();
	}

	public void returnResource(JedisCommands jedis) {
		this.jedisDataSource.returnResource(jedis);
	}

}
