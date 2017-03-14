package raft.solution;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.Snapshottable;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.snapshot.SnapshotReader;
import io.atomix.copycat.server.storage.snapshot.SnapshotWriter;

import java.util.HashMap;
import java.util.Map;

public class MapStateMachine extends StateMachine implements Snapshottable {
  private Map<Object, Object> map = new HashMap<>();

  public void put(Commit<PutCommand> commit) {
    try {
      map.put(commit.operation().key(), commit.operation().value());
    } finally {
      commit.close();
    }
  }

  public Object get(Commit<GetQuery> commit) {
    try {
      return map.get(commit.operation().key());
    } finally {
      commit.close();
    }


  }

  @Override
  public void snapshot(SnapshotWriter writer) {
    writer.writeObject(map);
  }

  @Override
  public void install(SnapshotReader reader) {
    map = reader.readObject();
  }
}
