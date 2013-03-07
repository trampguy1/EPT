package mil.af.rl.jcat.siamtransform.xml;

/*
 * File SIAMTransform.java Copywrite (c) 2005 - All rights reserved Developed by
 * NGI Systems Engineer Simon Vogel Created on Dec, 2005
 */

import java.io.ByteArrayInputStream;
import java.io.FileWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import java.io.File;

public class SIAMTransform {

    private File _siamPath = null; //Path to the SIAM model
    private String _transformPath = null; //Path to the XSLT file
    private String _resultPath = null; //Path to save the CAT model at
    private Document _catDoc = null; //Transformed plan xml doc
    public boolean writeFile = true;
    private static Logger logger = Logger.getLogger(SIAMTransform.class);
    

    public SIAMTransform() {

    }

    //Setters\Getters
    public void setSIAMPath(File p) {
        _siamPath = p;
    }

    public void setTransformPath(String p) {
        _transformPath = p;
    }

    public void setResultPath(String p) {
        _resultPath = p;
    }

    public File getSIAMPath() {
        return _siamPath;
    }

    public String getTransformPath() {
        return _transformPath;
    }

    public String getResultPath() {
        return _resultPath;
    }

    //Returns the transformed document, which should be a valid CAT plan
    public Document getCATDocument() {
        return _catDoc;
    }

    public void performTransform() {
        try {
            Document doc = loadSIAM(_siamPath);
            Document transform = styleDocument(doc, _transformPath);
//            write(transform,_resultPath);
            if(writeFile)
                write(transform, _resultPath);
        } catch (Exception e) {
        	logger.error("performTransform - Error transforming SIAM document:  "+e.getMessage());
        }
    }

    public Document loadSIAM(File path) {

        Document document = null;
        SAXReader reader = null;

        try {
            reader = new SAXReader();

            //Set the entity resolver such that it ignores the doctype and
            // associated DTD - makes it
            //possible to load SIAM files without access to the netwrok and DTD
            reader.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicID, String systemID) {
                    ByteArrayInputStream stream = new ByteArrayInputStream(
                            "<?xml version='1.0' encoding = 'UTF-8'?>".getBytes());
                    return new InputSource(stream);
                }
            });

            reader.setIncludeExternalDTDDeclarations(false);

            document = reader.read(path);
        } catch (Exception e) {
            logger.error("loadSIAM - error reading/parsing SIAM file:  "+e.getMessage());
        }

        return document;
    }

    public Document styleDocument(Document document, String stylesheet)
            throws Exception {

        //The following from dom4j FAQ for using xslt
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(
                stylesheet));

        DocumentSource source = new DocumentSource(document);
        DocumentResult result = new DocumentResult();
        //SAXResult result = new DocumentResult();

        try{
            transformer.transform(source, result);
        }catch(TransformerException te)
        {
            logger.error("styleDocument - Error tranforming document:  "+te.getMessage());
        }
        
        Document transformedDoc = result.getDocument();
        
        _catDoc = transformedDoc;
        return transformedDoc;
    }

    public void write(Document d, String path) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(new FileWriter(path), format);
            writer.write(d);
            writer.close();
        } catch (Exception e) {
            logger.error("write - error writing converted SIAM document to file:  "+e.getMessage());
        }
    }

}
