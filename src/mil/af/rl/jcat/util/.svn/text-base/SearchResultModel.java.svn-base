package mil.af.rl.jcat.util;

import javax.swing.tree.*;

import mil.af.rl.jcat.plan.Event;

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
 * @author not attributable
 * @version 1.0
 */
public class SearchResultModel
{

    Object[] arResults ;
    DefaultTreeModel model;

    public SearchResultModel(Object[] arEvents)
    {
        arResults = arEvents;
        model = new DefaultTreeModel(getNode());

    }

    public DefaultTreeModel getModel()
    {
        return model;
    }

    public DefaultMutableTreeNode getNode()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();

        for (int j = 0; j < arResults.length; j++)
        {
            Event event = (Event) arResults[j];

            DefaultMutableTreeNode nodeEvent = new DefaultMutableTreeNode(event);
         
            node.add(nodeEvent);

        }

        return node;
    }

}
