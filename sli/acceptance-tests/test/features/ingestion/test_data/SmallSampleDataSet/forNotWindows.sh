perl -n -e '@x=split ",";  next if($#x != 3);$m=`md5 -q $x[2]`; $x[3]=(split(" ", $m))[0]; print join (",", @x), "\n"' MainControlFile.ctl >MainControlFile.ctl.updated
cp MainControlFile.ctl MainControlFile.ctl.bak
cp MainControlFile.ctl.updated MainControlFile.ctl

#if [bundle exec rake ingestionPreloading] fails, run this script in Small and Medium Data Set directories and then do [mvn clean package install].
