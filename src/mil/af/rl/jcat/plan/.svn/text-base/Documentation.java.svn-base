/*
 * Created on Aug 5, 2004
 *
 */
package mil.af.rl.jcat.plan;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mil.af.rl.jcat.util.Resource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author vogels /// galimem as of Oct 31, 2004
 * 
 */
public class Documentation implements Serializable
{
	private static final long serialVersionUID = 1L;
	String description = new String();
    LinkedList comments = new LinkedList();
    LinkedList resources = new LinkedList();
    LinkedList keyWords = new LinkedList();

    public void setComments(LinkedList list)
    {
        comments = list;
    }

    public void setResources(LinkedList list)
    {
        //rewritten to set resources instead of add 02/02/05
        resources = list;
    }

    public void insertKeyWord(String keyWord)
    {
        keyWords.add(keyWord);
    }

    public void setKeyWords(LinkedList list)
    {
        keyWords = list;
    }

    public void insertDescription(String des)
    {
        description = des;
    }

    public LinkedList getComments()
    {
        return comments;
    }

    public LinkedList getResources()
    {
        return resources;
    }

    public LinkedList getKeyWords()
    {
        return keyWords;
    }

    public String getDescription()
    {
        return description;
    }
    
    public boolean isDocumented()
    {
    	if(comments.size() + resources.size() + keyWords.size() > 0 || description.trim().length() > 0)
    		return true;
    	else
    		return false;
    }
    
    public Object clone()
    {
        
        Documentation d = new Documentation();
        d.insertDescription(this.getDescription());
        d.setComments(d.getComments());
        d.setKeyWords(d.getKeyWords());
        d.setResources(d.getResources());
        return d;
    }

    //added to allow Documentation Saving -MPG
    public static Documentation getDocumentation(Element ment)
    {
        Documentation docClass = new Documentation();

        //set the description
        String description = ment.valueOf("./description");
        docClass.description = description;

        //set the resources

        LinkedList resources = new LinkedList();

        List rlist = ment.selectNodes("./resource");

        for (Iterator it = rlist.iterator(); it.hasNext();)
        {
            if (rlist.size() == 0)
            {
                break;
            }
            Resource res = new Resource();
            Element e = (Element) it.next();

            res.setLocation(e.valueOf("./location"));

            res.setName(e.valueOf("./name"));

            res.setOriginator(e.valueOf("./originator"));

            res.setDate(e.valueOf("./date"));

            resources.add(res);

        }

        docClass.resources = resources;

        //set the comments

        LinkedList comments = new LinkedList();

        List clist = ment.selectNodes("./comment");

        for (Iterator it = clist.iterator(); it.hasNext();)
        {

            if (clist.size() == 0)
            {
                break;
            }

            Comment com = new Comment();
            Element e = (Element) it.next();

            com.setComment(e.valueOf("./commenttext"));
            com.setOriginator(e.valueOf("./originator"));
            com.setDate(e.valueOf("./date"));

            comments.add(com);

        }

        docClass.comments = comments;

        //set the keywords

        LinkedList keywords = new LinkedList();

        List klist = ment.selectNodes("./keyword");

        for (Iterator it = klist.iterator(); it.hasNext();)
        {
            if (klist.size() == 0)
            {
                break;
            }
            String kwrd;
            Element e = (Element) it.next();

            kwrd = e.getText();

            keywords.add(kwrd);

        }

        docClass.keyWords = keywords;

        return docClass;
    }

    public Document toXML()
    {

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("documentation");

        root.addElement("description").addText(description);

        for (int i = 0; i < comments.size(); i++)
        {
            Comment currentComment = (Comment) comments.get(i);
            root.add(currentComment.toXML());
        }

        for (int i = 0; i < resources.size(); i++)
        {
            Resource currentResource = (Resource) resources.get(i);
            root.add(currentResource.toXML());
        }

        for (int i = 0; i < keyWords.size(); i++)
        {
            root.addElement("keyword").addText((String) keyWords.get(i));
        }

        return document;
    }
}
