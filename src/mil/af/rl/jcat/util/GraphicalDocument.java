package mil.af.rl.jcat.util;

import java.util.LinkedList;
import java.util.ArrayList;

// MPG 01/19/2005

public class GraphicalDocument implements Cloneable
{
    protected String Description = null;

    protected String Comment = "Comments Table";

    protected ArrayList Comments = null;

    protected String Username = null;

    protected String Date = null;

    protected LinkedList FileName = null;

    public void setDescription(String s)
    {
        Description = s;
    }

    //I had to override the clone() method
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public String getDescription()
    {
        return Description;
    }

    public void setComments(ArrayList s)
    {
        Comments = s;
    }

    public ArrayList getComments()
    {
        return Comments;
    }

    public void setComment(String s)
    {
        Comment = "Comments Table";
    }

    public String getComment()
    {
        return Comment;
    }

    public void setUsername(String s)
    {
        Username = s;
    }

    public String getUsername()
    {
        return Username;
    }

    public void setDate(String s)
    {
        Date = s;
    }

    public String getDate()
    {
        return Date;
    }

    public void addFiletoList(String s)
    {
        FileName.add(s);
    }

    public void setFileList(LinkedList lst)
    {
        FileName = lst;
    }

    public LinkedList getFileList()
    {
        return FileName;
    }

}
