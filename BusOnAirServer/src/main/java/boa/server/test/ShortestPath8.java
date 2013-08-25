/*
 * Output atteso:
 * dt: 563	(1014:ID574:STAZID70:TIME795)-->(2321:ID1705:STAZID70:TIME810)-->(2333:ID1715:STAZID70:TIME810)-->(2335:ID1716:STAZID83:TIME815)-->(5250:ID4267:STAZID83:TIME820)-->(1433:ID937:STAZID83:TIME845)-->(1434:ID938:STAZID72:TIME850)-->(1435:ID939:STAZID9:TIME860)-->(2537:ID1900:STAZID9:TIME860)-->(6304:ID5195:STAZID9:TIME860)-->(6305:ID5196:STAZID8:TIME868)-->(6306:ID5197:STAZID7:TIME873)-->(6307:ID5198:STAZID5:TIME878)-->(6308:ID5199:STAZID4:TIME883)-->(6309:ID5200:STAZID53:TIME888)-->(6310:ID5201:STAZID95:TIME890)-->(6311:ID5202:STAZID1:TIME893)
 * 
(1014:ID574:STAZID70:TIME795)-->(1017:ID575:STAZID71:TIME800)-->(5251:ID4268:STAZID71:TIME823)-->(5252:ID4269:STAZID72:TIME825)-->(5253:ID4270:STAZID3:TIME830)-->(594:ID215:STAZID3:TIME833)-->(451:ID87:STAZID3:TIME840)-->(3755:ID2970:STAZID3:TIME843)-->(3756:ID2971:STAZID15:TIME850)-->(4051:ID3222:STAZID15:TIME850)-->(4362:ID3488:STAZID15:TIME850)-->(4859:ID3923:STAZID15:TIME850)-->(5932:ID4866:STAZID15:TIME850)-->(1703:ID1167:STAZID15:TIME855)-->(1706:ID1168:STAZID55:TIME858)-->(1707:ID1169:STAZID54:TIME865)-->(WALK: t:11|arrtime:876|distwalk:0.9406955539146882|numchanges:3)

(1014:ID574:STAZID70:TIME795)-->(1017:ID575:STAZID71:TIME800)-->(5251:ID4268:STAZID71:TIME823)-->(5252:ID4269:STAZID72:TIME825)-->(5253:ID4270:STAZID3:TIME830)-->(594:ID215:STAZID3:TIME833)-->(451:ID87:STAZID3:TIME840)-->(452:ID88:STAZID4:TIME845)-->(4369:ID3493:STAZID4:TIME857)-->(6852:ID5665:STAZID4:TIME870)-->(6853:ID5666:STAZID53:TIME875)-->(6854:ID5667:STAZID2:TIME877)-->(6855:ID5668:STAZID95:TIME879)-->(WALK: t:11|arrtime:890|distwalk:0.9818784824271852|numchanges:3)

(1014:ID574:STAZID70:TIME795)-->(1017:ID575:STAZID71:TIME800)-->(5251:ID4268:STAZID71:TIME823)-->(5252:ID4269:STAZID72:TIME825)-->(5253:ID4270:STAZID3:TIME830)-->(594:ID215:STAZID3:TIME833)-->(451:ID87:STAZID3:TIME840)-->(452:ID88:STAZID4:TIME845)-->(4369:ID3493:STAZID4:TIME857)-->(6852:ID5665:STAZID4:TIME870)-->(6853:ID5666:STAZID53:TIME875)-->(WALK: t:9|arrtime:884|distwalk:0.7577758502916099|numchanges:3)

(1014:ID574:STAZID70:TIME795)-->(2321:ID1705:STAZID70:TIME810)-->(2333:ID1715:STAZID70:TIME810)-->(2335:ID1716:STAZID83:TIME815)-->(5250:ID4267:STAZID83:TIME820)-->(1433:ID937:STAZID83:TIME845)-->(1434:ID938:STAZID72:TIME850)-->(1435:ID939:STAZID9:TIME860)-->(2537:ID1900:STAZID9:TIME860)-->(6304:ID5195:STAZID9:TIME860)-->(6305:ID5196:STAZID8:TIME868)-->(6306:ID5197:STAZID7:TIME873)-->(6307:ID5198:STAZID5:TIME878)-->(6308:ID5199:STAZID4:TIME883)-->(6309:ID5200:STAZID53:TIME888)-->(6310:ID5201:STAZID95:TIME890)-->(6311:ID5202:STAZID1:TIME893)-->(WALK: t:0|arrtime:893|distwalk:0.0013822123004936543|numchanges:2)

 */


//

// NB: output in /tmp/sp1.log


package boa.server.test;

import boa.server.domain.DbConnection;
import boa.server.domain.Station;
import boa.server.domain.Stations;
import boa.server.domain.Stop;
import boa.server.routing.Criteria;
import boa.server.routing.ShortestPathGeo;
import com.vividsolutions.jts.geom.Coordinate;
import org.neo4j.graphdb.GraphDatabaseService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author rashta
 */
public class ShortestPath8 {

    public static void main(String[] args) throws IOException {
        DbConnection.createEmbeddedDbConnection();
        GraphDatabaseService db = DbConnection.getDb();


        FileWriter logFile = new FileWriter("/tmp/sp1.log");
        BufferedWriter log = new BufferedWriter(logFile);


        int time = 0;     //9h00
//            Station s1 = Stations.getStations().getStationById(70);
//            Station s2 = Stations.getStations().getStationById(1);

        Station s1 = Stations.getStations().getStationById(22);
        Station s2 = Stations.getStations().getStationById(33);

        log.write("\ns1: " + s1);
        log.write("\ns2: " + s2);

//            shortestpath.BreadthTraverser.shortestPath(s1, s2, time);
//            Path foundPath = shortestpath.ShortestPath.shortestPath(s1, s2, time);
//            
//                
//            if(foundPath != null){
//            log.writeln( "ShortestPath: "
//                                + Traversal.simplePathToString( foundPath) );
//            } else {
//            log.writeln( "ShortestPath: NULL");
//
//            }
        Stop firstStop = s1.getFirstStopFromTime(time);
//            int prevTime = time;


        //log.write(firstStop);
//                myShortest mysp = new myShortest(firstStop, s2, 1440);
//                myShortestGeo mysp = new myShortestGeo(firstStop, s2, 1440);

        double lat1 = 42.3799;
        double lon1 = 13.298555;

        double lat2 = 42.34300;
        double lon2 = 13.46300;

        ShortestPathGeo mysp = new ShortestPathGeo(time, 1400, lat1, lon1, lat2, lon2, 1000, Criteria.MINCHANGES);
        mysp.shortestPath();
        boa.server.plugin.json.Directions directs = new boa.server.plugin.json.Directions(new Coordinate(lon1, lat1), mysp.getArrivalList());
        for (boa.server.plugin.json.Direction d : directs.getDirectionsList()) {
            log.write(d.toString());
        }


//                Stop arrivo = mysp.getShortestPath();
        //log.write("\n\nSTOP ARRIVO" + arrivo);
//                log.write("\n-------\ndt: " + (arrivo.getTime() - prevTime));


        log.write("\n\n" + mysp.toString());
//                String outPath = "";
//                for(Stop s : mysp.getWeightedPath()){
//                    outPath = "(" + s.getUnderlyingNode().getId() + ":ID" + s.getId()  + ":STAZID" + s.getStazione().getId() + ":TIME" + s.getTime() + ")-->" + outPath;                
//
//                }


//            Stop arrivo = s2.getFirstStopsFromTime(time);
//            
//            while(arrivo != null && !cache.check(arrivo)){
//                arrivo = arrivo.getNextInStation();
////                log.writeln( "\n\nNot reachable node: " + arrivo);
//            }
//
//            arrivo = cache.get(arrivo);
//            while(arrivo != null){
//                arrivo = cache.get(arrivo);
//
//                log.writeln("\n\n#cambi: " + arrivo.numeroCambi);
//
//                String outPath = "(" + arrivo.getUnderlyingNode().getId() + ":ID" + arrivo.getId() + ":STAZID" + arrivo.getStazione().getId() + ":TIME" + arrivo.getTime() + ")";
//                
//
//                Stop arr = arrivo;
//                while(!arr.equals(firstStop)){
//                    arr = arr.prevSP;
//                    outPath = "(" + arr.getUnderlyingNode().getId() + ":ID" + arr.getId()  + ":STAZID" + arr.getStazione().getId() + ":TIME" + arr.getTime() + ")-->" + outPath;                
//                }
//
//                log.writeln( "myShortestPath: " + outPath);
//
//   
//                do{
//                    arrivo = arrivo.getNextInStation();   
//                    if(arrivo != null)
//                        arrivo = cache.get(arrivo);
//                } while(arrivo != null && arrivo.prevSP == null);
//            }
//            


        log.flush();

        DbConnection.turnoff();
    }


}
