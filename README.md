# docker-kafka-twitter4j
sending twitter stream across kafka in a docker environment


# setup
create app on https://apps.twitter.com, you will need to update to tokens and keys in java classes based on your own app

Import project in intellij \
To run, use 'docker compose deploy' run configuration \
This creates 4 containers with zookeper, kafka, consumer and producer \
Producer uses stream from twitter(twitter4j) 

If not using intellij, cd into Docker-dir and run 'docker-compose up'


