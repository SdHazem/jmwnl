/**
 * Java Multi WordNet Library (JMWNL)
 * See the documentation of the original JWNL and of its JMWNL extension for
copyright information.
 * This class file is contributed by:
 * University of Roma Tor Vergata.
 * Portions created by University of Roma Tor Vergata are Copyright (C)
2008.
 * All Rights Reserved.
 */ 

/**
 * @author Alexandra Tudorache <tudorache@info.uniroma2.it>
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
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

public class JMWNL_EuroWordNet_Spanish_Test {

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
            new JMWNL_EuroWordNet_Spanish_Test().go();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }     

        Synset synset;
        POS pos = POS.getPOSForLabel("noun");  
    }
        
        
    
    private IndexWord APARTADO;
    private IndexWord ABADENGO;
    private IndexWord ABANDONO;
    private IndexWord TAREA;
    
    public JMWNL_EuroWordNet_Spanish_Test()
    {
    try {	
    	APARTADO = Dictionary.getInstance().getIndexWord(POS.NOUN, "apartado");
    	ABADENGO = Dictionary.getInstance().getIndexWord(POS.NOUN, "abadengo");
    	ABANDONO = Dictionary.getInstance().getIndexWord(POS.NOUN, "abandono");
    	TAREA = Dictionary.getInstance().getIndexWord(POS.NOUN, "tarea");

    } catch (Exception ex) {
        ex.printStackTrace();
        ex.getMessage();
    }  
    }

    public void go() throws JWNLException {
    	System.out.println("APARTADO.getLemma() = " + APARTADO.getLemma());
    	
    	demonstrateDirectHypernyms(APARTADO);
    	System.out.println();
    	demonstrateDirectHyponyms(APARTADO);
    	System.out.println();
    	
    	demonstrateDirectHypernyms(ABADENGO);
    	System.out.println();
    	demonstrateDirectHyponyms(ABADENGO);
    	System.out.println();
    	
    	demonstrateDirectHypernyms(ABANDONO);
    	System.out.println();
    	demonstrateDirectHyponyms(ABANDONO);
    	System.out.println();
    	
    	demonstrateDirectHypernyms(TAREA);
    	System.out.println();
    	demonstrateDirectHyponyms(TAREA);
    	System.out.println();
    	
    	
    	
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
    		
    		List hyps = PointerUtils.getInstance().getHypernymTree(word.getSense(i)).getAllMatches(new Ritorna());
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
    
    
}
