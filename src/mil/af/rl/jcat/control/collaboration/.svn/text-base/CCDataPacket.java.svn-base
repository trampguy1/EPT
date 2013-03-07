/*
 * Created on Dec 30, 2005
 * Author:  Mike D
 * CCDataPacket - Packet Object containing some sort of data sent/received by CollabControl
 */
package mil.af.rl.jcat.control.collaboration;

import java.awt.Point;
import java.io.Serializable;

public class CCDataPacket implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final int LOGIN_TYPE = 1;
	public static final int LOGIN_INFO = 0;
	public static final int LOG_INFO = 2;
	public static final int ANNOUNCE = 3;
	public static final int ANNOUNCE_HIGH_PRIORITY = 4;
	public static final int OTHER = 10;

	private int type;
	private Serializable value = null;
	private Point loc = new Point(0,0);

	public CCDataPacket(int tp, Serializable arg) 
	{
		type = tp;
		value = arg;
	}

	//intended specifically for an announcment packet
	public CCDataPacket(int tp, Serializable arg, java.awt.Point location)
	{
		this(tp, arg);
		loc = location;
	}

	public int getPacketType()
	{
		return type;
	}

	public Serializable getValue()
	{
		return value;
	}

	public Point getLoc()
	{
		return loc;
	}

	public void setValue(Serializable newVal)
	{
		value = newVal;
	}
}