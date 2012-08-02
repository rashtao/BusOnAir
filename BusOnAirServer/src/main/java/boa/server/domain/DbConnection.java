package boa.server.domain;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.database.Database;

//import boa.server.importer.IndexManager;
//import boa.server.importer.RelationshipIndex;

public class DbConnection {
	private static final String dbpath = "/tmp/neo4j/busonairserver/data/graph.db";  
//	private static final String dbpath = "/home/rashta/neo4j/neo4j/busonairserver/data/graph.db";  
    private static DbConnection instance = null;
    private AbstractGraphDatabase db = null;
    private SpatialDatabaseService spatialDb = null;
    
    // DB per applicazione server
    public static synchronized DbConnection createDbConnection(Database _db) {
    	if (instance == null){ 
            instance = new DbConnection(_db);            
        }
        return instance;
    }    
    
    public static void destroy() {
    	instance = null;
    }        
    
    // DB per applicazione standalone
    public static synchronized DbConnection createEmbeddedDbConnection() {
        if (instance == null){ 
            instance = new DbConnection();            
        }
        return instance;
    }    
    
    public static synchronized DbConnection getDbConnection() {

        return instance;
    }    
       
    private DbConnection(Database _db){
        db = (AbstractGraphDatabase) _db.graph;
        spatialDb = new SpatialDatabaseService(db);
    }
    
    private DbConnection(){
//        db = new EmbeddedReadOnlyGraphDatabase( dbpath );
        db = new EmbeddedGraphDatabase( dbpath );
        spatialDb = new SpatialDatabaseService(db);
    }

    
    public static AbstractGraphDatabase getDb(){
        return getDbConnection().getDatabase();
    }
        
    public static SpatialDatabaseService getSpatialDb(){
        return getDbConnection().getSpatialDatabase();
    }
        
    public static void turnoff(){
        getDbConnection().shutdown();
    }
    
    public AbstractGraphDatabase getDatabase(){
        return db;
    }
    
    public SpatialDatabaseService getSpatialDatabase(){
    	return spatialDb;
    }
    
    public void shutdown(){
        db.shutdown();
    }    
    
    public static void clear(){
        deleteRecursively( new File( dbpath ) );
        //inner = new EmbeddedGraphDatabase( storeDir, params );
    }
        
    private static void deleteRecursively( File file )
         {
             if ( !file.exists() )
             {
                 return;
             }
     
             if ( file.isDirectory() )
             {
                 for ( File child : file.listFiles() )
                 {
                     deleteRecursively( child );
                 }
             }
             if ( !file.delete() )
            {
                throw new RuntimeException( "Couldn't empty database. Offending file:" + file );
            }
        }
    
//    private void deleteFileOrDirectory( final File file ) {
//        if ( file.exists() ) {
//            if ( file.isDirectory() ) {
//                for ( File child : file.listFiles() ) {
//                    deleteFileOrDirectory( child );
//                }
//            }
//            file.delete();
//        }
//    }
    

    

	  public Map<String, Object> cleanDb() {
		  Map<String, Object> result = new HashMap<String, Object>();
		  clearIndex(result);
          removeNodes(result);
	        
          org.neo4j.graphdb.index.IndexManager indexManager = db.index();
          result.put("node-indexes", Arrays.asList(indexManager.nodeIndexNames()));
          result.put("relationship-indexes", Arrays.asList(indexManager.relationshipIndexNames()));

          return result;	        
	    }

	    private void removeNodes(Map<String, Object> result) {
	        Node refNode = db.getReferenceNode();
	        
	        for (Node node : db.getAllNodes()) {
	            for (Relationship rel : node.getRelationships()) {
	                rel.delete();
	            }
	            if (!refNode.equals(node)) {
	                node.delete();
	            }
	        }
	    }

	    private void clearIndex(Map<String, Object> result) {
	        org.neo4j.graphdb.index.IndexManager indexManager = db.index();
	        try {
	            for (String ix : indexManager.nodeIndexNames()) {
	                final Index<Node> index = indexManager.forNodes(ix);
//	                getMutableIndex(index).delete();
	                index.delete();
	            }
	            for (String ix : indexManager.relationshipIndexNames()) {
	                final RelationshipIndex index = indexManager.forRelationships(ix);
//	                getMutableIndex(index).delete();
	                index.delete();
	            }
	        } catch (UnsupportedOperationException uoe) {
	            throw new RuntimeException("Implementation detail assumption failed for cleaning readonly indexes, please make sure that the version of this extension and the Neo4j server align");
	        }
	    }    
	    
//	    private <T extends PropertyContainer> Index<T> getMutableIndex(Index<T> index) {
//	        final Class<? extends Index> indexClass = index.getClass();
//	        if (indexClass.getName().endsWith("ReadOnlyIndexToIndexAdapter")) {
//	            try {
//	                final Field delegateIndexField = indexClass.getDeclaredField("delegate");
//	                delegateIndexField.setAccessible(true);
//	                return (Index<T>) delegateIndexField.get(index);
//	            } catch (Exception e) {
//	                throw new UnsupportedOperationException(e);
//	            }
//	        } else {
//	            return index;
//	        }
//	    }	    
}
