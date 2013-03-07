package mil.af.rl.jcat.util;

import java.io.ByteArrayInputStream;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * Title: XTree
 * </p>
 * <p>
 * Description: The XTree class is an extension of the javax.swing.JTree class.
 * Some methods have been provided to facilitate the parsing of an XML document
 * into a DOM object and translating that DOM object into a viewable JTree
 * structure.
 * </p>
 * <p>
 * Company: C3I/AFRL
 * </p>
 * 
 * @author Edward Verenich
 * @version 1.0
 */

public class XTree extends JTree
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(XTree.class);
	private DefaultMutableTreeNode treeNode;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private Document doc;

	/**
	 * This constructor builds an XTree object using the XML text passed in
	 * through the constructor.
	 * 
	 * @param text
	 *            A String of XML formatted text
	 * 
	 * @exception ParserConfigurationException
	 *                This exception is potentially thrown if the constructor
	 *                configures the parser improperly. It won't.
	 */
	public XTree(String text) throws ParserConfigurationException
	{
		// Initialize the superclass portion of the object
		super();

		// Set basic properties for the Tree rendering
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		setEditable(false); // A more advanced version of this tool would allow
		// the Tree to be editable

		// Begin by initializing the object's DOM parsing objects
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		db = dbf.newDocumentBuilder();

		// Take the DOM root node and convert it to a Tree model for the JTree
		treeNode = createTreeNode(parseXml(text));
		setModel(new DefaultTreeModel(treeNode));
	}

	/**
	 * This constructor builds an XTree object using the XML text passed in
	 * through the constructor.
	 * 
	 * @param text
	 *            A String of XML formatted text
	 * 
	 * @exception ParserConfigurationException
	 *                This exception is potentially thrown if the constructor
	 *                configures the parser improperly. It won't.
	 */
	public XTree(org.w3c.dom.Document doc) throws ParserConfigurationException
	{
		// Initialize the superclass portion of the object
		super();

		// Set basic properties for the Tree rendering
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		setEditable(true); // A more advanced version of this tool would allow
		// the Tree to be editable

		// Begin by initializing the object's DOM parsing objects
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		db = dbf.newDocumentBuilder();

		// Take the DOM root node and convert it to a Tree model for the JTree
		treeNode = createTreeNode(doc);
		setModel(new DefaultTreeModel(treeNode));
		this.doc = doc;
	}

	public Document getDocument()
	{
		return this.doc;
	}

	/**
	 * Method takes a DOM Node and recurses through the children until each one
	 * is added to a DefaultMutableTreeNode. The JTree then uses this object as
	 * a tree model.
	 * 
	 * @param root
	 *            org.w3c.Node.Node
	 * @return Returns a DefaultMutableTreeNode object based on the root Node
	 *         passed in
	 */
	public DefaultMutableTreeNode createTreeNode(Node root)
	{
		DefaultMutableTreeNode treeNode = null;
		String type, name, value;
		NamedNodeMap attribs;
		Node attribNode;

		// Get data from root node
		type = getNodeType(root);
		name = root.getNodeName();
		value = root.getNodeValue();

		// Special case for TEXT_NODE
		treeNode = new DefaultMutableTreeNode(
				root.getNodeType() == Node.TEXT_NODE ? value : name);

		// Display the attributes if there are any
		attribs = root.getAttributes();
		if (attribs != null)
		{
			for (int i = 0; i < attribs.getLength(); i++)
			{
				attribNode = attribs.item(i);
				name = attribNode.getNodeName().trim();
				value = attribNode.getNodeValue().trim();

				if (value != null)
				{
					if (value.length() > 0)
					{
						treeNode.add(new DefaultMutableTreeNode(
								"[Attribute] --> " + name + "=\"" + value
								+ "\""));
					}
				}
			}
		} //end if( attribs != null )

		// Recurse children nodes if any exist
		if (root.hasChildNodes())
		{
			NodeList children;
			int numChildren;
			Node node;
			String data;

			children = root.getChildNodes();
			// Only recurse if Child Nodes are non-null
			if (children != null)
			{
				numChildren = children.getLength();

				for (int i = 0; i < numChildren; i++)
				{
					node = children.item(i);
					if (node != null)
					{
						// A special case could be made for each Node type.
						if (node.getNodeType() == Node.ELEMENT_NODE)
						{
							treeNode.add(createTreeNode(node));
						} //end if( node.getNodeType() == Node.ELEMENT_NODE )

						data = node.getNodeValue();

						if (data != null)
						{
							data = data.trim();
							if (!data.equals("\n") && !data.equals("\r\n")
									&& data.length() > 0)
							{
								treeNode.add(createTreeNode(node));
							}
						}
					}
				}
			}
		}
		return treeNode;
	}

	/**
	 * Method returns a string representing the type of node passed in.
	 * 
	 * @param node
	 *            org.w3c.Node.Node
	 * 
	 * @return Returns a String representing the node type
	 */
	private String getNodeType(Node node)
	{
		String type;

		switch (node.getNodeType())
		{
			case Node.ELEMENT_NODE:
			{
				type = "Element";
				break;
			}
			case Node.ATTRIBUTE_NODE:
			{
				type = "Attribute";
				break;
			}
			case Node.TEXT_NODE:
			{
				type = "Text";
				break;
			}
			case Node.CDATA_SECTION_NODE:
			{
				type = "CData section";
				break;
			}
			case Node.ENTITY_REFERENCE_NODE:
			{
				type = "Entity reference";
				break;
			}
			case Node.ENTITY_NODE:
			{
				type = "Entity";
				break;
			}
			case Node.PROCESSING_INSTRUCTION_NODE:
			{
				type = "Processing instruction";
				break;
			}
			case Node.COMMENT_NODE:
			{
				type = "Comment";
				break;
			}
			case Node.DOCUMENT_NODE:
			{
				type = "Document";
				break;
			}
			case Node.DOCUMENT_TYPE_NODE:
			{
				type = "Document type";
				break;
			}
			case Node.DOCUMENT_FRAGMENT_NODE:
			{
				type = "Document fragment";
				break;
			}
			case Node.NOTATION_NODE:
			{
				type = "Notation";
				break;
			}
			default:
			{
				type = "???";
				break;
			}
		}
		return type;
	}

	/**
	 * Method performs the actual parsing of the XML text
	 * 
	 * @param text
	 *            A String representing an XML document
	 * @return Returns an org.w3c.Node.Node object
	 */
	private Node parseXml(String text)
	{
		ByteArrayInputStream byteStream;

		byteStream = new ByteArrayInputStream(text.getBytes());

		try
		{
			doc = db.parse(byteStream);
		} catch (Exception e)
		{
			logger.error("parseXml - Error parsing xml from text:  ", e);
		}
		return (Node) doc.getDocumentElement();
	}

}