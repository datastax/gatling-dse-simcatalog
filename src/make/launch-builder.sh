#!/bin/sh

BUILD_TO=target/gatling-dse-sims

cat src/make/gatling-dse-launcher.sh target/scala-2.12/gatling-dse-simcatalog-*.jar > ${BUILD_TO} && chmod 755 ${BUILD_TO}