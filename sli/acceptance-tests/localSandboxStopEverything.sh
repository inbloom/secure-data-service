ps -el|grep java  |grep jetty |perl -n -e 'if(/\d+ (\d+) /){print "$1\n";`kill -9 $1`;}'
ps -el|grep search-indexer   |perl -n -e 'if(/\d+ (\d+) /){print "$1\n";`kill -9 $1`;}'
ps -el|grep rails   |perl -n -e 'if(/\d+ (\d+) /){print "$1\n";`kill -9 $1`;}'


ps -el|grep java
ps -el|grep search-indexer 
ps -el|grep rails 
