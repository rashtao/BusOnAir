package boa.server.importer.domain;

import java.util.HashMap;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.Node;

import boa.server.domain.CheckPoint;
import boa.server.domain.DbConnection;
import boa.server.domain.Run;

public class RunImporter extends Run {

    public RunImporter(Node node, int id){
    	super();
    	underlyingNode = node;
        setId(id);
        setType();
    	cpIndex = DbConnection.getDb().index().forNodes("cpIndex" + getId());
    }   
	
    public RunImporter(Run r){
    	super(r.getUnderlyingNode());
    }   
	
    
	public void importCheckPoint(CheckPoint cp) {
        cpIndex.add(cp.getUnderlyingNode(), "id", cp.getId());
    }
    
	public void createCheckPointsSpatialIndex() {
		if(checkPointsSpatialIndex == null)
			checkPointsSpatialIndex = new LayerNodeIndex( "checkPointsSpatialIndex" + getId(), DbConnection.getDb(), new HashMap<String, String>() );

		for(CheckPoint cp : getAllCheckPoints()){
			checkPointsSpatialIndex.add(cp.getUnderlyingNode(), "", "" );
		}
	}

	
}
