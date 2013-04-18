#!/bin/bash

export NEO4J_HOME=../neo4j

sudo $NEO4J_HOME/bin/neo4j stop
echo "Copying DB & Plugins to NEO4J_HOME"
sudo chmod 777 $NEO4J_HOME -R
#sudo rm $NEO4J_HOME/data/graph.db/* -R
sudo rm $NEO4J_HOME/plugins/* -R
#cp -a dbAq/* $NEO4J_HOME/data/graph.db/
cp -a ./target/plugins/* $NEO4J_HOME/plugins/
echo "org.neo4j.server.thirdparty_jaxrs_classes=boa.server=/plugin" >>  $NEO4J_HOME/conf/neo4j-server.properties
echo "cache_type=strong" >>  $NEO4J_HOME/conf/neo4j.properties
sudo chmod 777 $NEO4J_HOME -R
sudo $NEO4J_HOME/bin/neo4j start

