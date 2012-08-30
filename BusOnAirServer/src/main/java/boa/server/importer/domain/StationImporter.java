package boa.server.importer.domain;

import org.neo4j.graphdb.Node;

import boa.server.domain.DbConnection;
import boa.server.domain.Station;

public class StationImporter extends Station {

	
    public StationImporter(Node node, int id, String name, double latitude, double longitude, boolean isSchool, boolean isTerminal){
    	super();
    	underlyingNode = node;
        setId(id);
        setName(name);	
        setLatitude(latitude);
        setLongitude(longitude);
        setIsSchool(isSchool);
        setIsSchool(isTerminal);
        setType();
        stopIndex = DbConnection.getDb().index().forNodes("stopIndex" + getId());
    }   


    public StationImporter(Node node, int id, String name, double latitude, double longitude){
        this(node, id, name, latitude, longitude, false, false);
    }   

    public StationImporter(Node node, int id, String name){
        this(node, id, name, 0, 0, false, false);
    }      
}
