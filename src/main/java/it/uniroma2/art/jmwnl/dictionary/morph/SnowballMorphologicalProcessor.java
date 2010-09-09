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

package it.uniroma2.art.jmwnl.dictionary.morph;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.MorphologicalProcessor;
import net.didion.jwnl.util.factory.Param;
import net.sf.snowball.SnowballProgram;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;


/**
 * 
 * @author Alexandra Tudorache <tudorache@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * 
 * implementation of <code>MorphologicalProcessor</code> relying on Snowball algorithmic stemmer and Lucene indexes 
 * 
 */
public class SnowballMorphologicalProcessor implements MorphologicalProcessor {

	private List <String> wordList; 
	public TreeMap <String, String> stemMap= new TreeMap<String, String>();
	public static final String INDEX_PATH = "index_path";
	
	protected String indexPath;
	protected String lang;
	
	protected boolean dirExist = false;
	
	public SnowballMorphologicalProcessor(){}
	
	public SnowballMorphologicalProcessor(String indexPath) throws JWNLException {
		this.indexPath = indexPath;
	    this.lang = LanguageMap.getLanguage(JWNL.getVersion().getLocale().getLanguage().toString());
	}
	
	public Object create(Map params) throws JWNLException {
		Param param = (Param) params.get(INDEX_PATH);
	    if (param == null) {
	        throw new JWNLException(this.getClass().getName() + " needs the " + INDEX_PATH + " parameter to be properly activated");
	    }
	    return new SnowballMorphologicalProcessor(param.getValue());
	}
	
	

	/**
	 * Lookup the base form of a word. Given a lemma, finds the WordNet
	 * entry most like that lemma. This function returns the first base form
	 * found. Subsequent calls to this function with the same part-of-speech
	 * and word will return the same base form. To find another base form for
	 * the pos/word, call lookupNextBaseForm.
	 * @param pos the part-of-speech of the word to look up
	 * @param derivation the word to look up
	 * @return IndexWord the IndexWord found during lookup or null if an IndexWord is not found
	 */
	public IndexWord lookupBaseForm(POS pos, String derivation) throws JWNLException {
		if(!dirExist){
			File dir = new File(indexPath);
			if(!dir.isDirectory()){
				throw new JWNLException("JWNL_EXCEPTION_011");
			}
			dirExist = true;
		}
		
		// See if we've already looked this word up
		String lemma= "";
   	 
		String result = stemMap.get(derivation);
		
   	 	if(result != null) {
   	 		lemma = result;
		}
   	 	
   	 	//if we didn't already searched for this lemma before we get the first match available
   	 	else if (lemma.length()==0) {
			lemma=this.getFirstLemma(derivation, pos, indexPath, lang);
			stemMap.put(derivation, lemma);
		}
   	 	
		if (lemma.trim().length()>0)
		{
			return Dictionary.getInstance().getIndexWord(pos, lemma);
		}
		else return null;
	}

	/**
	 * 
	 */
	public List <String>lookupAllBaseForms(POS pos, String derivation) throws JWNLException {
		if(!dirExist){
			File dir = new File(indexPath);
			if(!dir.isDirectory()){
				throw new JWNLException("JWNL_EXCEPTION_011");
			}
			dirExist = true;
		}
		return this.getAllLemma(derivation, pos, indexPath, lang);
	}

	/**
	 * This method get the first lemma associated to a particular word
	 * @param derivation the word to which to associate a the first lemma
	 * @param pos the pos associated to the derivation
	 * @param indexDir the lucene index path
	 * @param language the language of the derivation word
	 * @return the first lemma associated to the desired word
	 */
	public String getFirstLemma(String derivation, POS pos, String indexDir, String language) 
	  {
		 String field="word";
		 String queryStr="", word="", stemWord="";
		 int currDiff, derivationLength, hitWordLength, newDiff;
		  try {
			  
			  
			  IndexReader reader = IndexReader.open(indexDir);
			  Searcher searcher = new IndexSearcher(reader);
			  Analyzer analyzer = new StandardAnalyzer();

			  derivation=derivation.replace("_"," ");
					 
			  
			  queryStr = stemWords(derivation, language);

			  
			  
			  //Analyzer analyzer = new StandardAnalyzer();
			  QueryParser parser = new QueryParser(field, analyzer);
			  
			  Query query = parser.parse(queryStr);
			  Hits hits = searcher.search(query);
			  if (hits.length()>0) word=hits.doc(1).get("word").trim();
			  else //fuzzy search
			  {
				  	queryStr = queryStr.replace("+", "");
				  	stemWord = queryStr.trim();
				  	queryStr=query.toString(field).replaceAll(" ", "* ")+"*";
				  	//queryStr=query.toString(field).replaceAll(" ", "~ ")+"~";
				  	
				  	//System.out.println("new queryStr "+queryStr);
					query = parser.parse(queryStr);
				    hits = searcher.search(query);
				    
				  	
					currDiff = 100; //a big number
					for(int i =0; i<hits.length(); ++i){
						if(pos.getKey().trim().compareTo(hits.doc(i).get("pos").trim())==0){
							
							String stemWordsHit = stemWords(hits.doc(i).get("word").trim(), language);
							if(stemWord.compareTo(stemWordsHit) == 0){
								derivationLength = derivation.length();
								hitWordLength = hits.doc(i).get("word").trim().length();
								newDiff = hitWordLength-derivationLength;
								if(newDiff<0) newDiff = -newDiff;
								if(currDiff > newDiff) {
									word=hits.doc(i).get("word").trim();
								}
							}
						}
					}
				}

		      //System.out.println("word= "+word);
		      stemMap.put(derivation, word);
		      reader.close();
		      return word;
		  }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	return word;
	    }
	  }
	
	private List <String>getAllLemma(String lemma, POS pos, String indexDir, String language) 
	  {
		  String field="word";
		  String queryStr="";
		  try {

			  if (wordList!=null) wordList.clear();
			  if (lemma.contains("_")) 
			  {
				  String[] lemmaStr;
				  lemmaStr=lemma.split("_");
				  for (int i=0;i<lemmaStr.length;i++)
				  {
					  queryStr+=lemmaStr[i]+"* ";
				  }
			  }
			  if (queryStr.length()>0)
			  {
				  IndexReader reader = IndexReader.open(indexDir);
				
				  Searcher searcher = new IndexSearcher(reader);
				  Analyzer analyzer = new SnowballAnalyzer(language);
				  QueryParser parser = new QueryParser(field, analyzer);
				  Query query = parser.parse(queryStr.trim());
				  Hits hits = searcher.search(query);
				  System.out.println(hits.doc(0).get("word"));
			      for (int i=0;i<hits.length();i++)
			      {
			          wordList.add(hits.doc(i).get("word"));
			          System.out.println(hits.doc(i).get("word"));
			      }
			      reader.close();
			  }
		      return wordList;
		  }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	return wordList;
	    }
	  }
	
	/**
	 * This method return the stemming of a word(s) according to a particular language
	 * @param words the word(s) to be stemmed
	 * @param language the language according to which the word(s) should be stemmed
	 * @return the stemming of the word(s)
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private String stemWords(String words, String language) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
		String stemWords = "";
		Class stemClass = Class.forName("net.sf.snowball.ext." +language + "Stemmer");
		SnowballProgram stemmer = (SnowballProgram) stemClass.newInstance();
		Method getStem=stemClass.getMethod("stem", null);
		
		
		
		String[] wordsArray=words.replace("_", " ").split(" ");
		
		for (int i=0;i<wordsArray.length;i++)
		  {
			  stemmer.setCurrent(wordsArray[i]);
			  getStem.invoke(stemmer, null);
			  stemWords += " "+stemmer.getCurrent();
		  }
		
		
		
		
		return stemWords.trim();
	}
}