/**
 * @author Alexandra Tudorache
 * 
 * this class contains information about content of synsets
 * and is used during conversion from Eurowordnet file format to Wordnet file format
 */

package it.uniroma2.art.jmwnl.ewn.conv;

public class SynsetContents {
		private String synsetReference, EqLinksWordnetOffset, lexFilenum, pos, word, gloss, pCnt, newSynset, newOffset, wCnt;
		
		
		SynsetContents(){
			synsetReference="";		// the id of the synset
			EqLinksWordnetOffset= "00000000"; 
			lexFilenum= "";			// the lex 
			pos="";					// the pos of the synset
			wCnt="";				// the number of the word in the synset
			pCnt="";				// the number of the relation that have this synset has their subject
			word= "";				// all the word+sense in the synset
			newSynset="";			// temp synset
			newOffset="";			// the offset of the synset
			gloss="";				// the gloss of the synset
		}
		
		/**
		 * This method get the id of the synset 
		 * @return the id of the synset 
		 */
		public String getSynsetReference() {
	        return synsetReference;
	     }
		
		/**
		 * This method set the id of the synset 
		 * @param SynsetRef the id of the synset 
		 * @return the id of the synset 
		 */
		public String putSynsetReference(String SynsetRef ) {
			synsetReference=SynsetRef;
	        return synsetReference;
	     }
		
		/**
		 * This method get the offest of the eqLink   
		 * @return the offest of the eqLink
		 */
		public String getEqLinksWordnetOffset() {
	        return EqLinksWordnetOffset;
	     }
		
		/**
		 * This method set the offest of the eqLink
		 * @param EQLinks_WordnetOffset the offest of the eqLink
		 * @return the offest of the eqLink
		 */
		public String putEqLinksWordnetOffset(String EQLinks_WordnetOffset ) {
			EqLinksWordnetOffset=EQLinks_WordnetOffset;
	        return EqLinksWordnetOffset;
	     }
		
		/**
		 * This method get the two digit decimal integer corresponding to the lexicographer file name
		 * @return the two digit decimal integer corresponding to the lexicographer file name
		 */
		public String getLexFilenum() {
	        return lexFilenum;
	     }
		
		/**
		 * This method set the two digit decimal integer corresponding to the lexicographer file name
		 * @param LexFilenum the two digit decimal integer corresponding to the lexicographer file name
		 * @return the two digit decimal integer corresponding to the lexicographer file name
		 */
		public String putLexFilenum(String LexFilenum) {
			lexFilenum=LexFilenum;
	        return lexFilenum;
	     }
		
		/**
		 * This method get the pos of the sysnset
		 * @return the pos of the sysnset
		 */
		public String getPos() {
	        return pos;
	     }
		
		/**
		 * This method set the pos of the sysnset
		 * @param Pos the pos of the sysnset
		 * @return the pos of the sysnset
		 */
		public String putPos(String Pos ) {
			pos=Pos;
	        return pos;
	     }
		
		/**
		 * This method get all the word+sense in the synset
		 * @return all the word+sense in the synset
		 */
		public String getWord() {
	        return word;
	     }
		
		/**
		 * This method set all the word+sense in the synset
		 * @param Word all the word+sense in the synset
		 * @return all the word+sense in the synset
		 */
		public String putWord(String Word) {
			word=Word;
	        return word;
	     }
		
		/**
		 * This methd get the gloss of the sysnset
		 * @return the gloss of the sysnset
		 */
		public String getGloss() {
	        return gloss;
	     }
		
		/**
		 * This method set the gloss of the sysnset
		 * @param Gloss the gloss of the sysnset
		 * @return the gloss of the sysnset
		 */
		public String putGloss(String Gloss) {
			gloss=Gloss;
	        return gloss;
	     }
		
		/**
		 * This method get the number of words in the sysnset
		 * @return the number of words in the sysnset
		 */
		public String getWCnt() {
	        return wCnt;
	     }
		
		/**
		 * This method set the number of words in the sysnset
		 * @param WCnt the number of words in the sysnset
		 * @return the number of words in the sysnset
		 */
		public String putWCnt(String WCnt) {
			wCnt=WCnt;
	        return wCnt;
	     }
		
		/**
		 * This method get the number of relations that have this synset as their subject
		 * @return the number of relations that have this synset as their subject
		 */
		public String getPCnt() {
	        return pCnt;
	     }
		
		/**
		 * This method set the number of relations that have this synset as their subject
		 * @param PCnt the number of relations that have this synset as their subject
		 * @return the number of relations that have this synset as their subject
		 */
		public String putPCnt(String PCnt) {
			pCnt=PCnt;
	        return pCnt;
	     }
		
		/**
		 * This method get the offset of the synset
		 * @return the offset of the synset
		 */
		public String getNewOffset() {
	        return newOffset;
	     }
		
		/**
		 * This method set the offset of the synset
		 * @param NewOffset the offset of the synset
		 * @return the offset of the synset
		 */
		public String putNewOffset(String NewOffset) {
			newOffset=NewOffset;
	        return newOffset;
	     }
		
		/**
		 * This method get a temp value associated with the synset
		 * @return a temp value associated with the synset
		 */
		public String getNewSynset() {
	        return newSynset;
	     }
		
		/**
		 * This method get a temp value associated with the synset
		 * @param NewSynset a temp value associated with the synset
		 * @return a temp value associated with the synset
		 */
		public String putNewSynset(String NewSynset) {
			newSynset=NewSynset;
	        return newSynset;
	     }
	}


