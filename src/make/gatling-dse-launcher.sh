#!/bin/sh
WHO_AM_I=`which "$0" 2>/dev/null`
[ $? -gt 0 -a -f "$0" ] && WHO_AM_I="./$0"

if [ -n "${JAVA_HOME}" ]; then
    JAVA="${JAVA_HOME}"/bin/java
else
    JAVA=java
fi

DEFAULT_JAVA_OPTS="-server"
DEFAULT_JAVA_OPTS="${DEFAULT_JAVA_OPTS} -Xms2G -Xmx2G"
DEFAULT_JAVA_OPTS="${DEFAULT_JAVA_OPTS} -XX:+UseG1GC -XX:MaxGCPauseMillis=30 -XX:G1HeapRegionSize=16m -XX:InitiatingHeapOccupancyPercent=75 -XX:+ParallelRefProcEnabled"
DEFAULT_JAVA_OPTS="${DEFAULT_JAVA_OPTS} -XX:+PerfDisableSharedMem -XX:+AggressiveOpts -XX:+OptimizeStringConcat"
DEFAULT_JAVA_OPTS="${DEFAULT_JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
DEFAULT_JAVA_OPTS="${DEFAULT_JAVA_OPTS} -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false"
DEFAULT_JAVA_OPTS="${DEFAULT_JAVA_OPTS} -XX:+UseTLAB -XX:+ResizeTLAB"

exec "$JAVA" ${DEFAULT_JAVA_OPTS} ${JAVA_OPTS} -jar ${WHO_AM_I} "$@"
exit 1