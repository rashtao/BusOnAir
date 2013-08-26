echo "Starting a 15 minutes long test on plugin/directions rest api..."
siege -b -q -t15M -i -f directions.txt 1>>directions_results.txt 2>>directions_results.txt
echo "Test ended, see directions_results.txt for details."
