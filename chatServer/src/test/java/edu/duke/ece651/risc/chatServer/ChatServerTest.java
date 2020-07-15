package edu.duke.ece651.risc.chatServer;

import org.junit.jupiter.api.Test;

public class ChatServerTest {
  @Test
  public void test_ChatServer() {
    try{
      Thread fakeClient1 = new Thread(new FakeClient("mesg1"));
      Thread fakeClient2 = new Thread(new FakeClient("mesg2"));
      fakeClient1.start();
      fakeClient2.start();

      ChatServer server = ChatServer.start("src/main/resources/config.txt");
      for (int i = 0; i < 2; i++) {
        server.handleRequest();
      }

      Thread fakeClient3 = new Thread(new FakeClient(""));
      fakeClient3.start();

      fakeClient1.join();
      fakeClient2.join();
      
      server.handleRequest();
    } catch (Exception e) {
    }
  }
}
