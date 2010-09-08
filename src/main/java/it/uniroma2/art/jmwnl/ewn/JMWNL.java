/**
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
 */

package it.uniroma2.art.jmwnl.ewn;

import it.uniroma2.art.jmwnl.ewn.data.EWNPointerTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.util.ResourceBundleSet;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JMWNL {
	
	private static final String ENCODING_TAG="encoding";
	
	//private static final String PARAM_TAG ="param";
	private static final String VALUE_ATTRIBUTE = "value";
	//private static final String NAME_ATTRIBUTE ="name";
	private static final String PUBLISHER_ATTRIBUTE ="publisher";
	
	private static final String EWN_PUBLISHER = "ELRA_EWN";
	private static final String PRINCETON_PUBLISHER = "Princeton";
	private static final String VERSION_TAG = "version";
	
	private static final String WORDNET_DICT_RESOURCE = "WN_Resource";
	private static final String EUROWORDNET_DICT_RESOURCE = "EWN_Resource";
	
	private static boolean isEWN = false;
	
	private static String _dictEncoding;
	
	
	private static Document _doc;
	
	
	/**
	 * This is the first method that should be call of this class. It initializes all the structure and 
	 * it call JWNL.initialize
	 * @param fileProperties the xml property file containing all the parameters
	 * @throws JWNLException
	 */
	public static void initialize(File fileProperties) throws JWNLException {
		
		try {
			_doc = readXMLPropertyFile(new FileInputStream(fileProperties));
			identifyWordNetVocabulary(_doc);
			if (isEWN) {
			    _dictEncoding = readDictEncoding(_doc);
			    ((ResourceBundleSet)JWNL.getResourceBundle()).addResource(EUROWORDNET_DICT_RESOURCE);
			}
			else {
			    ((ResourceBundleSet)JWNL.getResourceBundle()).addResource(WORDNET_DICT_RESOURCE);
			}			    
			JWNL.initialize(new FileInputStream(fileProperties));
			
			if (isEWN)
			    EWNPointerTypes.initialize();
			
		} catch (Exception ex) {
			throw new JWNLException("JWNL_EXCEPTION_002", ex);
		}
	}
	

	/**
	 * This method read and parse the xml property 
	 * @param propertiesStream the xml property file to be parsed
	 * @return the Document associated to the xml property file
	 * @throws JWNLException
	 */
	private static Document readXMLPropertyFile(InputStream propertiesStream) throws JWNLException {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder docBuilder;
        try {
            docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.parse(propertiesStream);
        } catch (ParserConfigurationException ex) {
            throw new JWNLException("JWNL_EXCEPTION_002", ex);
        } catch (SAXException ex) {
            throw new JWNLException("JWNL_EXCEPTION_002", ex);
        } catch (IOException ex) {
            throw new JWNLException("JWNL_EXCEPTION_002", ex);
        }
        try {
            propertiesStream.close();
        } catch (IOException e) {
            //nothing to do: the property file has been read in any case
            e.printStackTrace();
        }
        return doc;
	}
	
	/**
	 * This method identify if the xml property file belongs to EuroWordNet or WordNet
	 * @param doc the Document from which to read the publisher
	 * @throws JWNLException
	 */
	private static void identifyWordNetVocabulary(Document doc) throws JWNLException {
        org.w3c.dom.Element root;
        root = doc.getDocumentElement();
        NodeList versionNodes = root.getElementsByTagName(VERSION_TAG);
        if (versionNodes.getLength() == 0) {
            throw new JWNLException("JWNL_EXCEPTION_003");
        }
        Node version = versionNodes.item(0);        
        String publisher = getAttribute(version, PUBLISHER_ATTRIBUTE);
        if (publisher.equals(EWN_PUBLISHER))
            isEWN=true;
        else if (publisher.equals(PRINCETON_PUBLISHER))
            isEWN=false;
        else throw new JWNLException("JWNL_EXCEPTION_003");        
	}
	
	/**
	 * This method read the encoding of the xml property file
	 * @param doc the Document from which to read the encoding
	 * @return the encoding of the xml property file
	 * @throws JWNLException
	 */
	private static String readDictEncoding(Document doc) throws JWNLException {
		
        org.w3c.dom.Element root;
		root = doc.getDocumentElement();			
		
		NodeList encodingNodeListlist = root.getElementsByTagName(ENCODING_TAG);
		
		if (encodingNodeListlist.getLength() == 0) {
			throw new JWNLException("JWNL_EXCEPTION_011");
		}
		return encodingNodeListlist.item(0).getAttributes().getNamedItem(VALUE_ATTRIBUTE).getNodeValue();
	}
	
	/**
	 * This method tells if it's using EuroWordN or WordNet 
	 * @return true if you are using EuroWordN or WordNet
	 */
	public static boolean isEWN(){
		return isEWN;
	}
	
	/**
	 * a copy of the one present in the original JWNL file
	 * return the value associated to the selected attribute of the selected node
	 * @param node the selected node
	 * @param attributeName the selected attribute
	 * @return the value associated to the selected attribute of a particular node
	 */
	public static String getAttribute(Node node, String attributeName) {
		NamedNodeMap map = node.getAttributes();
		if (map != null) {
			Node n = map.getNamedItem(attributeName);
			if (n != null) {
				return n.getNodeValue();
			}
		}
		return null;
	}
	
	/**
	 * Thid method return the encoding specified in the xml property file
	 * @return encoding specified in the xml property file
	 */
	public static String getEncoding(){
		return _dictEncoding;
	}	

}
