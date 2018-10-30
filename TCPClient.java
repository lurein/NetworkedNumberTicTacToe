import java.io.*;
import java.net.*;
import java.util.*;
public class TCPClient {

	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchElementException {
//		if (args.length != 1) {
//			System.out.println("Usage: java TCPClient <hostname>");
//			return;
//		}
		// get a TCP socket
		// set up reader from the server and writer from the client
		Socket socket = new Socket("localhost", 4445); // make TCP connection
		OutputStream sendStream;
		InputStream recvStream;
		Scanner scan2= new Scanner(System.in);
		int[] boardOut = {0, 0, 0,
	                      0, 0, 0,
	                      0, 0, 0};
		int[] boardIn;
		int isFirstTime = 0;

		sendStream = socket.getOutputStream();
		recvStream = socket.getInputStream();
		while(true) {
			
			if(isFirstTime == 0) {
				sendBoard(boardOut, sendStream);
				isFirstTime += 1;
			} 
			
			// get board
			boardIn = getBoard(recvStream, socket);
			if(boardIn[0] == -1) {
				System.out.println("You win!!!!");
				System.out.println("Want to play again? y/n");
				if(scan2.next() == "y") {
					Arrays.fill(boardIn, 0);
				} else if(scan2.next() == "n") {
					boardOut[0] = -3;
					sendBoard(boardOut, sendStream);
					break;
				}
			} else if (boardIn[0] == -2) {
				System.out.println("The other player won");
				System.out.println("Want to play again? y/n");
				if(scan2.next() == "y") {
					Arrays.fill(boardIn, 0);
				} else {
					boardOut[0] = -3;
					sendBoard(boardOut, sendStream);
					break;
				}
			}
			
			System.out.println("Current board is: ");
			printBoard(boardIn);
			boardOut = boardIn;
			//send board
			int blockNo = 0, newNumber = 0;
			System.out.println("Select an empty square to use (0-8)");
			blockNo = scan2.nextInt();
			System.out.println("Select an int to put in box" + String.valueOf(blockNo));
			newNumber = scan2.nextInt();
			boardOut[blockNo] = newNumber;
			
			sendBoard(boardOut, sendStream);
			

			try {
				Random rand = new Random();
				int  n = rand.nextInt(3000); 
				Thread.sleep(n+1000); // sleep 5 seconds just to slow things down a bit
			} catch (InterruptedException e) {
			}	
			
		}
		scan2.close();
        sendStream.close();
		recvStream.close();
		socket.close(); // when all done asking for quotes, close the socket
	}

	static void sendBoard(int[] board, OutputStream sendStream) {
		try {
			SerializedData toSend = new SerializedData(board);
			
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(255);
			ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			outputStream.writeObject(toSend);
			outputStream.flush();
			
			byte[] sendBuff = byteStream.toByteArray();
			
			//System.out.println("sending request to server...");
			sendStream.write(sendBuff, 0, sendBuff.length);
			//System.out.println("sent request...");
		} catch (IOException ex) {
			System.err.println("IOException in sendRequest");
		}
	}

	static int[] getBoard(InputStream recvStream, Socket socket) throws IOException, ClassNotFoundException {
		SerializedData toReceive;
		int[] request = null;
		try {
			int dataSize = 255;
			while ((dataSize = recvStream.available()) == 0);
			byte[] recvBuff = new byte[dataSize];
			recvStream.read(recvBuff, 0, dataSize);
			
			ByteArrayInputStream byteStream1 = new ByteArrayInputStream(recvBuff);
			ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(byteStream1));
			toReceive = (SerializedData) inputStream.readObject();
			request = toReceive.getData();
		} catch (SocketException e) {
			System.out.println("Connection closed prematurely");
			socket.close();

		} catch (IOException ex) {
			System.err.println("IOException in getResponse");
		}
		return request;
	} 
	static void printBoard(int[] board) {
		int counter = 1;
		for(int el = 0; el < board.length ;el++){
			System.out.print(board[el]);
			if(counter % 3 == 0) {
				System.out.println("");
			}
			counter += 1;
		}
		
	}
}