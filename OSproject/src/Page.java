
import java.util.Random;

/**
 * 
 */

/**
 * @author cd1207
 *
 */
public class Page {

	//constructor:
	public Page(int owningProcessID, int pageNumber) {  // we make a page by giving it a process (A-J) and page number (0-9)
		this.owningProcessID = owningProcessID;
		this.pageNumber = pageNumber;
		//turn the process ID into a letter, so A-J
		this.adjustedProcessID = 65 + owningProcessID;
		this.processIDletter = (char)adjustedProcessID;
	}

	//fields:
	public final int owningProcessID;
	public final int pageNumber;
	public boolean contentBit = false;
	public boolean referenceBit = false;
	public int touchCount = 0;
	//Needed for naming:
	private final int adjustedProcessID;
	public final char processIDletter;
	
	//this is how we output to the console
	public String toString() {
		//turn the process ID into a letter, so A-J
		return processIDletter + "" + pageNumber + (contentBit ? "*" : " ");
	}
	
	public int compareTo(Page o) {
		int comparison = 0;
		if (this.touchCount < o.touchCount)
			comparison = -1;
		else if (this.touchCount > o.touchCount)
			comparison = -1;
		return comparison;
	}
	
	public void use() {							//for all algorithms
		if (new Random().nextInt(10) == 5)
			this.contentBit = true;
	}
	
	public void touch() {						//for L/MFU
		touchCount++;
		this.use();
	}
	
	public void reference() {					//for LRU
		this.use();
		this.referenceBit = true;
	}
	
	public int getTouchCount() {
		return this.touchCount;
	}
	
}
