package boa.server.importer.domain;

import java.util.HashMap;

import org.neo4j.gis.spatial.indexprovider.LayerNodeIndex;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import boa.server.domain.CheckPoint;
import boa.server.domain.DbConnection;
import boa.server.domain.RelTypes;
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
	
	@Override
    public void setLastCheckPoint(CheckPoint last){
    	Relationship rel = underlyingNode.getSingleRelationship(RelTypes.RUN_LASTCHECKPOINT, Direction.OUTGOING);
    	if(rel != null)
    		rel.delete();
        underlyingNode.createRelationshipTo(last.getUnderlyingNode(), RelTypes.RUN_LASTCHECKPOINT);		
    }
	

	
}
