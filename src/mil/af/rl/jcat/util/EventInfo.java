/*
 * Created on Apr 22, 2004
 *
 */
package mil.af.rl.jcat.util;

import java.io.IOException;

/**
 * @author John Lemmer
 *  
 */
public class EventInfo extends Object implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	public int A;

    public double B;

    static final int A_default = 7;

    static final double B_default = 47.0;

    public EventInfo()
    {
        this(A_default, B_default);
    }

    public EventInfo(int a, double b)
    {
        A = a;
        B = b;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        System.out.println("EventInfo: trying to write itself");
        out.writeInt(A);
        out.writeDouble(B);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException
    {
        System.out.println("EventInfo: trying reconstruct itself");
        A = in.readInt();
        B = in.readDouble();
    }
}