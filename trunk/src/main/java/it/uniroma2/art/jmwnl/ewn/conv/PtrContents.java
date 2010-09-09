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
 * @author Alexandra Tudorache
 * 
 * this class contains information about content of pointers
 * and is used during conversion from Eurowordnet file format to Wordnet file format
 */
package it.uniroma2.art.jmwnl.ewn.conv;

public class PtrContents {
	private String word, sense, synsetReference, pointerSymbol, pos, source, newOffset;
	PtrContents(){
		synsetReference=" "; // the synset subject of the relation
		pointerSymbol= " "; // symbol of the pointer
		pos= " ";		// the pos of the target word
		source=" ";		// 0000
		word=" ";		// the target word of the relation
		sense= " ";		// the sense of the target word
		newOffset="00000000"; // offset of the target word (to be precise is the offset of the target synset) 
	}
	

	/**
	 * This method subject synset of the relation
	 * @return the subject synset of the relation
	 */
	public String getSynsetReference() {
        return synsetReference;
     }
	
	/**
	 * This method set the subject synset of the relation
	 * @param SynsetRef the subject synset of the relation
	 * @return the subject synset of the relation
	 */
	public String putSynsetReference(String SynsetRef ) {
		synsetReference=SynsetRef;
        return synsetReference;
     }
	
	/**
	 * This method get the symbol of the pointer
	 * @return the symbol of the pointer
	 */
	public String getPointerSymbol() {
        return pointerSymbol;
     }
	
	/**
	 * This method set he symbol of the pointer
	 * @param PointerSymbol the symbol of the pointer
	 * @return the symbol of the pointer
	 */
	public String putPointerSymbol(String PointerSymbol ) {
		pointerSymbol=PointerSymbol;
        return pointerSymbol;
     }
	
	/**
	 * This method get the pos of the target word of the pointer
	 * @return the pos of the target word of the pointer
	 */
	public String getPos() {
        return pos;
     }
	
	/**
	 * This method set the pos of the target word of the pointer
	 * @param POS the pos of the target word of the pointer
	 * @return the pos of the target word of the pointer
	 */
	public String putPos(String POS ) {
		pos=POS;
        return pos;
     }
	
	/**
	 * This method get the source of the relation (0000)
	 * @return the source of the relation (0000)
	 */
	public String getSource() {
        return source;
     }
	
	/**
	 * This method set the source of the relation (0000)
	 * @param the source of the relation (0000)
	 * @return the source of the relation (0000)
	 */
	public String putSource(String Source ) {
		source=Source;
        return source;
     }
	
	/**
	 * This method get the target word of the relation
	 * @return the target word of the relation
	 */
	public String getWord() {
        return word;
     }
	
	/**
	 * This method set the target word of the relation
	 * @param Word the target word of the relation
	 * @return the target word of the relation
	 */
	public String putWord(String Word) {
		word=Word;
        return word;
     }
	
	/**
	 * This method get the sense of the target word of the relation
	 * @return the sense of the target word of the relation
	 */
	public String getSense() {
        return sense;
     }
	
	/**
	 * This method set the sense of the target word of the relation
	 * @param Sense the sense of the target word of the relation
	 * @return the sense of the target word of the relation
	 */
	public String putSense(String Sense) {
		sense=Sense;
        return sense;
     }
	
	/**
	 * This method get the offset of the target word (which it's the offset of the synset in which the target word belongs to)
	 * @return the offset of the target word
	 */
	public String getNewOffset() {
        return newOffset;
     }
	
	/**
	 * this method set the offset of the target word (which it's the offset of the synset in which the target word belongs to)
	 * @param NewOffset the offset of the target word
	 * @return the offset of the target word
	 */
	public String putNewOffset(String NewOffset ) {
		newOffset=NewOffset;
        return newOffset;
     }
}
