package it.uniroma2.art.jmwnl.ewn.test;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.Synset;

/**
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 */

public class RandomSynsetWalk {

	/**
	 * This method start the random walk between (Euro)WordNet's synset
	 * @param firstIndexWord The starting IndexWord (not a single synset)
	 * @param numberOfSteps the number of steps the porgram will try to do
	 * @param verbose true if we want to print all the information during each hop
	 * @return the last synset or null if the random walk did not even started
	 */
	public Synset startRandomWalk(IndexWord firstIndexWord, int numberOfSteps, boolean verbose){
		int sensePos;
		Synset synset = null, lastSynset = null;
		int senseCount = firstIndexWord.getSenseCount();
		if(senseCount == 1)
			sensePos = 1;
		else if(senseCount > 1) {
			double intervalSize = 1.0 / senseCount;
			sensePos = (int) (Math.random() / intervalSize) + 1;
		}
		else{
			return null;
		}
		try {
			synset = firstIndexWord.getSense(sensePos);
			lastSynset = synset;
			for(int i=0; i<numberOfSteps; ++i){
				synset = getNextRandomStep(synset, verbose, i);
				if(synset== null){
					synset = lastSynset;
					break;
				}
				lastSynset = synset;
			}
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		return synset;
		
	}
	
	/**
	 * This method return the next synset in the random walk or null if the current synset has no pointers
	 * @param synset the current synset
	 * @param verbose true to print all the information about the synset and selected pointer
	 * @param hopNumber the number of hops done
	 * @return the next synset or null if there is non next synset
	 */
	public Synset getNextRandomStep(Synset synset, boolean verbose, int hopNumber){
		Synset nextSynset = null;
		try {
			Pointer []pointers = synset.getPointers();
			int pointersCount = pointers.length;
			Pointer selectedPointer;
			if(pointersCount == 1)
				selectedPointer = pointers[0];
			else if(pointersCount > 1){
				double intervalSize = 1.0 / pointersCount;
				selectedPointer = pointers[(int) (Math.random() / intervalSize)];
			}
			else
				return null;
			if(verbose){
				System.out.println(synset.toString());
				System.out.println("\thopNumber = "+hopNumber);
				System.out.println("\tsynsetOffset = "+synset.getOffset());
				System.out.println("\tpointersCount = "+pointersCount);
				System.out.println("\tpointerLabel = "+selectedPointer.getType().getLabel());
				System.out.println("\tword = "+selectedPointer.getTargetSynset().getWord(0).getLemma());
			}
			nextSynset = selectedPointer.getTargetSynset();
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nextSynset;
	}
}
