package cn.yihai.basic.core.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class JedisDataSourceSupport extends JedisDataSource {

	private JedisPool jedisPool;
	
	private ShardedJedisPool shardedJedisPool;

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}

	@Override
	public Jedis getJedis() {
		return this.jedisPool.getResource();
	}

	@Override
	public ShardedJedis getShardedJedis() {
		return this.shardedJedisPool.getResource();
	}

	@Override
	public void returnResource(JedisCommands jedis) {
		if (jedis == null) {
			return;
		}
		if ((jedis instanceof Jedis)) {
			((Jedis) jedis).close();
		} else if ((jedis instanceof ShardedJedis)) {
			((ShardedJedis) jedis).close();
		}
	}

}
