package ie.transportdublin.server;

import ie.transportdublin.server.plugin.directions.DirectionsResource;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.server.database.Database;


public class ServerPluginTest extends Neo4jTestCase
{

    private DirectionsResource directionsPlugin;
    private AbstractGraphDatabase db;
    private Database database;
    private long startTime ;

    @Before
    public void setUp() throws Exception
    {
//        startTime = System.nanoTime();
//        db = new EmbeddedGraphDatabase("target/db" );
//        this.database  = new Database((AbstractGraphDatabase) db );
//        directionsPlugin = new DirectionsResource(null, database,  null );  

    }

    @Test
    public void testDirectionsplugin01()
    {       
//        Response response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//        response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//        assertEquals(response.getStatus() , 200 );
    }
//    
//    @Test
//    public void testDirectionsplugin02()
//    {
//       Response response =   directionsPlugin.directions(53.37388058688718, -6.364164810302782, 53.36179430400081,-6.299620132568407, 1000 );
//       assertEquals(response.getStatus() , 200 );
//    }
//    @Test
//    public void testDirectionsplugin03()
//    {
//       Response response =   directionsPlugin.directions(53.37388058688718, -6.364164810302782, 53.36179430400081,-6.299620132568407, 1000 );
//       assertEquals(response.getStatus() , 200 );
//    }
//    @Test
//    public void testDirectionsplugin04()
//    {
//
//       Response response =   directionsPlugin.directions( 53.377464,-6.374817, 53.373665, -6.251902, 1100 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    
//    @Test
//    public void testDirectionsplugin05()
//    {
//
//       Response response =   directionsPlugin.directions( 53.377464,-6.374817, 53.373665, -6.251902, 1100 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin06()
//    {
//
//       Response response =   directionsPlugin.directions( 53.336901, -6.222363, 53.377464,-6.374817, 1200 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//
//    @Test
//    public void testDirectionsplugin07()
//    {
//
//       Response response =   directionsPlugin.directions( 53.336901, -6.222363, 53.377464,-6.374817, 1200 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin08()
//    {
//
//       Response response =   directionsPlugin.directions( 53.336901, -6.222363, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin09()
//    {
//
//       Response response =   directionsPlugin.directions( 53.336901, -6.222363, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin10()
//    {
//
//       Response response =   directionsPlugin.directions( 53.377464,-6.374817, 53.373665, -6.251902, 1100 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin11()
//    {
//
//       Response response =   directionsPlugin.directions( 53.336901, -6.222363, 53.377464,-6.374817, 1200 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    
//    @Test
//    public void testDirectionsplugin12()
//    {
//        Response response =   directionsPlugin.directions(53.377464,-6.374817 , 53.336901, -6.222363, 1200 );
//        assertEquals(response.getStatus() , 200 );
//
//    }
//    
//    @Test
//    public void testDirectionsplugin13()
//    {
//
//       Response response =   directionsPlugin.directions( 53.336901, -6.222363, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin14()
//    {
//       
//       Response response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin15()
//    {
//       
//       Response response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin16()
//    {
//       
//       Response response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin17()
//    {
//       
//       Response response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }
//    @Test
//    public void testDirectionsplugin18()
//    {
//       
//       Response response =   directionsPlugin.directions(  53.347234, -6.259117, 53.373665, -6.251902, 1000 );
//       assertEquals(response.getStatus() , 200 );
//
//    }

    @After
    public void tearDown() throws Exception
    {
//        long endTime = (System.nanoTime() - startTime)/ 1000000;
//        db.shutdown();
    }




}
