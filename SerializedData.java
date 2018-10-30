import java.io.Serializable;

//Author: Gamamada (Lurein) Perera
//Date: 09/19/2018

public class SerializedData implements Serializable {
	private int[] data; 
	
	public SerializedData(int[] d) { //This is the constructor
		this.data = d;
	}
	
	public int[] getData() {
		return data;
	}
	public void setData(int[] d) {
		this.data = d;
	}
	
}