import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;
import java.util.Scanner;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class MyTwitterStream {

    public static Producer<String, String> producer;
    /**
     * Main entry of this application.
     *
     * @param args arguments doesn't take effect with this example
     * @throws TwitterException when Twitter service or network is unavailable
     */
    public static void main(String[] args) throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("SbNm8xUQ4hkS9g3ySjOTtw2e4")
                .setOAuthConsumerSecret("eNlGwXQo6THs3tjKdVhUCAWEg5l2PKfgp6kvAiBown1tHDMjMm")
                .setOAuthAccessToken("397230451-nIYMwJzmte0b5fzBIDIv0jRvFQ3VTM8AdD8AMM7C")
                .setOAuthAccessTokenSecret("pAjWacj2FMMx6dEjuDqOfYKO6VU4TPkJVyTS4RvKzWFQJ");
        TwitterStreamFactory tsf = new TwitterStreamFactory(cb.build());
        TwitterStream twitterStream = tsf.getInstance();

        //kafka producer
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.99.100:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //public Producer<String, String> producer = null;
        try {
            System.out.println("Creating producer...");
            producer = new KafkaProducer<>(props);
            System.out.println("Producer created!");
        } catch (Exception e) {
            e.printStackTrace();

        }

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                try{
                    String msg = "@" + status.getUser().getScreenName() + " - " + status.getText();
                    producer.send(new ProducerRecord<String, String>("HelloKafkaTopic", msg));
                    System.out.println("Sent:" + msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        FilterQuery fq = new FilterQuery();

        String keywords[] = {"north korea"};

        fq.track(keywords);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }
}