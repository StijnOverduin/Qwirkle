package tests;

import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    client.run();
    
  }

  @After
  public void tearDown() throws Exception {
    server.shutDown();
    client.shutDown();
  }

  @Test
  public void testSendMessage() {
    client.sendMessage("HELLO Stijn");
    
    
  }

}
