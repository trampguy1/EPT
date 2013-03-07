package mil.af.rl.jcat.util;

import java.util.Date;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;



public class CustomHTMLLayout extends HTMLLayout
{
	private StringBuffer sbuf;
	private boolean locationInfo;
	private String title;
	static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";


	public CustomHTMLLayout()
	{
		super();
		sbuf = new StringBuffer(256);
		locationInfo = false;
		title = "JCAT Log Messages (Log4j)";

	}


	public String format(LoggingEvent event)
	{
		if(sbuf.capacity() > 1024)
			sbuf = new StringBuffer(256);
		else
			sbuf.setLength(0);
		
		boolean fatal = event.getLevel().equals(Level.FATAL);
		
		sbuf.append(Layout.LINE_SEP + "<tr>" + Layout.LINE_SEP);
		sbuf.append("<td> "+(fatal?"<font color=\"#ff0000\">":""));
		sbuf.append(event.timeStamp - LoggingEvent.getStartTime());
		sbuf.append("</td>" + Layout.LINE_SEP);
		sbuf.append("<td title=\"" + event.getThreadName() + " thread\"> "+(fatal?"<font color=\"#ff0000\">":""));
		sbuf.append(Transform.escapeTags(event.getThreadName()));
		sbuf.append("</td>" + Layout.LINE_SEP);
		sbuf.append("<td title=\"Level\"> "+(fatal?"<font color=\"#ff0000\">":""));
		
		//Log LEVEL
		if(event.getLevel().equals(Level.TRACE)) // light green
			sbuf.append("<font color=\"#33ff33\"><strong>");
		else if(event.getLevel().equals(Level.DEBUG)) // green
			sbuf.append("<font color=\"#229922\"><strong>");
		else if(event.getLevel().equals(Level.INFO)) //blue
			sbuf.append("<font color=\"#0000ff\"><strong>");
		else if(event.getLevel().equals(Level.WARN)) //orange: ff9c00
			sbuf.append("<font color=\"#ff9c00\"><strong>");
		else if(event.getLevel().equals(Level.ERROR)) //pale red: 993300
			sbuf.append("<font color=\"#993300\"><strong>");
		else if(event.getLevel().equals(Level.FATAL)) //red: ff0000
			sbuf.append("<font color=\"#ff0000\"><strong>");
		else
			sbuf.append("<font color=\"black\"><strong>");
		
		sbuf.append(event.getLevel());
		sbuf.append("</strong></font>");

		sbuf.append("</td>" + Layout.LINE_SEP);
		sbuf.append("<td title=\"" + event.getLoggerName() + " category\"> "+(fatal?"<font color=\"#ff0000\">":""));
		sbuf.append(Transform.escapeTags(event.getLoggerName()));
		sbuf.append("</td>" + Layout.LINE_SEP);
		if(locationInfo)
		{
			LocationInfo locInfo = event.getLocationInformation();
			sbuf.append("<td> "+(fatal?"<font color=\"#ff0000\">":""));
			sbuf.append(Transform.escapeTags(locInfo.getFileName()));
			sbuf.append(':');
			sbuf.append(locInfo.getLineNumber());
			sbuf.append("</td>" + Layout.LINE_SEP);
		}
		sbuf.append("<td title=\"Message\"> "+(fatal?"<font color=\"#ff0000\">":""));
		sbuf.append(Transform.escapeTags(event.getRenderedMessage()));
		sbuf.append("</td>" + Layout.LINE_SEP);
		sbuf.append("</tr>" + Layout.LINE_SEP);
		if(event.getNDC() != null)
		{
			sbuf.append("<tr><td bgcolor=\"#EEEEEE\" style=\"font-size : xx-small;\" colspan=\"6\" title=\"Nested Diagnostic Context\">");
			sbuf.append("NDC: " + Transform.escapeTags(event.getNDC()));
			sbuf.append("</td></tr>" + Layout.LINE_SEP);
		}
		String s[] = event.getThrowableStrRep();
		if(s != null)
		{
			sbuf.append("<tr><td bgcolor=\"#993300\" style=\"color:White; font-size : xx-small;\" colspan=\"6\">");
			appendThrowableAsHTML(s, sbuf);
			sbuf.append("</td></tr>" + Layout.LINE_SEP);
		}
		
		if(event.getLevel().equals(Level.FATAL))
			sbuf.append("</font>");
		return sbuf.toString();

	}

	void appendThrowableAsHTML(String s[], StringBuffer sbuf)
	{
		if(s != null)
		{
			int len = s.length;
			if(len == 0)
				return;
			sbuf.append(Transform.escapeTags(s[0]));
			sbuf.append(Layout.LINE_SEP);
			for(int i = 1; i < len; i++)
			{
				sbuf.append(TRACE_PREFIX);
				sbuf.append(Transform.escapeTags(s[i]));
				sbuf.append(Layout.LINE_SEP);
			}

		}
	}

	public String getHeader()
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Layout.LINE_SEP);
		sbuf.append("<html>" + Layout.LINE_SEP);
		sbuf.append("<head>" + Layout.LINE_SEP);
		sbuf.append("<title>" + title + "</title>" + Layout.LINE_SEP);
		sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
		sbuf.append("<!--" + Layout.LINE_SEP);
		sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}" + Layout.LINE_SEP);
		sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}" + Layout.LINE_SEP);
		sbuf.append("-->" + Layout.LINE_SEP);
		sbuf.append("</style>" + Layout.LINE_SEP);
		sbuf.append("</head>" + Layout.LINE_SEP);
		sbuf.append("<body bgcolor=\"#FFFFFF\"o topmargin=\"6\" leftmargin=\"6\">" + Layout.LINE_SEP);
		sbuf.append("<hr size=\"1\" noshade>" + Layout.LINE_SEP);
		sbuf.append("Log session start time " + new Date() + "<br>" + Layout.LINE_SEP);
		sbuf.append("<br>" + Layout.LINE_SEP);
		sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">" + Layout.LINE_SEP);
		sbuf.append("<tr>" + Layout.LINE_SEP);
		sbuf.append("<th>Time</th>" + Layout.LINE_SEP);
		sbuf.append("<th>Thread</th>" + Layout.LINE_SEP);
		sbuf.append("<th>Level</th>" + Layout.LINE_SEP);
		sbuf.append("<th>Category</th>" + Layout.LINE_SEP);
		if(locationInfo)
			sbuf.append("<th>File:Line</th>" + Layout.LINE_SEP);
		sbuf.append("<th>Message</th>" + Layout.LINE_SEP);
		sbuf.append("</tr>" + Layout.LINE_SEP);
		return sbuf.toString();
	}

	public String getFooter()
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("</table>" + Layout.LINE_SEP);
		sbuf.append("<br>" + Layout.LINE_SEP);
		sbuf.append("</body></html>");
		return sbuf.toString();
	}

}
