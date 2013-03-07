package mil.af.rl.jcat.util;

import java.util.ArrayList;
import java.lang.Object;
import mil.af.rl.jcat.gui.MainFrm;
import mil.af.rl.jcat.plan.Event;
import java.util.Vector;


/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author MPG
 * @version 1.0
 */
public class NodeSearch
{
	Object[] searchWords = null;
	MainFrm frame = MainFrm.getInstance();
	int searchType ;
	private Object[] keywords;
	private int swCount;
	private ArrayList retList;
	private Event event;
	private boolean caseSensitive = false;
	
	//type == 0: Search Keywords, type == 1: Search Node Text, type == 2: Search Both
	public NodeSearch(int type, boolean caseSens)
	{
		searchType = type;
		caseSensitive = caseSens;
	}
	
	public Object[] getResults(Vector v)
	{
		retList = new ArrayList();
		Object[] events = frame.getActiveView().getPlan().getAllEvents().toArray();
		searchWords = v.toArray();
		swCount = searchWords.length;
		
		//check event keywords for search word(s) 
		if(searchType == 0)
			searchKeywords(events);
		//check node text for search word(s)
		else if(searchType == 1)
			searchNodeText(events);
		//check both
		else if(searchType == 2)
		{
			searchKeywords(events);
			searchNodeText(events);
		}
		
		return retList.toArray();
	}
	
	
	private void searchKeywords(Object[] arEvents)
	{
		//iterate events
		for (int i=0; i < arEvents.length; i++)
		{
			event = (Event) arEvents[i];
			keywords = event.getDocumentation().getKeyWords().toArray();
			search();
		}
	}
	
	private void searchNodeText(Object[] arEvents)
	{
		for(int i=0; i < arEvents.length; i++)
		{
			event = (Event) arEvents[i];
			
			String text = ((String)(event.getName())).concat(" "+event.getLabel());
			keywords = (Object[])(text.split(" "));
			
			search();
		}
	}
	
	private void search()
	{
		boolean writeToList = false;
		int kwCount = keywords.length;
		
		//iterate search words				
		for(int j=0; j < swCount; j++)
		{
			String sWord = (String)searchWords[j];
			//iterate node text words
			for(int k=0; k < kwCount; k++)
			{
				String kWord = (String) keywords[k];
				if(caseSensitive)
				{
					if(kWord.contains(sWord))
						writeToList = true;
				}
				else
				{
					if(kWord.toLowerCase().contains(sWord.toLowerCase()))
						writeToList = true;					
				}
			}
		}
		
		if(writeToList)
			retList.add(event.clone());
	}
}
