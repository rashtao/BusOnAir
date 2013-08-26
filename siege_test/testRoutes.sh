echo "Starting a 15 minutes long test on plugin/routesearch rest api..."
siege -b -q -t15M -i -f routes.txt 1>>routes_results.txt 2>>routes_results.txt
echo "Test ended, see routes_results.txt for details."