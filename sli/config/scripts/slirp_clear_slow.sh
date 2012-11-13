#!/bin/bash
#
# This script clears the slow query logs from SLIRP
#
#set -x

if [ $# -ne 0 ];
then
  echo "Usage: scripts/slirp_clear_slow (run from the config/ directory)"
  echo "This script clears the slow query logs"
  exit 1
fi

NAME=$1

SLOW_QUERY=100

######################
#   Primary Config   #
######################

PRIMARIES="slirpmongo03.slidev.org slirpmongo05.slidev.org slirpmongo09.slidev.org slirpmongo11.slidev.org"
ISDB="slirpmongo99.slidev.org"

#
# Get slow query logs
#
echo "Slow query logs, is..."
mongo $ISDB/is << END
  db.setProfilingLevel(0);
  db.system.profile.drop();
  db.setProfilingLevel(1,$SLOW_QUERY);
END
echo "Slow query logs, staging..."
mongo $ISDB/ingestion_batch_job << END
  db.setProfilingLevel(0);
  db.system.profile.drop();
  db.setProfilingLevel(1,$SLOW_QUERY);
END

for i in $PRIMARIES;
do
  echo "Slow query logs, ${i}/sli..."
  mongo $i/sli << END
    db.setProfilingLevel(0);
    db.system.profile.drop();
    db.setProfilingLevel(1,$SLOW_QUERY);
END
  echo "Slow query logs, $i/Hyrule..."
  mongo $i/d36f43474916ad310100c9711f21b65bd8231cc6 << END
    db.setProfilingLevel(0);
    db.system.profile.drop();
    db.setProfilingLevel(1,$SLOW_QUERY);
END
  echo "Slow query logs, $i/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a..."
  mongo $i/02f7abaa9764db2fa3c1ad852247cd4ff06b2c0a << END
    db.setProfilingLevel(0);
    db.system.profile.drop();
    db.setProfilingLevel(1,$SLOW_QUERY);
END
  echo "Slow query logs, $i/ff501cb38db19529bc3eb7fd5759f3844626fdf6..."
  mongo $i/ff501cb38db19529bc3eb7fd5759f3844626fdf6 << END
    db.setProfilingLevel(0);
    db.system.profile.drop();
    db.setProfilingLevel(1,$SLOW_QUERY);
END
done
