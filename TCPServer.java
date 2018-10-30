import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

public class TCPServer {
	
    public static void main(String[] args) throws IOException, NoSuchElementException {
    	ServerSocket serverSocket = null;
		boolean listening = true;
		try {
			serverSocket = new ServerSocket(4445);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4445.");
			System.exit(-1);
		}
        System.out.println("Game Server is up and running.....");
		while (listening)
		{
			Socket clntSock = serverSocket.accept();  //accept the incoming call, and pass the NEW socket to the thread
			TCPServerThread game = new TCPServerThread(clntSock);
			Thread T = new Thread(game);
			T.start();
		}
		serverSocket.close();
    }
}