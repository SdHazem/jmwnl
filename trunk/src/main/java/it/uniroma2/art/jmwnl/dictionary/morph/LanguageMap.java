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

package it.uniroma2.art.jmwnl.dictionary.morph;

import java.util.HashMap;

import net.didion.jwnl.JWNLException;

public class LanguageMap {

	/**
	 * @param args
	 */
	private static HashMap <String, String> languageMap= new HashMap<String, String>();
	
	static {
	      languageMap.put("dk", "Danish");
	      languageMap.put("nl", "Dutch");
	      languageMap.put("en", "English");
	      languageMap.put("fi", "Finnish");
	      languageMap.put("fr", "French");
	      languageMap.put("de", "German");
	      languageMap.put("it", "Italian");
	      languageMap.put("kp", "Kp");
	      languageMap.put("no", "Norwegian");
	      languageMap.put("pt", "Portuguese");
	      languageMap.put("ru", "Russian");
	      languageMap.put("es", "Spanish");
	      languageMap.put("se", "Swedish");	      
	      languageMap.put("ro", "Romanian");
	}
	
	/**
	 * This method return the language from its iso rappresentation 
	 * @param isoLang
	 * @return
	 * @throws JWNLException
	 */
	public static String getLanguage(String isoLang) throws JWNLException {
	    String lang;			
		lang = languageMap.get(isoLang.trim());
		if(lang != null){
			return lang;
		}
		throw new JWNLException("Language: " + isoLang + " is not supported by this Morphological Analyzer");
	}
}
