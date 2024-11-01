import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public Server(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);

            startServer();

        } catch (IOException e) {
            throw e;
        } finally {
            this.closeServerSocket();
        }
    }

    public void startServer() throws IOException {
        try {
            while (!serverSocket.isClosed()) {
                // Wait for connection from client.
                clientSocket = serverSocket.accept();
                System.out.println("New client connected!");

                ClientHandler clientHandler = new ClientHandler(clientSocket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void closeServerSocket() throws IOException {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
