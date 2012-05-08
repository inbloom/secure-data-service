xterm -e mvn jetty:run -Ptest -Djetty.port=8081 &
xterm -e mvn jetty:run -Pdev &

echo "launched servers"
cd ../acceptance-tests

export dashboard_app_prefix_live_mode=":8080/dashboard"
export dashboard_app_prefix_test_mode=":8081/dashboard"
export dashboard_server_address="http://local.slidev.org"

sleep 10
bundle install
echo "running tests"
bundle exec rake dashboardTests
