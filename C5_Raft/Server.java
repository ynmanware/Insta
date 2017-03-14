package raft.solution;

import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;



/**
 * @author Yogesh.Manware
 *
 */
public class Server {

  public static CopycatServer createServer(String host, int port) {
    Address address = new Address(host, port);
    CopycatServer server =
        CopycatServer
            .builder(address)
            .withStateMachine(MapStateMachine::new)
            .withTransport(NettyTransport.builder().withThreads(4).build())
            .withStorage(
                Storage.builder().withDirectory(new File(host + "_" + port + "_logs"))
                    .withStorageLevel(StorageLevel.DISK).build()).build();

    server.serializer().register(PutCommand.class);
    server.serializer().register(GetQuery.class);
    return server;
  }

  public static void main(String[] args) throws InterruptedException {
    CopycatServer server1 = createServer("localhost", 7001);
    CopycatServer server2 = createServer("localhost", 7002);
    CopycatServer server3 = createServer("localhost", 7003);
    CopycatServer server4 = createServer("localhost", 7004);
    CopycatServer server5 = createServer("localhost", 7005);

    CompletableFuture<CopycatServer> future1 = server1.bootstrap();

    CompletableFuture<CopycatServer> future2 =
        server2.join(Arrays.asList(new Address("localhost", 7001)));
    future2.join();

    CompletableFuture<CopycatServer> future3 =
        server3.join(Arrays.asList(new Address("localhost", 7001)));
    future3.join();

    CompletableFuture<CopycatServer> future4 =
        server4.join(Arrays.asList(new Address("localhost", 7001)));
    future4.join();

    CompletableFuture<CopycatServer> future5 =
        server5.join(Arrays.asList(new Address("localhost", 7001)));
    future5.join();

    future1.join();

    System.out.println("Cluster is ready to serve");
  }
}
/**
 *
 * @Author Yogesh.Manware
 *
 */


