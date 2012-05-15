cat pids | xargs kill
sleep 1
rm -rf data logs pids
