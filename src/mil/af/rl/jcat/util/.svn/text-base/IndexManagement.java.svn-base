/**
 * 
 */
package mil.af.rl.jcat.util;

/**
 * @author John Lemmer
 * The purpose of this class is to map subsets of events to the corresponding (integer)
 * index of the power set of all events and vice versa. It is assumed that each bit in the index
 * corresponds to one element of the overall set of events. A bit set in the index implies the corresponding 
 * event is an element of the (indexed) subset
 *
 */
public class IndexManagement {
	static boolean userGeneratedEvent = true;
	static public int getIndex(int[] subset){
		int index = 0;
		for(int i : subset){
			index = index | (1 << i);
		}
		return index;
	}
	static public int[] getSubset(int index){
		final int bitLimit = /*sizeof(int):http://java.sun.com/docs/books/performance/1st_edition/html/JPRAMFootprint.fm.html*/
			4 * 8/* 8 bits per byte*/;// would be nice if the constant 8 were replaced by system constant!
		int bitCount = 0;
		for(int i = 0; i < bitLimit; i++){
			if(((index >> i) & 1) == 1){
				bitCount += 1;
			}
		}
		int subset[] = new int[bitCount];
		int currentSize = 0;
		for(int i = 0; i < bitLimit; i++){
			if(((index >> i) & 1) == 1){
				subset[currentSize++] = i;
			}
		}
		return subset;
	}
	/**
	 * @return Returns the userGeneratedEvent.
	 */
	public static boolean isUserGeneratedEvent() {
		return userGeneratedEvent;
	}
	/**
	 * @param userGeneratedEvent The userGeneratedEvent to set.
	 */
	public static void setUserGeneratedEvent(boolean userGeneratedEvent) {
		IndexManagement.userGeneratedEvent = userGeneratedEvent;
	}
}
