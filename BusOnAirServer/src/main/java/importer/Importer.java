package importer;

import domain.DbConnection;
import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.tools.shell.commands.ClearCommand;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.*;

import test.ImportTest;

public class Importer {

    //private static BusonairSql readData;
    private static DBInserter dbInserter;  

    public static BusonairSql importData(){
    	BusonairSql readData = new BusonairSql();
        readData.stationList = XMLReader.readStations().getStationList();
//        readData.routeList = XMLReader.readRoutes().getRouteList();
        readData.stop_scheduleList = XMLReader.readStops().getStopList();
        
        return readData;
    }
    
    public static void main(String[] args) {        
		DbConnection.createEmbeddedDbConnection();
 		
        dbInserter = new DBInserter();
        dbInserter.addData();
        dbInserter.addSpatialIndex();
        ImportTest.importTest();
        dbInserter.generateRunsId();
        dbInserter.duplicateRoutes();
        ImportTest.importTest();
        DbConnection.turnoff();

// TEST LETTURA FILEs XML
//		readData = XMLReader.readRoutes();
//		for (RouteSql route : readData.getRouteList()){
//			System.out.print("\n" + route);			
//		}	
//	
//		readData = XMLReader.readStations();
//		for (StationSql station : readData.getStationList()){
//			System.out.print("\n" + station);			
//		}	
//
//		readData = XMLReader.readStops();
//		for (StopSql stop : readData.getStopList()){
//			System.out.print("\n" + stop);			
//		}	
		

	}
    
    


}
