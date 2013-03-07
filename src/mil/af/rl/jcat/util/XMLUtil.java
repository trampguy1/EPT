package mil.af.rl.jcat.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;

/**
 * <p>
 * Title: XMLUtil.java
 * </p>
 * <p>
 * Description: Utility class to manipulate XML documents
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Edward Verenich
 * @version 1.0
 */

public class XMLUtil
{
	private static Logger logger = Logger.getLogger(XMLUtil.class);
	
    public XMLUtil()
    {
    }

    public static org.w3c.dom.Document toDOM(org.dom4j.Document doc)
            throws org.dom4j.DocumentException
    {
        DOMWriter writer = new DOMWriter();
        return writer.write(doc);
    }

    public static org.dom4j.Document toDom4j(org.w3c.dom.Document doc)
    {
        DOMReader reader = new DOMReader();
        return reader.read(doc);
    }

    public static org.dom4j.Document insertElement(Document doc, String xpath,
            String element, String value)
    {

        Element e = (Element) doc.selectSingleNode(xpath);
        List content = e.content();
        Element ne = DocumentFactory.getInstance().createElement(element);
        if (value != null)
        {
            ne.setText(value);
        }
        content.add(0, ne);
        logger.debug("insertElement - \n"+doc.asXML());
        return doc;
    }

    public static org.dom4j.Document insertAttribute(Document doc,
            String xpath, String attribute, String value)
    {

        Element e = (Element) doc.selectSingleNode(xpath);
        if (value != null)
        {
            e.addAttribute(attribute, value);
        }
        logger.debug("insertElement - \n"+doc.asXML());
        return doc;
    }

    public static org.dom4j.Document replaceElement(Document doc, String xpath,
            String value)
    {
        Element e = (Element) doc.selectSingleNode(xpath);
        if (value != null)
        {
            e.setText(value);
        }
        logger.debug("insertElement - \n"+doc.asXML());
        return doc;
    }

    public static org.dom4j.Document replaceAttribute(Document doc,
            String xpath, String attribute, String value)
    {
        Element e = (Element) doc.selectSingleNode(xpath);
        if (value != null)
        {
            e.addAttribute(attribute, value);
        }
        logger.debug("insertElement - \n"+doc.asXML());
        return doc;
    }

    public static void main(String[] args)
    {
        Document doc = null;
        SAXReader reader = new SAXReader();
        java.io.File test = new java.io.File("./resources/SimpleProcess.xml");
        try
        {
            doc = reader.read(test);
            // now the test
            XMLUtil.insertElement(doc, "//ModeSet[@mode='3']", "SignalSet",
                    null);
            XMLUtil.insertElement(doc, "//ModeSet[@mode='3']", "SignalSet",
                    "TEST");
            XMLUtil.insertAttribute(doc, "//ModeSet[@mode='3']/SignalSet[1]",
                    "number", "3");

        } catch (Exception e)
        {
            e.printStackTrace(System.out);
        }

    }

}