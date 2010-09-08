
package it.uniroma2.art.jmwnl.ewn.conv;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.util.ResourceBundleSet;

import it.uniroma2.art.jmwnl.ewn.JMWNL;

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
 * @author Alexandra Tudorache <tudorache@info.uniroma2.it>
 */
public class EWN2PrincetonFormatConverter {
	
	private static final String EUROWORDNET_DICT_RESOURCE = "EWN_Resource";

    private static final String USAGE = "java Examples <conversion_properties_file>";

    private static final String EWNSOURCEDIR_ATTRIBUTE = "sourcedir";
    private static final String EWNSORCEFILES_ATTRIBUTE = "sourcefiles";
    private static final String EWNVERBOSECONVERSION_ATTRIBUTE = "verboseconversion";
    private static final String EWNENCODING_ATTRIBUTE = "encoding";
    private static final String EWNDICTIONAYPATH_ATTRIBUTE = "dictionary_path";
    private static final String EWNINDEXPATH_ATTRIBUTE = "index_path";
    private static final String EWNLANGUAGE_R_ATTRIBUTE = "language_resource";
    private static final String EWNLANGUAGE_P_ATTRIBUTE = "language_properties";
    private static final String EWNPARAMS_TAG = "ewnparams";
    
    private String _ewnSourcePath;
    private String _ewnDestinationPath;
    private String _ewnSourceFiles;
    private boolean _ewnVerboseConversion;
    private String _ewnIndexPath;
    private String _ewnEncoding;
    private String _ewnLanguageR;
    private String _ewnLanguageP;
    
    /**
     * This method return the ewn source path
     * @return ewn source path
     */
    public String getEwnSourcePath() {
        return _ewnSourcePath;
    }

    /**
     * This method return the ewn destination path
     * @return ewn destination path
     */
    public String getEwndestinationPath() {
        return  _ewnDestinationPath;
    }
    
    /**
     * This method return the ewn source files
     * @return ewn source files
     */
    public String getEwnSourceFiles() {
        return _ewnSourceFiles;
    }
    
    /**
     * This method return the ewn source path
     * @return ewn source path
     */
    public boolean getEwnVerboseConversion() {
        return _ewnVerboseConversion;
    }
    
    /**
     * This method return the ewn index path
     * @return ewn index path
     */
    public String getEwnIndexPath() {
        return _ewnIndexPath;
    }
    
    /**
     * This method return the ewn encoding
     * @return ewn encoding
     */
    public String getEwnEncoding() {
        return _ewnEncoding;
    }
    
    /**
     * This method return the ewn language of the resource
     * @return ewn language of the resource
     */
    public String getEwnLanguageR() {
        return _ewnLanguageR;
    }

    /**
     * This method return the ewn language of the propery file
     * @return ewn language of the property file
     */
    public String getEwnLanguageP() {
        return _ewnLanguageP;
    }
    
    /**
     * This method prepare the structures that will be used during the 
     * conversion by reading the xml property file
     * @param fileProperties xml property file
     * @throws JWNLException
     */
    public void initializeConv(File fileProperties) throws JWNLException {
        
        try {
            
            readEWNParamsConv(new FileInputStream(fileProperties));
            
            //JWNL.initialize(new FileInputStream(fileProperties));
            
            
        } catch (Exception ex) {
            throw new JWNLException("JWNL_EXCEPTION_002", ex);
        }
    }
    
    /**
     * This method read and store the parameters from the xml property file 
     * @param propertiesStream InputStream of the xml property file
     * @throws JWNLException
     */
    private void readEWNParamsConv(InputStream propertiesStream) throws JWNLException{
                
        // parse the properties file
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.parse(propertiesStream);
            
        } catch (Exception ex) {
            throw new JWNLException("JWNL_EXCEPTION_002", ex);
        }

        //_initStage = INITIALIZED;
        
        // do this in a separate try/catch since parse can also throw an IOException
        try {
            propertiesStream.close();
        } catch (IOException ex) {
        }

        org.w3c.dom.Element rootConv = doc.getDocumentElement();
        
        //       parse ewnparams information
        NodeList ewnparamsNodes = rootConv.getElementsByTagName(EWNPARAMS_TAG);
        //isEWN=true;
        if (ewnparamsNodes.getLength()==0) 
        {
            throw new JWNLException("JWNL_EXCEPTION_009");
        }
        
        Node ewnparams = ewnparamsNodes.item(0);
        NodeList ewnNodeList = ewnparams.getChildNodes();
        for(int i=0; i<ewnNodeList.getLength(); ++i){
            Node nodeEwn = ewnNodeList.item(i);
            String attrName = JMWNL.getAttribute(nodeEwn, "name");
            String attrValue = JMWNL.getAttribute(nodeEwn, "value");
            if(attrName == null){
                continue;
            }
            if(attrName.compareTo(EWNSOURCEDIR_ATTRIBUTE) == 0){
                _ewnSourcePath = attrValue;
            }
            else if(attrName.compareTo(EWNSORCEFILES_ATTRIBUTE) == 0){
                _ewnSourceFiles = attrValue;
            }
            else if(attrName.compareTo(EWNVERBOSECONVERSION_ATTRIBUTE) == 0){
                _ewnVerboseConversion = Boolean.getBoolean(attrValue);
            }
            else if(attrName.compareTo(EWNDICTIONAYPATH_ATTRIBUTE) == 0){
                _ewnDestinationPath = attrValue;
            }
            else if(attrName.compareTo(EWNENCODING_ATTRIBUTE) == 0){
                _ewnEncoding = attrValue;
            }
            else if(attrName.compareTo(EWNLANGUAGE_R_ATTRIBUTE) == 0){
                _ewnLanguageR = attrValue;
            }
            else if(attrName.compareTo(EWNLANGUAGE_P_ATTRIBUTE) == 0){
                _ewnLanguageP = attrValue;
            }
            else if(attrName.compareTo(EWNINDEXPATH_ATTRIBUTE) == 0){
                _ewnIndexPath = attrValue;
            }
        }
        
    }
    
    /**
     * This method start the conversion from the EuroWordNet format to the WordNet one
     * @throws JWNLException
     */
    public void prepareEWNDictionaryData() throws JWNLException  {
        //prepare dictionary
        {
        	((ResourceBundleSet)JWNL.getResourceBundle()).setLocale(new Locale(getEwnLanguageP()));
        	((ResourceBundleSet)JWNL.getResourceBundle()).addResource(EUROWORDNET_DICT_RESOURCE);
        	
        	
            String EWNPath;
            if (_ewnDestinationPath.endsWith(System.getProperty("file.separator"))){
                EWNPath = _ewnDestinationPath;
            }
            else {
                EWNPath = _ewnDestinationPath + System.getProperty("file.separator");
            }
            new File(EWNPath).mkdir();
            ParseEWNFile ewnf= new ParseEWNFile();           
            ewnf.MapEWNFiles(_ewnSourcePath, _ewnDestinationPath, _ewnSourceFiles, _ewnVerboseConversion, _ewnEncoding);
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }
        
        String propsFile = args[0];
        try {
        	EWN2PrincetonFormatConverter ewnConv = new EWN2PrincetonFormatConverter();
            ewnConv.initializeConv(new File (propsFile));
            ewnConv.prepareEWNDictionaryData();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }     

    }

    
    
    
}
