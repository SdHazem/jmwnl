  /*
   * Contributor(s): Armando Stellato stellato@info.uniroma2.it
   * Contributor(s): Alexandra Tudorache tudorache@info.uniroma2.it
  */

package it.uniroma2.art.jmwnl.ewn.test;

import it.uniroma2.art.jmwnl.dictionary.morph.SnowballMorphologicalProcessor;
import it.uniroma2.art.jmwnl.ewn.JMWNL;
import it.uniroma2.art.jmwnl.ewn.data.EWNPointerUtils;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.list.PointerTargetTreeNode;
import net.didion.jwnl.data.list.PointerTargetTreeNodeList;
import net.didion.jwnl.dictionary.Dictionary;

public class JMWNL_EuroWordNet_Italian_Test {

    private static final String USAGE = "java Examples <properties file>";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        String propsFile = args[0];
        try {
            // initialize JWNL (this must be done before JWNL can be used)
        	System.out.println(propsFile);
        	//InputStream is=new FileInputStream(propsFile);
        	JMWNL.initialize(new File(propsFile));
        	//JWNL.initialize(is);
        	
        	//TODO vedere riga sottostante
        	//if (!JMWNL.checkEWNIndex() && (Dictionary.getInstance().getMorphologicalProcessor().getClass() == SnowballMorphologicalProcessor.class)) JMWNL.prepareEWNDictionaryIndex();
            new JMWNL_EuroWordNet_Italian_Test().go();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }     

        Synset synset;
        POS pos = POS.getPOSForLabel("noun");  
    }
        
        
    
    private IndexWord CITTA;
    private IndexWord PACIFICO;
    private IndexWord CANE;
    private IndexWord BUONO;
    private IndexWord CATTIVO;
    private IndexWord RIMPROVERARE;
    private IndexWord WRONG_WORD;
     
    public JMWNL_EuroWordNet_Italian_Test()
    {
    try {	
    	BUONO = Dictionary.getInstance().lookupIndexWord(POS.ADJECTIVE, "buono");
    	PACIFICO = Dictionary.getInstance().lookupIndexWord(POS.ADJECTIVE, "pacifico");
       	RIMPROVERARE = Dictionary.getInstance().lookupIndexWord(POS.VERB, "rimproverare");
       	CANE = Dictionary.getInstance().getIndexWord(POS.NOUN, "cane");
       	WRONG_WORD = Dictionary.getInstance().lookupIndexWord(POS.NOUN, "russel"); //the correct word is russell - it is English so the stemmer is of no use
    	CITTA = Dictionary.getInstance().lookupIndexWord(POS.NOUN, "città"); // the correct word is città

    } catch (Exception ex) {
        ex.printStackTrace();
        ex.getMessage();
    }  
    }

    public void go() throws JWNLException {
    	System.out.println("CITTA.getLemma() = " + CITTA.getLemma());
    	
    	demonstrateDirectHypernyms(CANE);
    	System.out.println();
    	demonstrateDirectHyponyms(CANE);
    	System.out.println();
    	demonstrateXposSynonymyListOperation(PACIFICO);
    	System.out.println();
    	demonstrateNearAntonymyListOperation(PACIFICO);
    	System.out.println();
    	demonstrateIsCausedByOperation(BUONO);
    	System.out.println();
    	demonstrateDirectHypernyms(BUONO);
    	System.out.println();
    	demonstrateXposSynonymyListOperation(BUONO);
    	System.out.println();
    	demonstrateCauseOperation(RIMPROVERARE);
    	System.out.println();
    	demonstrateSnowballStemmer();
    	System.out.println();
    	demonstrateDirectHypernyms(CITTA);
    	System.out.println();
    	demonstrateHypernymTreeOperation(CITTA);
    	System.out.println();
    	demonstrateInstanceHyponyms(CITTA);
    	System.out.println();
    	
    	if (WRONG_WORD==null)
    	{
    		System.out.println("russel (correct word russell) cannot be stemmed with the Italian stemmer! In fact is an English name found in the Italian resource");
    	}
    	
    	
    	// da cancellare da qui ..
    	/*
    	System.out.println(EWNPointerTypes.HAS_HOLO_LOCATION.getKey());
    	Pointer[] pointers = CITTA.getSense(1).getPointers();
    	for (int i = 0; i < pointers.length && i<20; ++i) {
    		System.out.println(pointers[i].getType());
    	}
    	System.out.println("*************************"); 
    	List <PointerType>list = PointerType.getAllPointerTypes();
    	Iterator<PointerType> iter = list.iterator();
    	System.out.println("PointerType.getAllPointerTypes().size()="+PointerType.getAllPointerTypes().size());
    	while(iter.hasNext()){
    		PointerType pointerType = iter.next();
    		System.out.println(pointerType.getKey()+"\t"+pointerType.getLabel());
    	}*/
    	// .. a qui
    	
    }     
    
    
    private void demonstrateDirectHypernyms (IndexWord word) throws JWNLException {
        // Get all the hypernyms (parents) of the first sense of <var>word</var>
        PointerTargetNodeList hypernyms = PointerUtils.getInstance().getDirectHypernyms(word.getSense(1));
        System.out.println("Direct Hyperyms of \"" + word.getLemma() + "\":");
        hypernyms.print();
    }
    
    private void demonstrateDirectHyponyms (IndexWord word) throws JWNLException {
//   	 Get all the hyponyms (children) of the first sense of <var>word</var>
       PointerTargetNodeList hyponyms = PointerUtils.getInstance().getDirectHyponyms(word.getSense(1));
       System.out.println("Direct Hyponyms of \"" + word.getLemma() + "\":");
       hyponyms.print();
    }
    
    private void demonstrateInstanceHyponyms (IndexWord word) throws JWNLException {
    	//Get all the hyponyms (children) of the first sense of <var>word</var>
    	PointerTargetNodeList inst_hyponyms = EWNPointerUtils.getInstance().getInstanceHyponyms(word.getSense(1));
    	System.out.println("Instance Hyponyms of \"" + word.getLemma() + "\":");
    	inst_hyponyms.print();
    }
    
    private void demonstrateCauseOperation(IndexWord word) throws JWNLException {
        PointerTargetNodeList cause = PointerUtils.getInstance().getCauses(word.getSense(1));
        System.out.println("Cause of \"" + word.getLemma() + "\":");
        cause.print();
    }
    
    private void demonstrateIsCausedByOperation(IndexWord word) throws JWNLException {
        PointerTargetNodeList iscaused = EWNPointerUtils.getInstance().getIsCausedBy(word.getSense(1));
        System.out.println("Cause of \"" + word.getLemma() + "\":");
        iscaused.print();
    }
    
    private void demonstrateNearAntonymyListOperation(IndexWord word) throws JWNLException {
        // Get all of the near antonyms of the first sense of <var>word</var>
    	System.out.println("Direct near antonyms of \"" + word.getLemma() + "\":");
        PointerTargetNodeList nearAntonyms = PointerUtils.getInstance().getAntonyms(word.getSense(1));
        nearAntonyms.print();
    }
    
    
    private void demonstrateSynonymyListOperation(IndexWord word) throws JWNLException {
        // Get all of the near synonyms of the first sense of <var>word</var>
    	System.out.println("Direct synonyms of \"" + word.getLemma() + "\":");
        PointerTargetNodeList synonyms = PointerUtils.getInstance().getSynonyms(word.getSense(1));
        
        synonyms.print();
    }
    
    private void demonstrateXposSynonymyListOperation(IndexWord word) throws JWNLException {
        // Get all of the near synonyms of the first sense of <var>word</var>
    	System.out.println("Direct xpos synonyms of \"" + word.getLemma() + "\":");
        PointerTargetNodeList xposSynonyms = EWNPointerUtils.getInstance().getXposSynonyms(word.getSense(1));
        
        xposSynonyms.print();
    }
    
    private void demonstrateTreeOperation(IndexWord word) throws JWNLException {
        // Get all the hyponyms (children) of the first sense of <var>word</var>
        PointerTargetTree hyponyms = PointerUtils.getInstance().getHyponymTree(word.getSense(1));
        System.out.println("HyponymsTree of \"" + word.getLemma() + "\":");
        hyponyms.print();
    }

    private class Stampanodi implements PointerTargetTreeNodeList.Operation
    {	
      public Object execute(PointerTargetTreeNode node)
      {
        return Long.toString(node.getSynset().getOffset());	//getOffset ereditato da PointerTargetNode
      } 	
    }

    private class Ritorna implements PointerTargetTreeNodeList.Operation
    {	
      public Object execute(PointerTargetTreeNode node)
      {
        return node;	//getOffset ereditato da PointerTargetNode
      } 	
    }
    
    
    private void demonstrateHypernymTreeOperation(IndexWord word) throws JWNLException {
        // Get all the hyponyms (children) of the first sense of <var>word</var>
    	for (int i=1;i<=word.getSenseCount();i++)
    	{
    		PointerTargetTree hypernyms = PointerUtils.getInstance().getHypernymTree(word.getSense(i));
    		System.out.println("HypernymTree of \"" + word.getLemma() + "\":");
    		hypernyms.print();
    		
    		List hyps = PointerUtils.getInstance().getHypernymTree(word.getSense(i)).getAllMatches(new JMWNL_EuroWordNet_Italian_Test.Ritorna());
    		System.out.println("Lista Ipernimi usuale");
    		ListIterator hypsIterator = hyps.listIterator();
    		while (hypsIterator.hasNext()) {
    			System.out.println(hypsIterator.next() + " ");
    		}
    		hyps = PointerUtils.getInstance().getHypernymTree(word.getSense(i)).getRootNode().getChildTreeList().getAllMatches(new Stampanodi());
    		System.out.println("Lista Ipernimi nuova");
    		hypsIterator = hyps.listIterator();
    		while (hypsIterator.hasNext()) {
    			System.out.println(hypsIterator.next() + " ");
    		}
    		
    	}
    	
    }

    /*
    private void demonstrateInstanceHyponymTreeOperation(IndexWord word) throws JWNLException {
        // Get all the hyponyms (children) of the first sense of <var>word</var>
    	for (int i=1;i<=word.getSenseCount();i++)
    	{
    		PointerTargetTree hyponyms = PointerUtils.getInstance().getInstanceHyponymTree(word.getSense(i));
    		System.out.println("HyponymTree of \"" + word.getLemma() + "\":");
    		hyponyms.print();
    		List hyps = PointerUtils.getInstance().getInstanceHyponymTree(word.getSense(i)).getAllMatches(new Stampanodi());
    		System.out.println("Lista Instance Iponimi usuale");
    		ListIterator hypsIterator = hyps.listIterator();
    		while (hypsIterator.hasNext()) {
    			System.out.println(hypsIterator.next() + " ");
    		}
    		hyps = PointerUtils.getInstance().getInstanceHyponymTree(word.getSense(i)).getRootNode().getChildTreeList().getAllMatches(new Stampanodi());
    		System.out.println("Lista Instance Iponimi nuova");
    		hypsIterator = hyps.listIterator();
    		while (hypsIterator.hasNext()) {
    			System.out.println(hypsIterator.next() + " ");
    		}
    		
    	}
    }
    */
    private void demonstrateSnowballStemmer() throws JWNLException
    {
    	//demonstrate snowball stemmer for Italian Language
    	//CATTIVO = Dictionary.getInstance().lookupIndexWord(POS.ADJECTIVE, "cattiva");
    	CATTIVO = Dictionary.getInstance().lookupIndexWord(POS.ADJECTIVE, "cattivi");
    	//System.out.println("Lemma of 'Cattiva' "+CATTIVO.getLemma());
    }
    
}
