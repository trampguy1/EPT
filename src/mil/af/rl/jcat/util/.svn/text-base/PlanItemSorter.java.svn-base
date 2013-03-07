/*
 * Created on Nov 14, 2005
 * MikeyD
 * Used to sort PlanItems based on sort mode selected (used by NavTree to sort tree items)
 */
package mil.af.rl.jcat.util;

import java.util.List;
import java.util.ListIterator;

public class PlanItemSorter
{
	public static final int ALPHABETIC_ASCEND = 0;
	public static final int ALPHABETIC_DESCEND = 1;
	public static final int CAUSES = 2;
	public static final int INHIBITS = 3;
	public static final int EFFECTS = 4;
	static int sortType = 0;
	
	//method from Collections with some changes
	public static void sort(List list, int type) 
	{
		sortType = type;
		Object[] a = list.toArray();
		sortArray(a);
		ListIterator i = list.listIterator();
		for (int j=0; j<a.length; j++)
		{
			i.next();
			i.set(a[j]);
		}
	}
	
	//method same as is in Collections.class with a couple mod's
	public static void sortArray(Object[] a)
	{
		Object[] aux = (Object[])a.clone();
		mergeSort(aux, a, 0, a.length, 0);
	}
	
	//method same as is in Array.class with a couple mod's
	private static final int INSERTIONSORT_THRESHOLD = 7;
	private static void mergeSort(Object[] src, Object[] dest, int low, int high, int off)
	{
		int length = high - low;
		
		// Insertion sort on smallest arrays
		if (length < INSERTIONSORT_THRESHOLD) 
		{
			for (int i=low; i<high; i++)
				for (int j=i; j>low && compare(((PIComparable)dest[j-1]), (dest[j])) >0; j--)
					swap(dest, j, j-1);
			return;
		}
		
		// Recursively sort halves of dest into src
		int destLow  = low;
		int destHigh = high;
		low  += off;
		high += off;
		int mid = (low + high) >> 1;
		mergeSort(dest, src, low, mid, -off);
		mergeSort(dest, src, mid, high, -off);
		
		// If list is already sorted, just copy from src to dest.  This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if ( compare(((PIComparable)src[mid-1]), (src[mid])) <= 0)
		{
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}
		
		// Merge sorted halves (now in src) into dest
		for(int i = destLow, p = low, q = mid; i < destHigh; i++)
		{
			if (q >= high || p < mid && compare(((PIComparable)src[p]), (src[q])) <=0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}
	
	//method same as is in Array.class
	private static void swap(Object[] x, int a, int b)
	{
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
	
	public static int compare(PIComparable obj1, Object obj2)
	{
		if(sortType == ALPHABETIC_DESCEND)
			return obj1.descendCompareTo(obj2);
		else if(sortType == CAUSES)
			return obj1.causeCompareTo(obj2);
		else if(sortType == INHIBITS)
			return obj1.inhibitCompareTo(obj2);
		else if(sortType == EFFECTS)
			return obj1.effectCompareTo(obj2);
		else
			return obj1.compareTo(obj2);
	}
}
