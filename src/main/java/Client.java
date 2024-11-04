import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromServer;

                while (socket.isConnected()) {
                    try {
                        messageFromServer = in.readLine();
                        System.out.println(messageFromServer);
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            out.write(message);       // Write the message to the buffer
            out.newLine();            // Add a newline to signify the end of the message
            out.flush();              // Flush the buffer to send the message
        } catch (IOException e) {
            closeEverything(socket, in, out); // Handle exceptions by closing resources
        }
    }


    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 6379);
        Client client = new Client(socket);
        client.listenForMessages();

        Scanner scanner = new Scanner(System.in);
        String message;

        System.out.println("Type your message. Type 'exit' to quit.");

        while (socket.isConnected()) {
            message = scanner.nextLine();

            if (message.equals("exit")) {
                break;
            }

            client.sendMessage(message);
        }

        scanner.close();
        client.closeEverything(socket, client.in, client.out);
    }
}
