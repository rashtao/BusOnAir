export BOA_BASE_URL=http://localhost:7777/plugin

#curl $BOA_BASE_URL/backend/stations/exportall > stations.json
#curl $BOA_BASE_URL/backend/routes/exportall > routes.json
#curl $BOA_BASE_URL/backend/runs/exportall > runs.json
#curl $BOA_BASE_URL/backend/stops/exportall > stops.json

echo ""
echo ""
echo "---"
echo "cleandb"
echo "---"
curl $BOA_BASE_URL/backend/cleandb

echo ""
echo ""
echo "---"
echo "stations/bulkimport"
echo "---"
curl -H "Content-Type: application/json" -X POST -d @stations.json $BOA_BASE_URL/backend/stations/bulkimport

echo ""
echo ""
echo "---"
echo "routes/bulkimport"
echo "---"
curl -H "Content-Type: application/json" -X POST -d @routes.json $BOA_BASE_URL/backend/routes/bulkimport

echo ""
echo ""
echo "---"
echo "runs/bulkimport"
echo "---"
curl -H "Content-Type: application/json" -X POST -d @runs.json $BOA_BASE_URL/backend/runs/bulkimport

echo ""
echo ""
echo "---"
echo "stop/bulkimport"
echo "---"
curl -H "Content-Type: application/json" -X POST -d @stops.json $BOA_BASE_URL/backend/stops/bulkimport

echo ""
echo ""
echo "---"
echo "stops/bulkimport"
echo "---"
curl -H "Content-Type: application/json" -X POST -d @stops.json $BOA_BASE_URL/backend/stops/bulkimport

echo ""
echo ""
echo "---"
echo "runs/bulkimport"
echo "---"
curl -H "Content-Type: application/json" -X POST -d @runs.json $BOA_BASE_URL/backend/runs/bulkimport

#echo ""
#echo ""
#echo "---"
#echo "checkpoints/bulkcreation"
#echo "---"
#curl $BOA_BASE_URL/backend/checkpoints/bulkcreation
