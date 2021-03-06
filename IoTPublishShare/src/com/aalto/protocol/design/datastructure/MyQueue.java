package com.aalto.protocol.design.datastructure;

import java.util.LinkedList;

public class MyQueue {

	private LinkedList<Packet> myLinkedList= new LinkedList<Packet>();
	
	private LinkedList<Packet> windowList= new LinkedList<Packet>();
	
	private int windowStart = 0; // Always zero ???
	
	private int windowEnd = 1; // Starting with 1 -> incremented
	
	public LinkedList<Packet> getSendingWindow() {
		
		if(windowList.size() == (windowEnd-windowStart)){             // the window is full
			return windowList;
		} else if (windowList.size() < (windowEnd-windowStart)){      // window is not full -- need to fetch entries from bigger Linked List
			int count = (windowEnd-windowStart)-windowList.size();
			for (Packet element : myLinkedList){
				if(count > 0){
					windowList.add(element);
					count--;
					myLinkedList.remove();
				} else {
					break;
				}
			}
			return windowList;
		} else {                                                      // window is bigger than expected -- shrink window and send the stuff ( packets might be lost)          
			LinkedList<Packet> shrinkedList= new LinkedList<Packet>();
			int count = 0;
			for (Packet element : myLinkedList){
				if(count<(windowEnd-windowStart)){
					shrinkedList.add(element);
				} else {
					break;
				}
			}
			windowList = shrinkedList;
			return windowList;
		}
		
	}

	public void pushToQueue(Packet element){
		myLinkedList.add(element);
	}
	
	private boolean removeFromQueue(Packet packet){
		if(windowList.contains(packet)){
			windowList.remove(windowList.indexOf(packet));
			return true;
		} else {
			// duplicate acknowledgement
			return false;
		}
	}
	
	public boolean removePacketWithSeqNumFromQueue(int seqNum){
		boolean removed = false;
		for (Packet packet : windowList){
			if(packet.isSent() && packet.getSeqNum()==seqNum){
				removed = removeFromQueue(packet);
				break;
			}
		}
		return removed;
	}
	
	public void linearIncementCwnd(){
		windowEnd++;
	}
	
	public void exponentialIncementCwnd(){
		int currWindowSize = windowList.size();
		double power = Math.log(currWindowSize);
		power++;
		windowEnd = (int) Math.exp(power);
	}
	
	public void halveCwnd(){
		for(int i=windowEnd; i>(windowEnd/2); i--){
			myLinkedList.addFirst(windowList.get(i)); // Moving elements from windowList to myLinkedList on halving the congestion Window
			windowList.remove(i);
		}
		if(windowEnd>2){
			windowEnd = windowEnd/2;
		} else {
			windowEnd = 1;
		}
		
	}
	
	public void setMinimumCwnd(){
		for(int i=windowEnd; i > 1; i--){
			myLinkedList.addFirst(windowList.get(i)); // Moving elements from windowList to myLinkedList on making the congestion Window 1
			windowList.remove(i);
		}
		windowEnd = 1;
	}
	
	public int getWindowStart() {
		return windowStart;
	}

	public int getWindowEnd() {
		return windowEnd;
	}

	
	
}
