import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServerThread implements Runnable {
	Socket socket;
	InputStream recvStream;
	OutputStream sendStream;
	Scanner scan= new Scanner(System.in);
	static int[] boardIn;
	static int[] boardOut = {0, 0, 0,
	               0, 0, 0,
	               0, 0, 0};

	//protected BufferedReader in = null;


	public TCPServerThread(Socket clntSock) throws IOException, NoSuchElementException {
		this.socket = clntSock;
		this.recvStream = clntSock.getInputStream();
		this.sendStream = clntSock.getOutputStream();
	
	}

	public void run() {
		while (true) {

			try {
				boardIn = getBoard();
				boardOut = boardIn;
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //blocks
			System.out.println("Current board is:");
			printBoard(boardIn);
			
			// Here we check if the client won the game
			if(hasWinner(boardIn)) {
				System.out.println("The other player won");
				boardOut[0] = -1;
				sendBoard(boardOut, sendStream);
			}
			if(boardIn[0] == -3) {
				break;
			}
			
			System.out.println("Select an empty square to use (0-8)");
			int blockNo = 0, newNumber = 0;
			blockNo = scan.nextInt();
			System.out.println("Select an int to put in box" + String.valueOf(blockNo));
			newNumber = scan.nextInt();
			boardOut[blockNo] = newNumber;
			if(hasWinner(boardOut)) {
				System.out.println("You won!");
				boardOut[0] = -2;
			}
			sendBoard(boardOut, sendStream);
			
		}
		try {
			System.out.println("Server Thread [" + Thread.currentThread().getName() + "] is now gone!");
			socket.close();
			sendStream.close();
			recvStream.close();
			scan.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public int[] getBoard() throws ClassNotFoundException {
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
		} catch (IOException ex) {
			System.err.println("IOException in getRequest");
		}
		return request;
	}

	 public boolean checkVertical(int[] board) {
	    	return
	    			(board[0] + board[3] + board[6] == 15) 
	    			||(board[1] + board[4] + board[7] == 15)
	    			||(board[2] + board[5] + board[8] == 15);
	    }
	    
	    public boolean checkHorizontal(int[] board) {
	    	return
	    			(board[0] + board[1] + board[2] == 15) 
	    			||(board[3] + board[4] + board[5] == 15)
	    			||(board[6] + board[7] + board[8] == 15);
	    }
	    
	    public boolean checkDiagonal(int[] board) {
	    	return
	    			(board[0] + board[4] + board[8] == 15) 
	    			||(board[2] + board[4] + board[6] == 15);
	    }
	    
	    public boolean hasWinner(int[] board) {
	        return
	            checkDiagonal(board) || checkVertical(board) || checkHorizontal(board);
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
