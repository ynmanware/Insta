package org.myn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;

/**
 *
 * @Author Yogesh.Manware
 *         https://console.instaclustr.com/dashboard/0e14d126-cfdd-4248-ad2a-78c80b061ff6/clusters
 *
 */

public class C7_CassandraTest {

  private Session session;

  private Cluster cluster;

  public static void main(String[] args) {
    C7_CassandraTest t = new C7_CassandraTest();
    t.connect();
    t.createSchema();
    // t.truncateTable();
    t.loadData();
    t.printData();
    t.close();
  }

  /**
   * 
   */
  private void close() {
    this.session.close();
    this.cluster.close();
  }

  /**
   * 
   */
  public void connect() {
    final Cluster.Builder clusterBuilder =
        Cluster
            .builder()
            .addContactPoints("34.206.120.1", "54.86.97.16", "52.70.193.39" // AWS_VPC_US_EAST_1
                                                                            // (Amazon Web Services
                                                                            // (VPC))
            )
            .withLoadBalancingPolicy(
                DCAwareRoundRobinPolicy.builder().withLocalDc("AWS_VPC_US_EAST_1").build())
            // your local data centre
            .withPort(9042)
            .withAuthProvider(
                new PlainTextAuthProvider("iccassandra", "ec2b4f5094c2db89d3fc44995ad6c9ce"));

    final Cluster cluster = clusterBuilder.build();

    final Metadata metadata = cluster.getMetadata();
    System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());

    for (final Host host : metadata.getAllHosts()) {
      System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(),
          host.getAddress(), host.getRack());
    }

    this.cluster = cluster;
    this.session = cluster.connect();
  }

  /**
   * 
   */
  public void createSchema() {
    session.execute("CREATE KEYSPACE art WITH replication "
        + "= {'class':'SimpleStrategy', 'replication_factor':3};");

    session.execute("CREATE TABLE art.movies (" + "title text," + "release_year int, "
        + "genre text," + "director text," + "PRIMARY KEY (title, release_year)" + ");");
  }

  /**
   * 
   */
  public void truncateTable() {
    session.execute("TRUNCATE TABLE art.movies");
    System.out.println("table truncated...");
  }

  /**
   * 
   */
  public void loadData() {
    String DATA = "movies.csv";
    try (BufferedReader br = new BufferedReader(new FileReader(DATA))) {
      String line = "";
      line = br.readLine();
      while ((line = br.readLine()) != null) {
        // use comma as separator
        String[] s = line.split(",");
        session
            .execute("INSERT INTO art.movies (title, release_year, genre, director) " + "VALUES ("
                + "'" + s[0] + "'," + s[1] + "," + "'" + s[2] + "'," + "'" + s[3] + "' );");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  public void printData() {
    ResultSet f = session.execute("SELECT * FROM art.movies");
    for (Row row : f) {
      System.out.println(row);
    }
  }
}
