  /*
   * Author : Alexandra Tudorache
   *
   * this class uses lucene indexing engine to index lemmas in eurowordnet vocabularies
   * this is necessary since algorithmic stemming used for european languages
   * does not return pure lemmas but only their prefixes, so an unprecise match
   * is requested between stemmed words and the lemma vocabulary
   * 
  */
package it.uniroma2.art.jmwnl.idx;

import it.uniroma2.art.jmwnl.ewn.JMWNL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** 
 * @author Armando Stellato <stellato@info.uniroma2.it>
 * @author Alexandra Tudorache <tudorache@info.uniroma2.it>
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * 
 * this class uses lucene indexing engine to index lemmas in wordnet vocabularies
 * this index can then be used by Morphological Analyzers 
 */
public class LuceneIndexingUtility {
    
	private static final String USAGE = "java Examples <conversion_properties_file>";
	
	private static final String EWNENCODING_ATTRIBUTE = "encoding";
    private static final String EWNDICTIONAYPATH_ATTRIBUTE = "dictionary_path";
    private static final String EWNINDEXPATH_ATTRIBUTE = "index_path";
    private static final String EWNPARAMS_TAG = "ewnparams";
    
    
    private File _ewnDictionarynPath; // the path of the (Euro)WordNet index files
    private File _ewnIndexPath; // the path of the Lucene index files
    private String _ewnEncoding; // the encoding of the resource files
    
    private Date now;
	
    
    /**
     * This method does the actual indexing using Lucene
     * @throws IOException
     */
    private void index() throws IOException {
    	now = new Date();
    	String initIndex=DateFormat.getTimeInstance().format(now).toString();
    	System.out.println("Init indexing files "+initIndex);
        if (!_ewnDictionarynPath.exists() || !_ewnDictionarynPath.isDirectory()) {
            throw new IOException(_ewnDictionarynPath + " does not exist or is not a directory");
        }
        IndexWriter writer = new IndexWriter(_ewnIndexPath, new StandardAnalyzer(), true);
        indexDirectory(writer, _ewnDictionarynPath);
        writer.close();
        now = new Date();
        String endIndex=DateFormat.getTimeInstance().format(now).toString();
        System.out.println("Finish indexing files "+endIndex);
    }

    /**
     * This method read the xml property file and set the relative structure
     * @param propertiesStream the xml property file to read
     */
    private void readConfigXML(InputStream propertiesStream){
    	// parse the properties file
    	org.w3c.dom.Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.parse(propertiesStream);
            
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        // do this in a separate try/catch since parse can also throw an IOException
        try {
            propertiesStream.close();
        } catch (IOException ex) {
        }

        org.w3c.dom.Element rootConv = doc.getDocumentElement();
        
        //       parse ewnparams information
        NodeList ewnparamsNodes = rootConv.getElementsByTagName(EWNPARAMS_TAG);
        
        Node ewnparams = ewnparamsNodes.item(0);
        NodeList ewnNodeList = ewnparams.getChildNodes();
        for(int i=0; i<ewnNodeList.getLength(); ++i){
            Node nodeEwn = ewnNodeList.item(i);
            String attrName = JMWNL.getAttribute(nodeEwn, "name");
            String attrValue = JMWNL.getAttribute(nodeEwn, "value");
            if(attrName == null){
                continue;
            }
            if(attrName.compareTo(EWNDICTIONAYPATH_ATTRIBUTE) == 0){
                _ewnDictionarynPath = new File(attrValue);
            }
            else if(attrName.compareTo(EWNENCODING_ATTRIBUTE) == 0){
                _ewnEncoding = attrValue;
            }
            else if(attrName.compareTo(EWNINDEXPATH_ATTRIBUTE) == 0){
                _ewnIndexPath = new File(attrValue);
            }
        }
    }
    
    /**
     * This method index all the files containing WordNet indexes using Lucene 
     * @param writer the writer used by Lucene
     * @param dir the direcotry of the (Euro)WordNet index files
     * @throws IOException
     */
    private void indexDirectory(IndexWriter writer, File dir) throws IOException {
        File[] files = dir.listFiles();
        String pos="";
        for (int i=0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                indexDirectory(writer, f);  // recurse
            } else if (f.getName().contains("index.noun") || f.getName().contains("index.verb") || f.getName().contains("index.adj") || f.getName().contains("index.adv")) {
            	if (f.getName().contains("noun")) pos="n";
            	else if (f.getName().contains("verb")) pos="v";
            	else if (f.getName().contains("adj")) pos="a";
            	else if (f.getName().contains("adv")) pos="r";
                indexFile(writer, f, pos);
            }
        }
    }

    /**
     * This method index using Lucene the single (Euro)WordNet index file
     * @param writer the writer used by Lucene
     * @param f the file to be indexed
     * @param pos the pos of the word contained in the file to be indexed
     * @throws IOException
     */
    private void indexFile(IndexWriter writer, File f, String pos) throws IOException {
        System.out.println("Indexing " + f.getName()+" "+DateFormat.getTimeInstance().format(new Date()).toString());
        Document doc = new Document();
        
        String line = null;
       
		Charset charset = Charset.forName(_ewnEncoding);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),charset));
		
        pos=pos.trim();
        while ((line=reader.readLine()) != null) {
        	doc = new Document();
        	doc.add( new Field("word", line.split(" "+pos+" ")[0].trim(), Field.Store.YES, Field.Index.TOKENIZED));
        	doc.add( new Field("pos", pos, Field.Store.YES, Field.Index.TOKENIZED));
        	writer.addDocument(doc);
        }
    }
    
    public static void main(String args[]) throws IOException {
    	if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(-1);
    	}
    	LuceneIndexingUtility EWNIdx=new LuceneIndexingUtility();
    	EWNIdx.readConfigXML(new FileInputStream(args[0]));
        EWNIdx.index();
    }
}
