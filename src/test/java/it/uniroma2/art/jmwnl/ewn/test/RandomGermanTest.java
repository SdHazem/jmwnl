

package it.uniroma2.art.jmwnl.ewn.test;

import it.uniroma2.art.jmwnl.ewn.JMWNL;

import java.io.File;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 */

public class RandomGermanTest {

	 private static final String USAGE = "java Examples <properties file>";
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		IndexWord seed;
		
		if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        String propsFile = args[0];
		
		try {
			JMWNL.initialize(new File(propsFile));
			seed = Dictionary.getInstance().getIndexWord(POS.NOUN, "haus");
			Synset synset = new RandomSynsetWalk().startRandomWalk(seed, 200, true);
			System.out.println("\nLast synset = "+synset.toString());
		} catch (Exception ex) {
	        ex.printStackTrace();
	        ex.getMessage();
	    } 
	}

}
