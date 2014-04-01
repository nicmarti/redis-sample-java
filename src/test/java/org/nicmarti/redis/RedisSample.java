package org.nicmarti.redis;

import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashSet;

import static org.fest.assertions.api.Assertions.*;

/**
 * Very simple JUnit+Fest Assert class to demonstrate some of the Redis core syntax.
 */
public class RedisSample {

    public static final String hostname="localhost";
    public static final int port=6363;
    public static final int redisDB=2;

    @BeforeClass
    public static void cleanupDB(){
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);
        // !!! THIS WILL FLUSH COMPLETELY THE SELECTED DB
        if(redisDB!=0) {
            jedis.flushDB();
        }
    }

    @Test
    public void ensureThatWeCanConnectToRedis() {
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);
        assertThat(jedis.ping()).isEqualToIgnoringCase("PONG");
    }

    @Test
    public void setAndGetSimpleValue(){
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);

        assertThat(jedis.get("myKey")).isNull();

        jedis.set("myKey","A super value");

        assertThat(jedis.get("myKey")).isNotEmpty();
        assertThat(jedis.get("myKey")).isEqualTo("A super value");

        assertThat(jedis.get("anotherKey")).isNull();

        jedis.setex("myKeyThatWillExpire",5,"this value expires in 5 seconds");
        assertThat(jedis.get("myKeyThatWillExpire")).isNotNull();
    }

    @Test
    public void listSample(){
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);

        // http://redis.io/commands#list
        assertThat(jedis.llen("myList")).isEqualTo(0L);

        jedis.lpush("myList","Antonio");
        jedis.lpush("myList","José");
        jedis.lpush("myList","Nicolas");
        jedis.lpush("myList","Zouheir");
        jedis.lpush("myList","Nicolas");

        assertThat(jedis.llen("myList")).isEqualTo(5L);


        String lastInserted = jedis.lpop("myList");
        assertThat(lastInserted).isEqualTo("Nicolas");

        assertThat(jedis.llen("myList")).isEqualTo(4L);

        assertThat(jedis.lindex("myList",2 )).isEqualTo("José");

        // Cut list, keep only the last inserted element (here, Zouheir)
        jedis.ltrim("myList",0,0);

        assertThat(jedis.llen("myList")).isEqualTo(1L);

        String lastInserted2 = jedis.lpop("myList");
        assertThat(lastInserted2).isEqualTo("Zouheir");
    }

    @Test
    public void setSample(){
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);

        assertThat(jedis.smembers("speakers")).isEmpty();

        jedis.sadd("speakers", "Nic");
        jedis.sadd("speakers","Bob");
        jedis.sadd("speakers","Bob"); // no duplicate... size=2

        assertThat(jedis.scard("speakers")).isEqualTo(2);

        jedis.sadd("speakers","Tom");
        jedis.sadd("speakers","Mike");
        jedis.sadd("speakers","John");
        jedis.sadd("speakers","Christophe");


        // Create a 2nd set
        jedis.sadd("speakers:french", "Nic");
        jedis.sadd("speakers:french", "Christophe");

        // Let's create a 3rd set for non-french speaker
        // We do a DIFF between speakers and speakers:french and we store to a new REDIS Set
        jedis.sdiffstore("speakers:not_french", "speakers", "speakers:french");

        assertThat(jedis.scard("speakers:not_french")).isEqualTo(4);

        HashSet<String> testSpeakersUK = new HashSet<String>();
        testSpeakersUK.add("Bob");
        testSpeakersUK.add("Tom");
        testSpeakersUK.add("Mike");
        testSpeakersUK.add("John");

        assertThat(jedis.smembers("speakers:not_french")).isEqualTo(testSpeakersUK);


        assertThat(jedis.sismember("speakers:not_french","Nic")).isFalse();
        assertThat(jedis.sismember("speakers","Nic")).isTrue();
    }

    @Test
    public void sortedSetSample(){
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);

        assertThat(jedis.exists("talks:proposed:nicolas_on_redis")).isFalse();

        // David gives a 2 for proposal "nicolas_on_redis"
        jedis.zadd("talks:proposed:nicolas_on_redis",2,"David");
        // Antonio gives a 6 for proposal "nicolas_on_redis"
        jedis.zadd("talks:proposed:nicolas_on_redis",6,"Antonio");
        // José gives a 3 for proposal "nicolas_on_redis"
        jedis.zadd("talks:proposed:nicolas_on_redis",3,"José");

        // Now
        // rank #1 -> David with score=2
        // rank #2 -> José with score=3
        // rank #3 -> Antonio with a score=6

        // But David gives a 7 after all
        jedis.zadd("talks:proposed:nicolas_on_redis",7,"David");

        // rank #0 -> José with score=3
        // rank #1 -> Antonio with a score=6
        // rank #2 -> David with score=7

        // Let's check that we have 3 voters
        assertThat(jedis.zcard("talks:proposed:nicolas_on_redis")).isEqualTo(3);

        // Check that David score is a 7
        assertThat(jedis.zscore("talks:proposed:nicolas_on_redis", "David")).isEqualTo(7);

        // Who has given the worst score ?
        HashSet<String> expectedSet = new HashSet<String>();
        expectedSet.add("José");
        assertThat(jedis.zrange("talks:proposed:nicolas_on_redis",0,0)).isEqualTo(expectedSet);

        // Who has given the best score ?
        HashSet<String> expectedBestVote = new HashSet<String>();
        expectedBestVote.add("David");
        // We use a REV RANGE Here (Reverse Range)
        assertThat(jedis.zrevrange("talks:proposed:nicolas_on_redis",0,0)).isEqualTo(expectedBestVote);

        // José decides to add six points to its score
        jedis.zincrby("talks:proposed:nicolas_on_redis",6,"José");

        // Now José's score should be 9, and thus the rank should be 2

        // rank #0 -> Antonio with a score=6
        // rank #1 -> David with a score=7
        // rank #2 -> José with a score=9
        assertThat(jedis.zscore("talks:proposed:nicolas_on_redis", "José")).isEqualTo(9);
        assertThat(jedis.zrank("talks:proposed:nicolas_on_redis","José")).isEqualTo(2);
    }

    @Test
    public void hashSample(){
        Jedis jedis=new Jedis(hostname,port);
        jedis.select(redisDB);

        assertThat(jedis.exists("speakers:nicolas")).isFalse();


    }


}