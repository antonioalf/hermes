#!/bin/bash -xe

if [ $SEED = "true" ]; then
    if [ ! -z $SEED_IP ]; then
        SEED_IP_ADDRESS=$SEED_IP
    else
        HOSTNAME="$(hostname)"
        SEED_IP_ADDRESS="$(dig +short $HOSTNAME)"
    fi
    java -jar -Dkhermes.ws=true -Dakka.remote.netty.tcp.port=$PORT0 -Dakka.remote.netty.tcp.hostname=$SEED_IP_ADDRESS -Dakka.cluster.seed-nodes.0=akka.tcp://khermes@$SEED_IP_ADDRESS:$PORT0 -Dzookeeper.connection=master.mesos:2181 /khermes.jar
else
    java -jar -Dkhermes.client=true -Dkhermes.ws=false -Dakka.remote.netty.tcp.port=$PORT0 -Dakka.cluster.seed-nodes.0=akka.tcp://khermes@$SEED_IP:$SEED_PORT -Dzookeeper.connection=master.mesos:2181 /khermes.jar
fi

tail -F /khermes.log
