package ie.transportdublin.dbinsertershortestpath;

import static org.junit.Assert.assertNotNull;
import ie.transportdublin.shortestpath.ShortestPath;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class ShortestPathTest
{
    
    
    private static GraphDatabaseService db;
    private static ShortestPath threeLayeredTraverserShortestPath;

    @BeforeClass
    public static void setup()
    {
//        db = new EmbeddedGraphDatabase( "target/db" );
//        threeLayeredTraverserShortestPath = new ShortestPath(db);
    }
    
    
    @Test
    public void findShortestPath()
    {
//        findShortestPaths();
    }
    
    
    public void findShortestPaths()
    {
        Transaction tx = db.beginTx();
        try
        {
            long startTime = System.nanoTime();
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.365304,-6.254997, 53.306878,-6.329842, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.373088,-6.310272, 53.373665, -6.251902, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.398070,-6.175346, 53.373088,-6.310272, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.284511,-6.132431, 53.381895,-6.086082, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.381895,-6.086082, 53.342148,-6.393356, 1000.0 )  );          
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.341943,-6.252251, 53.381895,-6.086082, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.325337,-6.302032, 53.398070,-6.175346, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.312621,-6.265984, 53.381895,-6.086082, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.325337,-6.302032, 53.398070,-6.175346, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.312621,-6.265984, 53.381895,-6.086082, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.381895,-6.086082, 53.284511, -6.132431, 1000.0 ) );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.342148,-6.393356, 53.381895, -6.086082, 1000.0 ) );          
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.291489,-6.248474, 53.373665, -6.251902, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.306878,-6.329842, 53.373665, -6.251902, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.348502,-6.262550, 53.398070,-6.175346, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.345223,-6.270103, 53.342558,-6.263924, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.312621,-6.265984, 53.342558,-6.263924, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.312621,-6.265984,  53.345223,-6.270103, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.312621,-6.265984,  53.335793,-6.327095, 1000.0 )  );            
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.312621,-6.265984,  53.335793,-6.327095, 1000.0 )  );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.376775, -6.375160, 53.347234, -6.259117, 1000.0 ) );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.306878, -6.329842, 53.365304, -6.254997, 1000.0 ) );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.373665, -6.251902, 53.373088, -6.310272, 1000.0 ) );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.373088, -6.310272, 53.398070, -6.175346, 1000.0 ) );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.373665, -6.251902, 53.291489, -6.248474, 1000.0 ) );
            assertNotNull(threeLayeredTraverserShortestPath.findShortestPath( 53.373665, -6.251902, 53.306878, -6.329842, 1000.0 ) );        

            long endTime = (System.nanoTime() - startTime)/ 1000000;
            System.out.println( "TOTAL TIME: "+endTime + " AVG TIME: "+endTime/27 );
            tx.success();
        }
        finally
        {
            tx.finish();
        }        
    }
    

    @AfterClass
    public static void shutdown()
    {
//        db.shutdown();
    }

}
