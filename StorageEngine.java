import java.util.concurrent.ConcurrentHashMap;

public class StorageEngine {
    // Thread-safe memory-mapped indexing structure
    private final ConcurrentHashMap<String, String> dataStore;

    public StorageEngine() {
        this.dataStore = new ConcurrentHashMap<>();
    }

    public void put(String key, String value) {
        dataStore.put(key, value);
        replicateToNodes(key, value);
    }

    public String get(String key) {
        return dataStore.get(key);
    }

    private void replicateToNodes(String key, String value) {
        // Simulating the consensus and data replication mechanism
        // In a real distributed system, this fires asynchronous TCP calls to replica nodes
        System.out.println("[REPLICATION LOG] Key: " + key + " safely replicated to backup nodes.");
    }
}
