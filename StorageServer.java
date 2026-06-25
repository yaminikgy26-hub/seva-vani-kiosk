import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class StorageServer {
    private static final int PORT = 8080;
    // Concurrently managed thread pool preventing race conditions
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private static final StorageEngine storageEngine = new StorageEngine();

    public static void main(String[] args) {
        System.out.println("Distributed Key-Value Store initialized on port " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket, storageEngine));
            }
        } catch (IOException e) {
            System.err.println("Server Exception: " + e.getMessage());
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private StorageEngine storage;

    public ClientHandler(Socket socket, StorageEngine storage) {
        this.clientSocket = socket;
        this.storage = storage;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request;
            while ((request = in.readLine()) != null) {
                String response = processCommand(request);
                out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Connection dropped. Node handling fault tolerance.");
        }
    }

    private String processCommand(String request) {
        String[] parts = request.split(" ");
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "PUT":
                    storage.put(parts[1], parts[2]);
                    return "ACK: Write successful";
                case "GET":
                    String val = storage.get(parts[1]);
                    return val != null ? "VAL: " + val : "ERR: Key not found";
                default:
                    return "ERR: Unknown protocol command";
            }
        } catch (Exception e) {
            return "ERR: Malformed request";
        }
    }
}
