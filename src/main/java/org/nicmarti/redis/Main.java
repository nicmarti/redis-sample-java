package org.nicmarti.redis;

import org.apache.commons.cli.*;
import redis.clients.jedis.Jedis;

/**
 * Main sample.
 * Created by nicmarti on 31/03/2014.
 */
public class Main {

    public static void main(String... args) throws Exception{

        // create the parser
        CommandLineParser parser = new BasicParser();

        CommandLine line = parser.parse(createOptions(), args);


        String host=line.getOptionValue("hostname","localhost");
        String port=line.getOptionValue("port","6379");

        System.out.println("Trying to connect to "+host+":"+port);


        Jedis jedis = new Jedis(host,Integer.parseInt(port));
        String result = jedis.ping();
        System.out.println("Ping result: " + result);
        System.out.println("Done.");
    }

    private static Options createOptions() {
        Option host = OptionBuilder.withArgName("hostname")
                .hasArg()
                .withDescription("Redis IP or hostname")
                .create("host");

        Option port = OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription("Redis TCP Port")
                .create("port");
        Options commandLineOptions = new Options();

        commandLineOptions.addOption(host).addOption(port);
        return commandLineOptions;
    }
}
