package raft.solution;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.client.ServerSelectionStrategies;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * @author Yogesh.Manware
 *
 */
public class Client {
  final static int PORT_START = 7001;

  public static void main(String[] args) throws InterruptedException {

    // set counter at 10;
    ClientWriter cw2 = new ClientWriter(PORT_START, 10);
    cw2.start();
    cw2.join();

    CountDownLatch cdl = new CountDownLatch(5);

    // print value of counter at all server instance
    for (int i = PORT_START; i < PORT_START + 5; i++) {
      new ClientReader(i, cdl).start();
    }
    cdl.await();

    // update new value
    ClientWriter cw3 = new ClientWriter(7002, 5);
    cw3.start();
    cw3.join();

    System.out.println();
    System.out.println("--------------AFTER ADDITION-----------------");

    cdl = new CountDownLatch(5);

    // print value of counter at all server instance
    for (int i = PORT_START; i < PORT_START + 5; i++) {
      new ClientReader(i, cdl).start();
    }
    cdl.await();

  }
}


class ClientReader extends Thread {
  private ServerSelectionStrategies strategy = ServerSelectionStrategies.ANY;
  private int port;

  private CountDownLatch cdl;

  public ClientReader(int port, CountDownLatch cdl) {
    this.port = port;
    this.cdl = cdl;
  }

  @Override
  public void run() {
    CopycatClient client =
        CopycatClient.builder().withTransport(NettyTransport.builder().withThreads(2).build())
            .withServerSelectionStrategy(this.strategy).build();

    client.serializer().register(PutCommand.class);
    client.serializer().register(GetQuery.class);

    CompletableFuture<CopycatClient> future =
        client.connect(Arrays.asList(new Address("localhost", this.port)));
    future.join();

    CompletableFuture<Object> future4 = client.submit(new GetQuery("counter"));
    future4.thenAccept(result2 -> {
      System.out.format("Server with at Port: %d, counter: %d", this.port, result2);
      System.out.println();
      this.cdl.countDown();
    });

    future4.join();
  }
}


class ClientWriter extends Thread {
  private ServerSelectionStrategies strategy = ServerSelectionStrategies.ANY;
  private int port;
  private int value;

  public ClientWriter(int port, int value) {
    this.port = port;
    this.value = value;
  }

  public ClientWriter(int port, int value, ServerSelectionStrategies s) {
    this(port, value);
    if (this.strategy != null) {
      this.strategy = s;
    }
  }

  @Override
  public void run() {
    CopycatClient client =
        CopycatClient.builder().withTransport(NettyTransport.builder().withThreads(2).build())
            .withServerSelectionStrategy(this.strategy).build();

    client.serializer().register(PutCommand.class);
    client.serializer().register(GetQuery.class);

    CompletableFuture<CopycatClient> future =
        client.connect(Arrays.asList(new Address("localhost", this.port)));
    future.join();


    CompletableFuture<Object> future4 = client.submit(new GetQuery("counter"));
    future4.thenAccept(result2 -> {
      int counter = 0;
      if (result2 == null) {
        counter = this.value;
      } else {
        counter = ((int) result2) + this.value;
      }

      final int counterf = counter;

      CompletableFuture<Object> future2 = client.submit(new PutCommand("counter", counterf));
      future2.join();
    });
  }
}
