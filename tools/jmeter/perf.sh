#/bin/sh

runScript()
{
  # local variable x and y with passed args    
  local script=$1
  local threads=$2
  local loops=$3
  local warm=$4
  log=out.jtl
  rm $log
  jmeter -n -t $script -q slirp.properties -l $log -Jloops=$loops -Jthreads=$threads > /dev/null
  startTime=$(head -3 $log| tail -1  | sed -n 's/.*ts=\"\(.*\)".s.*/\1/p')
  endTime=$(tail -3 $log| head -1  | sed -n 's/.*ts=\"\(.*\)".s.*/\1/p')
  total=$(expr $endTime - $startTime)
  reqs=$(expr $threads \* $loops)
  tp=$(echo "scale=5;$reqs / $total * 1000 * 60" | bc)
  if [ "$warm" != "warm" ]; then
    echo "Threads=$threads $total ms with throughput of $tp req/min"
  fi
}

testScript() {
  local script=$1
  local loops=$2
  runScript $script 1 $loops warm
  runScript $script 1 $loops
  runScript $script 2 $loops
  runScript $script 4 $loops
  runScript $script 8 $loops
}

echo "Single student request"
testScript single-student.jmx 20

echo "List students"
testScript list-students.jmx 4

echo "List sections"
testScript list-sections.jmx 2

echo "List grades"
testScript list-grades.jmx 2

echo "List attendance"
testScript list-attendance.jmx 2

echo "Update attendance"
testScript update-attendance.jmx 2

echo "Update gradebooks"
testScript update-gradebooks.jmx 4
