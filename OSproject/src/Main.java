/**
 * 
 */
import java.util.*;

/**
 * @author Connor
 * Licensed under GPLv3
 * 
 * Team Christian/Joseph/Connor/Chris
 * OS Course Final project
 * Emulates the 
 */
public class Main {
	// custom values:
	public final static int iterations = 500000;		// How many times to run. for example, one billion (1000000000)
	public final static int maxSize = 16;				// we can change this to simulate more RAM!
	public final static int processCount = 10;			// we can change this to simulate more processes!
	public final static int pagesPerProcess = 10;		// we can change this to simulate more pages!   (more weight on 8&9)
	
	// choose between FIFO, LIFO, LFU, MFU, LRU, "Random walk"
	public static String algorithm = "FIFO";	
	// output mode: normal/spreadsheet/matrix/teamGUI
	public static String outputMode = "normal";
	
	//programming values:
	public static List<Page> table = new ArrayList<Page>(); 					//this is our queue / table of pages
	public static Page[][] hardDrive = new Page[processCount][pagesPerProcess];	//all pages in existence in this array (100)
	public static int cycleNumber = 0;											//keep track of what cycle we're on
	public static int pageInCount = 0;											//keep track of page-ins
	public static int pageOutCount = 0;										//keep track of page-outs
	public static int residentCount = 0;										//keep track of resident page accesses
	public static String guiText = "";										//make the GUI team happy
	public static boolean pagedOut = false;
	public static boolean RPA = false;
	public static void main(String[] args) {
		if (args.length > 0)
			outputMode = args[0];
		
		//populate hard drive with all pages (100)
		for (int process = 0; process < processCount; process++) {
			for (int page = 0; page < pagesPerProcess; page++) {
				hardDrive[process][page] = new Page(process, page);
			}
		}
		
		//go!
		while (cycleNumber  < iterations)
			RoundRobin();
		
		output(cycleNumber + " cycles run\n" + pageInCount + " pages in (" + ((double)pageInCount/(double)cycleNumber)*100 + "%)\t  << Page faults\n" + pageOutCount + " pages out (" + ((double)pageOutCount/(double)cycleNumber)*100 + "%)\n" + residentCount + " pages resident (" + ((double)residentCount/(double)cycleNumber)*100 + "%)"
				+"\t <<non-PF", "", "");
	}
	
	public static void RoundRobin() {
		//Loop through all processes!
			switch (algorithm.toUpperCase().trim()) {
			case "RANDOM WALK":
			case "LIFO":
			case "FIFO":
				for (Page[] process : hardDrive)
					FIFO(process[pickaPage()]);
				break;
			case "LFU":
				for (Page[] process : hardDrive)
					LFU(process[pickaPage()]);
				break;
			case "MFU":
				for (Page[] process : hardDrive)
					MFU(process[pickaPage()]);
				break;
			case "LRU":
				for (Page[] process : hardDrive)
					LRU(process[pickaPage()]);
				break;
			default:
				System.out.println("Couldn't figure out which algorithm you wanted!");
		}			
	}

	public static void nextLine() {
		output("\n","\n","");
		if (outputMode.toLowerCase() == "matrix")
			drawMatrix();
		if (outputMode.toLowerCase() == "teamgui")
			generateGUI();
	}

	public static void FIFO(Page page) {
		cycleNumber++;
		if (table.contains(page)) { 						//it's already in memory					<possibility 1>
				resident(page);								//do... nothing
				page.use();									//access the page, possibly changing it 	[all 4]
		} 
		else {												//it's not already in memory! now check:
			if (table.size() >= maxSize) {					//RAM is full!  now check:
				
				if (table.get(0).contentBit) {			//candidate-for-removal page has changed! 	<possibility 4>
					pageOut(table.get(0));
				}
															//candidate removal page has not changed.	<possibility 3>
				table.remove(0);								//first in, first out						[3,4]
			}
			table.add(page);								//RAM isn't full							<possibility 2>
			page.use();										//access the page, possibly changing it 	[all 4]
			pageIn(page);									//page it in, back of the line 				[2,3,4]
		}
		//log to console
		nextLine();
	}

	public static void LRU(Page page) {
		cycleNumber++;
		if (table.contains(page)) { 						//it's already in memory					<possibility 1>
				resident(page);								//do... nothing
				page.use();									//access the page, possibly changing it 	[all 4]
		} 
		else {												//it's not already in memory! now check:
			if (table.size() >= maxSize) {					//RAM is full!  now check:
				
				Collections.sort(table, new LRU()); 	//percolate the least frequently used to the top.
				if (table.get(0).contentBit) {			//candidate-for-removal page has changed! 	<possibility 4>
					pageOut(table.get(0));
				}
															//candidate removal page has not changed.	<possibility 3>
				table.remove(0);								//decapitate						[3,4]
			}
			table.add(page);								//RAM isn't full							<possibility 2>
			page.reference();								//access the page, possibly changing it 	[all 4]
			pageIn(page);									//page it in, back of the line 				[2,3,4]
		}

		
		//LRU only:
		if (table.size() == maxSize && table.get(2).referenceBit) { //2 or less pages not referenced
			for (Page cleaningpage : table) {				// go through the whole table
				cleaningpage.referenceBit = false;			// reset all reference bits
			}
		}
		//log to console
		nextLine();	
	}
	
	public static void LFU(Page page) {
		cycleNumber++;
		if (table.contains(page)) { 						//it's already in memory					<possibility 1>
				resident(page);								//do... nothing
				page.touch();									//access the page, possibly changing it 	[all 4]
		} 
		else {												//it's not already in memory! now check:
			if (table.size() >= maxSize) {					//RAM is full!  now check:
				
				Collections.sort(table, new LFU()); 	//percolate the least frequently used to the top.
				if (table.get(0).contentBit) {			//candidate-for-removal page has changed! 	<possibility 4>
					pageOut(table.get(0));
				}
															//candidate removal page has not changed.	<possibility 3>
				table.remove(0);								//decapitate						[3,4]
			}
			table.add(page);								//RAM isn't full							<possibility 2>
			page.touch();								//access the page, possibly changing it 	[all 4]
			pageIn(page);									//page it in, back of the line 				[2,3,4]
		}
		//log to console
		nextLine();
	}
	
	public static void MFU(Page page) {
		cycleNumber++;
		if (table.contains(page)) { 						//it's already in memory					<possibility 1>
				resident(page);								//do... nothing
				page.touch();									//access the page, possibly changing it 	[all 4]
		} 
		else {												//it's not already in memory! now check:
			if (table.size() >= maxSize) {					//RAM is full!  now check:
				
				Collections.sort(table, new MFU()); 	//percolate the least frequently used to the top.
				if (table.get(0).contentBit) {			//candidate-for-removal page has changed! 	<possibility 4>
					pageOut(table.get(0));
				}
															//candidate removal page has not changed.	<possibility 3>
				table.remove(0);								//decapitate						[3,4]
			}
			table.add(page);								//RAM isn't full							<possibility 2>
			page.touch();								//access the page, possibly changing it 	[all 4]
			pageIn(page);									//page it in, back of the line 				[2,3,4]
		}
		//log to console
		nextLine();	
	}

	
	public static void resident(Page page) {
		residentCount++;
		output("["+table.size()+"]\t" + "         "+ page + "\t", "+0\t", page.processIDletter + "," + page.pageNumber + ",0,0,1");
		RPA = true;
	}	
	
	public static void pageIn(Page page) {
		pageInCount++;
		output("["+table.size()+"]\t" + "Page IN  "+ page + "\t", "+10\t", page.processIDletter + "," + page.pageNumber + ",10");
	}

	public static void pageOut(Page page) {
		output("["+table.size()+"]\t" + "Page OUT "+ page + "\n","+10\t", "");
		page.contentBit = false; //important!
		page.referenceBit = false;
		pageOutCount++;
		pagedOut = true;
	}

	public static int pickaPage() { 
		int page=500;  								//an out-of-bounds number so errors are obvious
		int ranNum = new Random().nextInt(100);
		
		if (ranNum < 45) { 							//bottom 45% chance (5 less than a 50% chance)
			page = 8; 
		}
		else if (ranNum >= 45 && ranNum < 90) { 	//top 45% chance
			page = 9;
		}
		else if (ranNum >= 90) { 					//remaining 10% chance -  all other pages
			page = new Random().nextInt(pagesPerProcess - 2);
		}

		return page;
	}
	
	public static void output(String normal, String spreadsheet, String teamgui) {
		//log to console
		switch (outputMode.toLowerCase()) {
		case "normal":
			System.out.print(normal);
			break;
		case "spreadsheet":
			System.out.print(spreadsheet);
			break;
		case "teamgui":
			guiText += teamgui;
			break;
		}
	}
	
	public static void drawMatrix() {
		System.out.println(table);

	}
		
	private static void generateGUI() {
		System.out.println(guiText + (RPA ? "" : (pagedOut ? ",10,0" : ",0,0")));
		guiText = "";
		pagedOut = false;
		RPA = false;
	}



}
