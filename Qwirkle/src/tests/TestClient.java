package tests;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import game.Client;
import game.server.Server;

public class TestClient {
  
    private Server server;
    private Client client;
    private InetAddress localhost;
  
    @Before
    public void setUp() throws Exception {
        localhost = localhost.getLocalHost();
        server = new Server(1234);
        server.run();
        client = new Client(localhost, 1234);
        client.start();
      
    }
  
   
  
    @Test
    public void testJar() {
        assertEquals(client.getVirtualJar(), 108);
      
      
    }
    
    @After
    public void tearDown() throws Exception {
        server.shutDown();
        client.shutDown();
    }

}
