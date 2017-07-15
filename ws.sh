#!/bin/sh

BASE=/opt/ws
LIB=$BASE/lib
ETC=$BASE/etc

PATH=$PATH:$ETC:$LIB
LIB_PATH=-Djava.library.path=/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib:$ETC:$LIB
JAVA=`which java`

ulimit -c unlimited

$JAVA -Xms1024m -Xmx1024m -verbose:gc -XX:+UseSerialGC -DbaseApplicationContext=/opt/ws/etc/baseApplicationContext.xml -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.StdErrLog -D{classref}.LEVEL=DEBUG -Djava.library.path=$LIB_PATH -Ddynamic.loader=$LIB -cp $ETC:$LIB/inodes-util-0.1.9.jar:$LIB/jetty-util-6.1.26.jar:$LIB/dcm-services-0.1.war com.inodes.util.DynamicLoader org.dcm.services.cli.CLI $@ > /opt/ws/log/server.log 2>> /opt/ws/log/server.log &
