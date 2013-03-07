/*
 * Created on Dec 29, 2005
 */
package mil.af.rl.jcat.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;

public class CustomFocusPolicy extends DefaultFocusTraversalPolicy
{

	private static final long serialVersionUID = 1L;
	private Component defComponent = null;

	public CustomFocusPolicy(Component defaultComponent)
	{
		defComponent = defaultComponent;
	}
	
//	public Component getComponentAfter(Container aContainer, Component aComponent)
//	{
//		System.out.println("getAfter");
//		System.out.println(aContainer.getComponentCount());
//		
//		if(!aContainer.isFocusCycleRoot())
//		{
//			aComponent = aContainer;
//			aContainer = aContainer.getFocusCycleRootAncestor();
//		}
//		
//		Component[] comps = aContainer.getComponents();
//		System.out.println(comps.length);
//		
//		for(int x=0; x<comps.length; x++)
//		{
//			if(comps[x].equals(aComponent))
//			{
//				System.out.println("found one");
//				if(x+1 < comps.length)
//				{
//					System.out.println(comps[x+1]);
//					return comps[x+1];
//				}
//				else
//				{
//					System.out.println(comps[0]);
//					return comps[0];
//				}
//			}
//		}
//		
//		System.out.println(aComponent);
//		return aComponent;
//	}

//	public Component getComponentBefore(Container aContainer, Component aComponent)
//	{
//		System.out.println("getBefo");
//		Component[] comps = aContainer.getComponents();
//				
//		for(int x=comps.length-1; x>=0; x--)
//		{
//			if(comps[x].equals(aComponent))
//			{
//				System.out.println("found one");
//				if(x-1 < comps.length)
//					return comps[x-1];
//				else
//					return comps[comps.length-1];
//			}
//		}
//		
//		return aComponent;
//	}

//	public Component getFirstComponent(Container aContainer)
//	{
//		System.out.println("getFirst");
//		return aContainer.getComponents()[0];
//	}
//
//	public Component getLastComponent(Container aContainer)
//	{
//		System.out.println("getLast");
//		return aContainer.getComponents()[aContainer.getComponentCount()-1];
//	}

	public Component getDefaultComponent(Container aContainer)
	{
		return defComponent;
	}

}
