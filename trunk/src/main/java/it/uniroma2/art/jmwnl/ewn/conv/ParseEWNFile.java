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
 * this class converts eurowordnet files to wordnet format
 *
 */
package it.uniroma2.art.jmwnl.ewn.conv;

//import it.uniroma2.art.jmwnl.ewn.JMWNL;

import it.uniroma2.art.jmwnl.ewn.data.EWNPointerTypes;

import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.*;
import java.io.*;

//import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.JWNLRuntimeException;
import net.didion.jwnl.data.PointerType;

import java.util.Date;

import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.Charset;


/**
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
 * @author Alexandra Tudorache <tudorache@info.uniroma2.it>
 */
public class ParseEWNFile {
	private String EWNTmpFileList="";
	private Date now;
	
	private TreeMap<String, ArrayList<String>> nounWordIndex=new TreeMap<String, ArrayList<String>>();
	private TreeMap<String, ArrayList<String>> verbWordIndex=new TreeMap<String, ArrayList<String>>();
	private TreeMap<String, ArrayList<String>> adjectiveWordIndex=new TreeMap<String, ArrayList<String>>();
	private TreeMap<String, ArrayList<String>> adverbWordIndex=new TreeMap<String, ArrayList<String>>();
	
	private TreeMap<String, SynsetContents> nounMap=new TreeMap<String, SynsetContents>();
	private TreeMap<String, SynsetContents> verbMap=new TreeMap<String, SynsetContents>();
	private TreeMap<String, SynsetContents> adverbMap=new TreeMap<String, SynsetContents>();
	private TreeMap<String, SynsetContents> adjectiveMap=new TreeMap<String, SynsetContents>();
	
	private TreeMap<String, String> newOffsetsMap=new TreeMap<String, String>(); //<word + sense + pos, offset>
	private TreeMap<String, ArrayList<PtrContents>> pointerMap=new TreeMap<String, ArrayList<PtrContents>>();
	
	private HashMap<String, String> nounRelationsMap=new HashMap<String, String>();
	private HashMap<String, String> adverbRelationsMap=new HashMap<String, String>();
	private HashMap<String, String> verbRelationsMap=new HashMap<String, String>();
	private HashMap<String, String> adjectiveRelationsMap=new HashMap<String, String>();

	private HashMap<String, String> symbolsMap=new HashMap<String, String>();
	String verbFrame=" 00 + 00 00";//significa che non esistono frames
	//private GetLang getLang=new GetLang();

	static final Comparator<PtrContents> WORD_SENSE_ORDER =
        new Comparator<PtrContents>() {
		public int compare(PtrContents ptr1, PtrContents ptr2) {
			String s1=ptr1.getWord()+" "+ptr1.getSense(),s2=ptr2.getWord()+" "+ptr2.getSense();
		return s1.compareTo(s2);
		}
	};
	
	public ParseEWNFile()
	{
	}
	
	
	/**
	 * This method is the only access point to the conversion from the EuroWorndNet format to the WordNet one
	 * @param ewnsourcePath the source path
	 * @param EWNDestPath the destination path
	 * @param EWNFileList a string that conteins all the files that will be converted
	 * @param calcRelations true if you want to view all the realtions present, false otherwise
	 * @param encoding the encoding of the resource
	 */
	public void MapEWNFiles(String ewnsourcePath, String EWNDestPath, String EWNFileList, boolean calcRelations, String encoding)
	{
		this.buidSymbolsMap();
		this.prepareFiles(ewnsourcePath, EWNDestPath, EWNFileList, encoding);
		this.generateFiles(EWNDestPath, calcRelations, encoding);
		this.deleteAuxFiles(EWNDestPath);
		
	}

	/**
	 * This method prepare a copy of the original files
	 * @param ewnsourcePath the source path
	 * @param ewnDestPath the destination path
	 * @param ewnFileList a string rappresenting the original files to be copied
	 * @param encoding the encoding of the files
	 */
	private void prepareFiles(String ewnsourcePath, String ewnDestPath, String ewnFileList, String encoding)
	{
		String src ="", dest = "";
		try {
			String[] EWNFilesList=ewnFileList.split(";");
		    /*
			Map map = Charset.availableCharsets();
		    Iterator it = map.keySet().iterator();
		    while (it.hasNext()) {
		        // Get charset name
		        String charsetName = (String)it.next();
		    
		        // Get charset
		        Charset charset = Charset.forName(charsetName);
		    }
*/
			
			for (int i=0; i<EWNFilesList.length;i++)
			{
				//prepare files
				src = ewnsourcePath+EWNFilesList[i];
				dest = ewnDestPath+EWNFilesList[i]+"_copy";
				
				//Charset charset = Charset.forName(JMWNL.getEncoding());
				Charset charset = Charset.forName(encoding);
				FileInputStream fis = new FileInputStream(src);
				InputStreamReader isr=new InputStreamReader(fis, charset);
				BufferedReader reader = new BufferedReader(isr);
				FileOutputStream fos = new FileOutputStream(dest);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, charset));
		        String line;   
		        while ((line = reader.readLine()) != null)
		        {
		            out.append(line + "\n");
		        }
		        out.write("\n0 @ \n");
				out.close();
				
				if (i<EWNFilesList.length-1) EWNTmpFileList+=dest+";";
				else EWNTmpFileList+=dest;
				
			}
		}
		catch (Exception e)
		{
			System.out.println("Caught: Prepare Files "+e.getMessage());
		}
	}
	
	/**
	 * this method deleta the temp files used during the conversion
	 * @param EWNDestPath
	 */
	private void deleteAuxFiles(String EWNDestPath)
	{
		try {

			//delete temp files
	    	String[] EWNFilesList=EWNTmpFileList.split(";");
			for (int i=0; i<EWNFilesList.length;i++)
			{
				File f = new File(EWNFilesList[i]);
				f.delete();
			}
			
		}
		catch (Exception e)
		{
			System.out.println("Caught: Delete Files "+e.getMessage());
		}
	}
	
	/**
	 * This method perform the actual conversion by calling the various method thar do the conversion
	 * @param EWNDestPath the destination path
	 * @param calcRelations true if you want to see all the relation during the conversion
	 * @param encoding the encoding of the resource
	 */
	private void generateFiles(String EWNDestPath, boolean calcRelations, String encoding){
    	try {
    		//calculate time elapsed for every operation
    		//gets a list of files to be parsed
	    	String[] EWNFilesList=EWNTmpFileList.split(";");
	    	now = new Date();
			System.out.println("Init parse files ");
			String initParseFiles=DateFormat.getTimeInstance().format(now).toString();
			for (int i=0; i<EWNFilesList.length;i++)
			{
    			
		    	this.parseFile(EWNFilesList[i].toString(), calcRelations, encoding);
			}
    		
	    	//calculates offsets for the new files
	    	now = new Date();
	    	System.out.println("Init calc noun offsets");
	    	String initCalcNounOffsets=DateFormat.getTimeInstance().format(now);
	    	this.calculateSynsetOffsets("noun");
	    	
	    	now = new Date();
	    	System.out.println("Init calc verb offsets");
	    	String initCalcVerbOffsets=DateFormat.getTimeInstance().format(now);
	    	this.calculateSynsetOffsets("verb");
	    	
	    	now = new Date();
	    	System.out.println("Init calc adjective offsets");
	    	String initCalcAdjectiveOffsets=DateFormat.getTimeInstance().format(now);
	    	this.calculateSynsetOffsets("adjective");
	    	
	    	now = new Date();
	    	System.out.println("Init calc adverb offsets");
	    	String initCalcAdverbOffsets=DateFormat.getTimeInstance().format(now);
	    	this.calculateSynsetOffsets("adverb");
	    	
	    	now = new Date();
	    	System.out.println("Init replace offsets");
	    	String initReplaceOffsets=DateFormat.getTimeInstance().format(now);
	    	this.replaceOffsets();
	    	
	    	now = new Date();
	    	System.out.println("Init write noun dat file");
	    	String initWriteNounDatFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteDatFile("noun", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write verb dat file");
	    	String initWriteVerbDatFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteDatFile("verb", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write adjective dat file");
	    	String initWriteAdjectiveDatFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteDatFile("adjective", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write adverb dat file");
	    	String initWriteAdverbDatFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteDatFile("adverb", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write noun index file");
	    	String initWriteNounIndexFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteIndexFile("noun", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write verb index file");
	    	String initWriteVerbIndexFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteIndexFile("verb", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write adjective index file");
	    	String initWriteAdjectiveIndexFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteIndexFile("adjective", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write adverb index file");
	    	String initWriteAdverbIndexFile=DateFormat.getTimeInstance().format(now);
	    	this.WriteIndexFile("adverb", EWNDestPath, encoding);
	    	
	    	now = new Date();
	    	System.out.println("Init write Exc file");
	    	String initWriteExcFiles=DateFormat.getTimeInstance().format(now);
	    	this.WriteExcFiles(EWNDestPath);
	    	
	    	now = new Date();
	    	System.out.println("Init get relations");
	    	String initGetRelations=DateFormat.getTimeInstance().format(now);
	    	if (calcRelations) this.getRelations();
	    	
	    	now = new Date();
	    	
	    	nounMap.clear();
	    	verbMap.clear();
	    	adverbMap.clear();
	    	adjectiveMap.clear();
	    	pointerMap.clear();
	    	nounRelationsMap.clear();
	    	adverbRelationsMap.clear();
	    	verbRelationsMap.clear();
	    	adjectiveRelationsMap.clear();
	    	symbolsMap.clear();
	    	
	    	String endProgram=DateFormat.getTimeInstance().format(now);
	    	
			System.out.println("initParseFiles " + initParseFiles);
	    	System.out.println("initCalcNounOffsets " + initCalcNounOffsets);
	    	System.out.println("initCalcVerbOffsets " + initCalcVerbOffsets);
	    	System.out.println("initCalcAdjectiveOffsets " + initCalcAdjectiveOffsets);
	    	System.out.println("initCalcAdverbOffsets " + initCalcAdverbOffsets);
	    	System.out.println("initReplaceOffsets " + initReplaceOffsets);
	    	System.out.println("initWriteDatFile " + initWriteNounDatFile);
	    	System.out.println("initWriteDatFile " + initWriteVerbDatFile);
	    	System.out.println("initWriteDatFile " + initWriteAdjectiveDatFile);
	    	System.out.println("initWriteDatFile " + initWriteAdverbDatFile);
	    	System.out.println("initWriteNounIndexFile " + initWriteNounIndexFile);
	    	System.out.println("initWriteVerbIndexFile " + initWriteVerbIndexFile);
	    	System.out.println("initWriteAdjectiveIndexFile " + initWriteAdjectiveIndexFile);
	    	System.out.println("initWriteadverbIndexFile " + initWriteAdverbIndexFile);
	    	System.out.println("initWriteExcFiles " + initWriteExcFiles);
	    	
	    	if (calcRelations) System.out.println("initGetRelations " + initGetRelations);
	    	
	    	System.out.println("Finish generating files " + endProgram);
    	}
		catch (Exception e)
		{
			System.out.println("Caught: Generate Files "+e.getMessage());
		}
	}
	
	/**
	 * This method prepares the map containing all the relations and their associated symbols
	 */
	private void buidSymbolsMap()
	{
		try {
		
			//List <PointerType>ls=PointerType.getAllPointerTypes();
		    //ListIterator <PointerType>listItr = ls.listIterator();
			List <PointerType>ls=EWNPointerTypes.getAllPointerTypes();
		    ListIterator <PointerType>listItr = ls.listIterator();
		    while(listItr.hasNext()) {
		    	PointerType currPointer = listItr.next();
			    //System.out.println(currPointer.getLabel().trim().replace(" ", "_")+" -> "+currPointer.getKey().trim());
			    if (!symbolsMap.containsKey(currPointer.getLabel().trim().replace(" ", "_")))
			    {
			    	symbolsMap.put(currPointer.getLabel().trim().replace(" ", "_"), currPointer.getKey().trim());
			    }
		    }
		}
		catch (Exception e)
		{
			System.out.println("Caught: Generate Files "+e.getMessage());
		}
	}//end function
	
	/**
	 * This method parse a single file
	 * @param EWNfile The file to be parsed
	 * @param calculateRelations true if you want to see all the relation during the conversion
	 * @param encoding the encoding of the file to be parsed
	 */
	private void parseFile(String EWNfile, boolean calculateRelations, String encoding){
		int lineno=0,synsetCount=0;
		try {
	    	String strLine="", synsetStr="", level1Str="", variantsToken="";
		    int i=0;
		    String[] sysnsetReference, level1, posSpeechTokens, literalsTokens, variantsTokens, literalsSubTokens, sense;
	    	int j=0, literalsCount=0,relationCount=0;
	    	String eqLinks_Wordnet_OffsetStr="", posSpeechToken="", synsetWord="";//synsetOut="",
	    	String pointer_symbol="";
	    	String p_cnt="", literalsToken="";
			String[] example, externalInfoTokens;//, externalInfoToken;//, source_id_Token;
			String  externalInfoTokensStr="";
			String[] internalLinksTokens, relationTokens, relationToken, targetConceptToken, internalLinks_PartofSpeachTokens, internalLinks_LiteralTokens, internalLinks_LiteralStrTokens;
			String relationStr="", internalLinks_PartofSpeach="", internalLinks_PartofSpeachStr="",internalLinks_Literal="", internalLinks_LiteralStr="";
			String internalLinks_Literals ="", internalLinks_Sense ="";
			String[]internalLinksFeatures;
			String[] eQRelationToken, targetILITokens, targetILIToken, eqLinks_PartofSpeachTokens, eqLinks_Wordnet_OffsetTokens;
			String eqLinks_PartofSpeachStr="", wordCount="", word="";
			// unused information present in EurowordNet
			String exampleStr="", definitionStr="", gloss="";//, source_id="", text_key="", internalLinksFeature="",relationsOut="";
			//String[] status;
			//Charset charset = Charset.forName(JMWNL.getEncoding());
			Charset charset = Charset.forName(encoding);
			FileInputStream fis = new FileInputStream(EWNfile);
			BufferedReader raf = new BufferedReader(new InputStreamReader(fis, charset));
	
	
		    while ((strLine = raf.readLine()) != null)   
		    {
		    	//count the read lines
		    	lineno++;
		    	
		      if ((i>0 && strLine.contains("0 @")) )
		      {
		    	// get synset reference
	        	sysnsetReference=synsetStr.replace("\"", "").split("@");
	            level1=synsetStr.replace("\"", "").split("  1");
	
	            for (int Level1Count=1; Level1Count<level1.length; Level1Count++) 
	            {
	            	level1Str=level1[Level1Count];
	                level1Str.trim();
	                // gets the first level of information
	                if (level1Str.contains("PART_OF_SPEECH") && ! level1Str.contains("4") )
	                {
	                	//if part of speach gets the POS
	                	posSpeechTokens=level1Str.split("PART_OF_SPEECH");
	                	posSpeechToken=posSpeechTokens[1];
	                    //pos name changed from eurowordnet to wordnet
	            		if (posSpeechToken.contains("pn")) posSpeechToken="n";
	            		if (posSpeechToken.contains("b")) posSpeechToken="r";
	            		posSpeechToken=posSpeechToken.trim();
	                }
	                if (level1Str.contains("VARIANTS"))
	                {
	                	//gets the variants                	
	                	variantsTokens=level1Str.split("VARIANTS");
	                    for (int VariantsCount=1; VariantsCount<variantsTokens.length; VariantsCount++) 
	                    {
	                   
	                    	variantsToken=variantsTokens[VariantsCount];
	                    	
	                    	literalsTokens=variantsToken.split("2 LITERAL");
	                    	synsetWord="";
	                        for (literalsCount=0; literalsCount<literalsTokens.length; literalsCount++) 
	                        {
	                        		//gets the literals
	                        		literalsToken=literalsTokens[literalsCount];
	                        		literalsSubTokens=literalsToken.split("   3 ");
	                        		literalsSubTokens[0]=literalsSubTokens[0].trim();
	
	                        		if (literalsSubTokens[0].matches("TOP"))
	                        		{
	                        		//if is a TOP CONCEPT add some infomration as POS and CONCEPt in order to distinguish it from top as word
	                        			literalsSubTokens[0]=literalsSubTokens[0].trim().toLowerCase()+" "+posSpeechToken.trim()+" concept";
	                        			
	                        		}
	                        		literalsSubTokens[0]=literalsSubTokens[0].replace(" ", "_");
	                        		synsetWord+=" "+literalsSubTokens[0].trim().toLowerCase();
	                                for (int LiteralsSubTokenCount=0; LiteralsSubTokenCount<literalsSubTokens.length; LiteralsSubTokenCount++) 
	                                {
	                        		
	                        		//gets the info for each literal
	                            		String LiteralsSubToken=literalsSubTokens[LiteralsSubTokenCount];
	
		                                			if (LiteralsSubToken.contains("SENSE"))
		                                            {
		                                				sense=LiteralsSubToken.split("SENSE");
		                                				synsetWord+=" "+sense[1].trim();
		                                				
		                                				word = literalsSubTokens[0].trim().toLowerCase();
	                                					if (posSpeechToken.contains("n") ) {
	                                						ArrayList <String>synsetList = nounWordIndex.get(word);
	                                						if(synsetList == null){
	                                							synsetList = new ArrayList<String>();
	                                						}
	                                						synsetList.add(sysnsetReference[1].trim());
	                                						nounWordIndex.put(word, synsetList);
	                                					}
	                                					if (posSpeechToken.contains("v") ) {
	                                						ArrayList <String>synsetList = verbWordIndex.get(word);
	                                						if(synsetList == null){
	                                							synsetList = new ArrayList<String>();
	                                						}
	                                						synsetList.add(sysnsetReference[1].trim());
	                                						verbWordIndex.put(word, synsetList);
	                                					}
	                                		
	                                					if (posSpeechToken.contains("r") ) {
	                                						ArrayList <String>synsetList = adverbWordIndex.get(word);
	                                						if(synsetList == null){
	                                							synsetList = new ArrayList<String>();
	                                						}
	                                						synsetList.add(sysnsetReference[1].trim());
	                                						adverbWordIndex.put(word, synsetList);
	                                					}
	                                					
	                                					if (posSpeechToken.contains("a") ){
	                                						ArrayList <String>synsetList = adjectiveWordIndex.get(word);
	                                						if(synsetList == null){
	                                							synsetList = new ArrayList<String>();
	                                						}
	                                						synsetList.add(sysnsetReference[1].trim());
	                                						adjectiveWordIndex.put(word, synsetList);
	                                					}
		                                			}
		                                			
		                                			if (LiteralsSubToken.contains("DEFINITION"))
		                                            {
		                                				definitionStr=LiteralsSubToken.split("DEFINITION")[1];
		                                			}
		                                			
		                                			
		                                			
		                                			if (LiteralsSubToken.contains("STATUS"))
		                                            {
		                                				//status=LiteralsSubToken.split("STATUS");
		                                			}
		                                			if (LiteralsSubToken.contains("EXAMPLES"))
		                                            {
		                                				example=LiteralsSubToken.split("4 EXAMPLE");
		                                				
		                                				for (int ExampleCount=1; ExampleCount<example.length; ExampleCount++) 
		                                                {
		                                				exampleStr+=example[ExampleCount]+"; ";
		                                				}
		                                			}
		                                			
		                                			LiteralsSubToken.trim();
		                                			if (LiteralsSubToken.contains("EXTERNAL_INFO"))
		                                            {
		                                				externalInfoTokens=LiteralsSubToken.split("EXTERNAL_INFO");
		                                				if(externalInfoTokens.length>1) {
			                                				externalInfoTokensStr=externalInfoTokens[1];
			                                				//externalInfoToken=externalInfoTokensStr.split("5 TEXT_KEY");
			                                				//source_id_Token=externalInfoToken[0].split("4 SOURCE_ID");
			                                				//source_id=source_id_Token[1];
			                                				if(externalInfoTokensStr.contains("5 TEXT_KEY")) {
			                                					//text_key=externalInfoToken[1];
			                                				}
		                                				
		                                				}
		                                				j++;
		                                			}
	                                }//end for Literals Sub Tokens
	                            	
	                            }//end for Literals Tokens
	                    }//end for variant tokens
	                	
	                }// end if VARIANTS
	                
	    			if (level1Str.contains("INTERNAL_LINKS"))
	                {
	                	 //gets the internal links data
	                	internalLinksTokens=level1Str.split("INTERNAL_LINKS");
	                	relationTokens=internalLinksTokens[1].split("2 RELATION");
	                    for (relationCount=1; relationCount<relationTokens.length; relationCount++) 
	                    	{
	                			relationToken=relationTokens[relationCount].split("3 TARGET_CONCEPT");
	                    		relationStr=relationToken[0];
	                    		targetConceptToken=relationToken[1].split("   4 ");
	                    		//part of speech
	                    		internalLinks_PartofSpeach=targetConceptToken[1];
	                    		internalLinks_PartofSpeachTokens=internalLinks_PartofSpeach.split("PART_OF_SPEECH");
	                    		internalLinks_PartofSpeachStr=internalLinks_PartofSpeachTokens[1];
	                    		internalLinks_PartofSpeach.trim();
	                    		internalLinks_Literal=targetConceptToken[2];
	                    		internalLinks_LiteralTokens=internalLinks_Literal.split("LITERAL");
	                    		internalLinks_LiteralStr=internalLinks_LiteralTokens[1];
	                    		internalLinks_LiteralStr.trim();
	
	                    		// split by sense
	                    		internalLinks_LiteralStrTokens=internalLinks_LiteralStr.split("  5 SENSE");
	                    		
	                    		if (internalLinks_LiteralStr.contains("FEATURES"))
	                    		{
	                        		internalLinks_Literals =internalLinks_LiteralStrTokens[0];
	                    			if (internalLinks_Literals.matches("TOP"))
	                        		{
	                    				internalLinks_Literals=internalLinks_Literals.toLowerCase()+" "+posSpeechToken.trim()+" concept";
	                        		}
	                        		internalLinksFeatures = internalLinks_LiteralStrTokens[1].split("3 FEATURES");
	                        		internalLinks_Sense =internalLinksFeatures[0].trim().split(" ")[0];
	                        		//internalLinksFeature=internalLinksFeatures[1].trim();
	                        	}
	                    		else
	                    		{
	                    			internalLinks_Literals =internalLinks_LiteralStrTokens[0];
	                    			internalLinks_Literals=internalLinks_Literals.trim();
	                    			if (internalLinks_Literals.matches("TOP"))
	                        		{
	                    				internalLinks_Literals=internalLinks_Literals.toLowerCase()+" "+posSpeechToken.trim()+" concept";
	                        		}
	                    			internalLinks_Literals=internalLinks_Literals.replace(" ", "_").toLowerCase();
	                        		internalLinks_Sense =internalLinks_LiteralStrTokens[1].trim().split(" ")[0];
	                    		}
	                    	
	                    		//Iterator itSymbols;
	                            
	                     	    //creates the symbols treeMap
	                     	    //itSymbols = symbolsMap.entrySet().iterator();
	                    			
	                		    //while (itSymbols.hasNext()) {
	                		    	
	                		        //Map.Entry pairsSymbols = (Map.Entry)itSymbols.next();
	                    		if (relationStr.trim().matches("has_hyperonym")) pointer_symbol="@";
	            		        else if (relationStr.trim().matches("has_hyponym")) pointer_symbol="~";
	            		        else if (relationStr.trim().matches("is_derived_from")) 
	            		        {
	            		        	if (internalLinks_PartofSpeach.matches("n")) pointer_symbol="\\";
	            		        	else if (internalLinks_PartofSpeach.matches("v")) pointer_symbol="<";
	            		        	else if(symbolsMap.get(relationStr.trim()) != null){
	                		        	pointer_symbol = symbolsMap.get(relationStr.trim());
	                		        }    
	            		        }
	            		        else if (relationStr.trim().matches("has_holo_member")) pointer_symbol="%m";
	            		        else if (relationStr.trim().matches("has_holo_part")) pointer_symbol="%p";
	            		        else if (relationStr.trim().matches("has_holo_portion")) pointer_symbol="%s";
	            		        else if (relationStr.trim().matches("has_instance")) pointer_symbol="~i";
	            		        else if (relationStr.trim().matches("has_mero_member")) pointer_symbol="#m";
	            		        else if (relationStr.trim().matches("has_mero_part")) pointer_symbol="#p";
	            		        else if (relationStr.trim().matches("has_mero_portion")) pointer_symbol="#s";
	            		        else if (relationStr.trim().matches("has_xpos_hyperonym")) pointer_symbol="x@";
	            		        else if (relationStr.trim().matches("has_xpos_hyponym")) pointer_symbol="x~";
	            		        else if (relationStr.trim().matches("is_subevent_of")) pointer_symbol="*";
	            		        else if (relationStr.trim().matches("near_antonym")) pointer_symbol="!";
	            		        else if (relationStr.trim().matches("near_synonym")) pointer_symbol="&";
	            		        //else if (RelationStr.trim().matches("xpos_fuzzynym")) pointer_symbol="x+";
	            		        else if (relationStr.trim().matches("xpos_near_antonym")) pointer_symbol="x!";
	            		        else if (relationStr.trim().matches("xpos_near_synonym")) pointer_symbol="x&";
	            		        else if (relationStr.trim().matches("causes")) pointer_symbol=">";
	            		        else if(symbolsMap.get(relationStr.trim()) != null){
	            		        	pointer_symbol = symbolsMap.get(relationStr.trim());
	            		        }    
	                    		
	            		        
	            		         // TODO: validate the partial mapping of FUZZYNYM with DERIVATIONALLY RELATED FORM
	            		         /*if fuzzynym create a string with all variants
	            		         //stemm variants and internal link if is fyzzynym
	            		        else if (RelationStr.trim().matches("fuzzynym") || RelationStr.trim().matches("xpos_fuzzynym"))
	            		        	{
	            		        		 // and stem) pointer_symbol="+";
	            		        		String[] words=synsetWord.split(" ");
	            		        		for (i=0;i<words.length;i++)
	            		        		{
	            		        			if (!words[i].matches("[0-9]"))
	            		        				if (this.analyze(InternalLinks_Literals).matches(this.analyze(words[i])))
	            		        				{
	            		        					if (RelationStr.trim().matches("fuzzynym")) pointer_symbol="+";
	            		        					else pointer_symbol="x+";
	            		        				}
	            		        		}
	            		        		
	            		        		//get all literals
	            		        		//split
	            		        		//analyze
	            		        	}
	            		        	*/
	
	                		    //}
	                		    //  pos name changed from eurowordnet to wordnet
	                    		if (internalLinks_PartofSpeachStr.contains("pn")) internalLinks_PartofSpeachStr="n";
	                    		if (internalLinks_PartofSpeachStr.contains("b")) internalLinks_PartofSpeachStr="r";

	                    		PtrContents ptrContents = new PtrContents();
                        		ptrContents.putSynsetReference(sysnsetReference[1].trim());
                        		ptrContents.putPointerSymbol(pointer_symbol);
                        		

                        		ptrContents.putPos(internalLinks_PartofSpeachStr.trim());
                        		ptrContents.putSource("0000");
                        		ptrContents.putWord(internalLinks_Literals.trim().replace(" ", "_").toLowerCase());
                        		ptrContents.putSense(internalLinks_Sense.trim());
                        		//pointerMap.put(ptrContents, ptrContents.getSynsetReference());
                        		ArrayList<PtrContents> listPtrContents = null;
                        		if(pointerMap.containsKey(ptrContents.getSynsetReference())){
                        			listPtrContents = pointerMap.get(ptrContents.getSynsetReference());
                        		}
                        		else{
                        			listPtrContents = new ArrayList<PtrContents>();
                        		}
                        		listPtrContents.add(ptrContents);
                        		pointerMap.put(ptrContents.getSynsetReference(), listPtrContents);
                        		if (calculateRelations) 
	                 		 	{
		                 			 if (posSpeechToken.contains("n")) nounRelationsMap.put(relationStr,ptrContents.getSynsetReference());
		                 			 if (posSpeechToken.contains("v")) verbRelationsMap.put(relationStr,ptrContents.getSynsetReference());
		          			    	 if (posSpeechToken.contains("r")) adverbRelationsMap.put(relationStr,ptrContents.getSynsetReference());
		          			    	 if (posSpeechToken.contains("a")) adjectiveRelationsMap.put(relationStr,ptrContents.getSynsetReference());
	          					}
	                		} // end for (RelationCount=1; RelationCount<RelationTokens.length; RelationCount++) 
	                }// end if INTERNAL_LINKS
	
	                if (level1Str.contains("EQ_LINKS"))
	                {
	                	eQRelationToken=level1Str.split("EQ_RELATION");
						targetILITokens=eQRelationToken[1].split("3 TARGET_ILI");
		        		targetILIToken=targetILITokens[1].split("   4 ");
		        		
		        		//part of speech
		        		eqLinks_PartofSpeachTokens=targetILIToken[1].split("PART_OF_SPEECH");
		        		eqLinks_PartofSpeachStr=eqLinks_PartofSpeachTokens[1];
		        		eqLinks_PartofSpeachStr.trim();
		        		
		        		if (eqLinks_PartofSpeachTokens[1].contains("WORDNET_OFFSET"))
			        	{
			        		eqLinks_Wordnet_OffsetTokens=eqLinks_PartofSpeachTokens[1].split("WORDNET_OFFSET");
			        		eqLinks_Wordnet_OffsetStr=eqLinks_Wordnet_OffsetTokens[1];
			        		
			        		eqLinks_Wordnet_OffsetStr="0000".trim()+eqLinks_Wordnet_OffsetStr.trim();
			        		eqLinks_Wordnet_OffsetStr=eqLinks_Wordnet_OffsetStr.substring(eqLinks_Wordnet_OffsetStr.length()-8,eqLinks_Wordnet_OffsetStr.length());
			        	}    
	                }
	            }    // end for Level1Count 
	            
	            if (relationCount>0) relationCount--;
	            p_cnt="000"+relationCount;
	            p_cnt=p_cnt.substring(p_cnt.length()-3,p_cnt.length());
	            if(literalsCount > 0){
	            	literalsCount--;
	            }
	            wordCount=("00"+literalsCount);
	            wordCount=wordCount.substring(wordCount.length()-2, wordCount.length());
	            if(definitionStr.trim().length()>0) gloss+=definitionStr.trim().replace("\'", "_")+"; ";
	            if(exampleStr.trim().length()>0) gloss+=exampleStr.trim().replace("\'", "_");
	            if(gloss.trim().length()>0) gloss=gloss.substring(0, gloss.trim().length()-1);
	
	
				//insert into SynsetContents
				//synsetObject
	            SynsetContents synsetContents = new SynsetContents();
	            synsetContents.putSynsetReference(sysnsetReference[1].trim());
	            synsetContents.putEqLinksWordnetOffset(eqLinks_Wordnet_OffsetStr);
	            synsetContents.putLexFilenum("00");
	            synsetContents.putPos(posSpeechToken.trim());
	            synsetContents.putWCnt(wordCount);
	            synsetContents.putWord(synsetWord.toLowerCase());
	            synsetContents.putPCnt(p_cnt);
	            synsetContents.putGloss(gloss);
	            
	            if (posSpeechToken.contains("n")) nounMap.put(synsetContents.getSynsetReference(), synsetContents);
	            if (posSpeechToken.contains("v")) verbMap.put(synsetContents.getSynsetReference(), synsetContents);
	            if (posSpeechToken.contains("r")) adverbMap.put(synsetContents.getSynsetReference(), synsetContents);
	            if (posSpeechToken.contains("a")) adjectiveMap.put(synsetContents.getSynsetReference(), synsetContents);
				synsetWord="";
	    		eqLinks_Wordnet_OffsetStr="";
	    		posSpeechToken="";
	    		//relationsOut="";
	    		eqLinks_PartofSpeachStr="";
	    		wordCount="";
	    	    pointer_symbol="";
	    	    p_cnt="";
	    	    exampleStr="";
	    	    externalInfoTokensStr="";
	    	    //source_id="";
	    	    //text_key="";
	    	    gloss="";
	    	    relationStr="";
	    	    definitionStr="";
	    	    internalLinks_PartofSpeach="";
	    	    //internalLinksFeature="";
	    	    internalLinks_PartofSpeachStr="";
	    	    internalLinks_Literal="";
	    	    internalLinks_LiteralStr="";
	    		internalLinks_Literals ="";
	    		internalLinks_Sense ="";
	    		eqLinks_PartofSpeachStr="";
	    		wordCount="";
		    	synsetStr=strLine;
		    	synsetCount++;
		    	relationCount=0;
		    	//Integer.valueOf(synsetCount mod 100);
		    	//if (synsetCount.)
		    	
		      }// end while synset
		      
		      else
		      {
		    	  synsetStr=synsetStr + strLine;
		      }
		      i++;
	    	}//end while
		    fis.close();
    	}
		catch (Exception e)
	    {
	    	System.out.print("The line of parsed file : "+lineno);
	    	System.out.println("Caught: parse file "+e.getMessage());
	    }
    }//end function

	/**
	 * This method calculate the offset of the synset inside the data file
	 * @param fileType the data file to be analyzed
	 */
	private void calculateSynsetOffsets(String fileType)
	{
		
		try { 
			String pointerStr="";
	        String newsynset="", curroffset="00000000";
	        int newoffset=0;
	        Iterator <Map.Entry<String, SynsetContents>> itSynset = null;
	 	    
	 	    if (fileType.contains("noun")) itSynset = nounMap.entrySet().iterator();
			if (fileType.contains("verb")) itSynset = verbMap.entrySet().iterator();
	 	    if (fileType.contains("adverb")) itSynset = adverbMap.entrySet().iterator();
		    if (fileType.contains("adjective")) itSynset = adjectiveMap.entrySet().iterator();
			
		    while (itSynset.hasNext()) {
		    	Map.Entry<String, SynsetContents> pairsSynset = itSynset.next();
		        SynsetContents synsetContent = pairsSynset.getValue();
		        pointerStr="";    
		        
		        String wordsSenses[] = synsetContent.getWord().trim().split(" ");
		        for(int i=0; i<wordsSenses.length; i+=2){
    		    	if(wordsSenses.length == 1){
						 continue;
					}
    		    	String wordSensePos = wordsSenses[i]+" "+wordsSenses[i+1]+" "+synsetContent.getPos();
    		    	newOffsetsMap.put(wordSensePos, curroffset); 
		        }
		        
		        synsetContent.putNewOffset(curroffset);
		        
	 			//Iterator <Map.Entry<String, PtrContents>>itPtr = pointerMap.entrySet().iterator();
				ArrayList<PtrContents> listPtrContents = pointerMap.get(synsetContent.getSynsetReference());
				if(listPtrContents != null){
					for(int i=0; i<listPtrContents.size(); ++i){
						PtrContents ptrContents = listPtrContents.get(i);
						pointerStr+=ptrContents.getPointerSymbol().trim()+" "+ptrContents.getNewOffset().trim()+" ";
						pointerStr+=ptrContents.getPos().trim()+" "+ptrContents.getSource()+" ";
					}
				}
				
				newsynset="00000000"+" "+synsetContent.getLexFilenum().trim()+" ";
		        newsynset+=synsetContent.getPos().trim()+" "+synsetContent.getWCnt().trim()+" "+synsetContent.getWord().trim()+" ";
		        newsynset+=synsetContent.getPCnt()+" "+pointerStr;
		        if (fileType.contains("verb")) 
		        	newsynset+=verbFrame;
		        if(synsetContent.getGloss().trim().length()!=0) 
		        	newsynset+=" |"+" "+synsetContent.getGloss().trim();
		        newsynset+="  \n";
		         
		        newoffset=Integer.parseInt(curroffset);
		        newoffset+=newsynset.length();			         
		        curroffset="00000000"+newoffset;
		        curroffset=curroffset.substring(curroffset.length()-8, curroffset.length());
		        
         	}//end while itSynset
		}// end try
		catch (Exception e)
		{
			System.out.println("Caught: Calculate offsets "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method replace the offset the synset
	 */
	private void replaceOffsets ()
	{   
		
		try{
				
			ArrayList<TreeMap<String, SynsetContents>>listAllSynset = new ArrayList<TreeMap<String, SynsetContents>>();
			listAllSynset.add(nounMap);
			listAllSynset.add(verbMap);
			listAllSynset.add(adverbMap);
			listAllSynset.add(adjectiveMap);
			
			
			Iterator <Map.Entry<String, ArrayList<PtrContents>>> itPtr = pointerMap.entrySet().iterator();
			
			
			//create a TreeMap with the key "getWord() + " " getSense() + " " getPos()"
			TreeMap <String, ArrayList<PtrContents>> pointerMap2 = new TreeMap<String, ArrayList<PtrContents>>();
			while(itPtr.hasNext()){
				ArrayList<PtrContents> listPtrContents = itPtr.next().getValue(); 
				for(int i=0; i<listPtrContents.size(); ++i){
					PtrContents ptrContents =  listPtrContents.get(i);
					//String content = ptrContents.getWord()+" "+ptrContents.getSense();
					
					SynsetContents synsetContent = null;
					int k=0;
					while(synsetContent == null){
						synsetContent = listAllSynset.get(k++).get(ptrContents.getSynsetReference());
					}
					String wordsSenses[] = synsetContent.getWord().trim().split(" ");
					 for(int j=0; j<wordsSenses.length; j+=2){
						 if(wordsSenses.length == 1){
							 continue;
						}
						 String wordSensePos = wordsSenses[j]+" "+wordsSenses[j+1]+" "+synsetContent.getPos();
						 ArrayList <PtrContents>listPtrContents2 = null;
							if(pointerMap2.containsKey(wordSensePos)){
								listPtrContents2 = pointerMap2.get(wordSensePos);
							}
							else{
								listPtrContents2 = new ArrayList<PtrContents>();
							}
							listPtrContents2.add(ptrContents);
							pointerMap2.put(wordSensePos, listPtrContents2);
					 }
					
					
				}
			}
			
			
    		Iterator <Map.Entry<String, SynsetContents>>itNounMap = nounMap.entrySet().iterator();
		    while (itNounMap.hasNext()) {
    		    Map.Entry <String, SynsetContents>pairsSynset = itNounMap.next();
    		    SynsetContents synsetContent = pairsSynset.getValue();
    		    String wordsSenses[] = synsetContent.getWord().trim().split(" ");
    		    for(int i=0; i<wordsSenses.length; i+=2){
    		    	if(wordsSenses.length == 1){
						 continue;
					}
    		    	String wordSensePos = wordsSenses[i]+" "+wordsSenses[i+1] + " n";
    		    	ArrayList<PtrContents> listPtrContents = pointerMap2.get(wordSensePos);
    		    	if(listPtrContents == null){
    		    		continue;
    		    	}
    		    	for(int k=0; k<listPtrContents.size(); ++k){
    		    		String wordSensePosRel = listPtrContents.get(k).getWord()+" "+listPtrContents.get(k).getSense()+" "+
    		    				listPtrContents.get(k).getPos();
    		    		listPtrContents.get(k).putNewOffset(newOffsetsMap.get(wordSensePosRel));
    		    	}
    		    }
            } //end while nounMap
		    
    		Iterator <Map.Entry<String, SynsetContents>>itVerbMap = verbMap.entrySet().iterator();
		    while (itVerbMap.hasNext()) {
		        Map.Entry <String, SynsetContents>pairsSynset = itVerbMap.next();
    		    SynsetContents synsetContents = pairsSynset.getValue();
               	String wordsSenses[] = synsetContents.getWord().split(" ");
    		    for(int i=0; i<wordsSenses.length; i+=2){
    		    	if(wordsSenses.length == 1){
    		    		 continue;
					}
    		    	String wordSensePos = wordsSenses[i]+" "+wordsSenses[i+1]+ " v";
    		    	ArrayList<PtrContents> listPtrContents = pointerMap2.get(wordSensePos);
    		    	if(listPtrContents == null){
    		    		continue;
    		    	}
    		    	for(int k=0; k<listPtrContents.size(); ++k){
    		    		String wordSensePosRel = listPtrContents.get(k).getWord()+" "+listPtrContents.get(k).getSense()+" "+
	    						listPtrContents.get(k).getPos();
    		    		listPtrContents.get(k).putNewOffset(newOffsetsMap.get(wordSensePosRel));
    		    	}
    		    }
            } //end while VerbMap
		    
    		Iterator <Map.Entry<String, SynsetContents>>itAdjectiveMap = adjectiveMap.entrySet().iterator();
		    while (itAdjectiveMap.hasNext()) {
		        Map.Entry <String, SynsetContents>pairsSynset = itAdjectiveMap.next();
    		    SynsetContents synsetContents = pairsSynset.getValue();
               	String wordsSenses[] = synsetContents.getWord().split(" ");
    		    for(int i=0; i<wordsSenses.length; i+=2){
    		    	if(wordsSenses.length == 1){
   		    		 	continue;
					}
    		    	String wordSensePos = wordsSenses[i]+" "+wordsSenses[i+1]+ " a";
    		    	ArrayList<PtrContents> listPtrContents = pointerMap2.get(wordSensePos);
    		    	if(listPtrContents == null){ 
    		    		continue;
    		    	}
    		    	for(int k=0; k<listPtrContents.size(); ++k){
    		    		String wordSensePosRel = listPtrContents.get(k).getWord()+" "+listPtrContents.get(k).getSense()+" "+
	    						listPtrContents.get(k).getPos();
    		    		listPtrContents.get(k).putNewOffset(newOffsetsMap.get(wordSensePosRel));
    		    	}
    		    }
            } //end while AdjMap
		    
        	Iterator <Map.Entry<String, SynsetContents>>itAdverbMap = adverbMap.entrySet().iterator();
		    while (itAdverbMap.hasNext()) {
		        Map.Entry <String, SynsetContents>pairsSynset = itAdverbMap.next();
    		    SynsetContents synsetContents = pairsSynset.getValue();
               	String wordsSenses[] = synsetContents.getWord().split(" ");
    		    for(int i=0; i<wordsSenses.length; i+=2){
    		    	if(wordsSenses.length == 1){
   		    		 	continue;
					}
    		    	String wordSensePos = wordsSenses[i]+" "+wordsSenses[i+1]+ " r";
    		    	ArrayList<PtrContents> listPtrContents = pointerMap2.get(wordSensePos);
    		    	if(listPtrContents == null){
    		    		continue;
    		    	}
    		    	for(int k=0; k<listPtrContents.size(); ++k){
    		    		String wordSensePosRel = listPtrContents.get(k).getWord()+" "+listPtrContents.get(k).getSense()+" "+
	    						listPtrContents.get(k).getPos();
    		    		listPtrContents.get(k).putNewOffset(newOffsetsMap.get(wordSensePosRel));
    		    	}
    		    }
            } //end while AdvMap
		}
		catch (Exception e)
		{
			System.out.println("Caught: Replace offsets "+e.getMessage());
			e.printStackTrace();
		}
	}//end function
	
	/**
	 * This method write the data file
	 * @param fileType the type of the data file to be written (noun/verb/adverb/adjective)
	 * @param EWNDestPath the destination path
	 * @param encoding the encoding of the file to be written
	 */
	private void WriteDatFile(String fileType, String EWNDestPath, String encoding)
	{
		String newEWNDataFile="";
		String pointerContents="", newsynset="";
		
		Iterator <Map.Entry<String, SynsetContents>>itSynset = null;
		
		try {
			if (fileType.contains("noun")) 
			{
				newEWNDataFile=EWNDestPath+File.separatorChar+"data.noun";
			}
		    if (fileType.contains("verb")) 
	    	{
		    	newEWNDataFile=EWNDestPath+File.separatorChar+"data.verb";
	    	}
		    if (fileType.contains("adverb")) 
	    	{
		    	newEWNDataFile=EWNDestPath+File.separatorChar+"data.adv";
	    	}
		    if (fileType.contains("adjective")) 
		    {
		    	newEWNDataFile=EWNDestPath+File.separatorChar+"data.adj";
		    }
		    //Charset charset = Charset.forName(JMWNL.getEncoding());
		    Charset charset = Charset.forName(encoding);
		    FileOutputStream fos = new FileOutputStream(newEWNDataFile);
			BufferedWriter raf = new BufferedWriter(new OutputStreamWriter(fos, charset));
			
			if (fileType.contains("noun")) 
			{
				itSynset = nounMap.entrySet().iterator();
			}
		    if (fileType.contains("verb")) 
		    	{
		    	itSynset = verbMap.entrySet().iterator();
		    }
		    if (fileType.contains("adverb")) 
		    	{
		    	itSynset = adverbMap.entrySet().iterator();
		    }
		    if (fileType.contains("adjective")) 
		    {
		    	itSynset = adjectiveMap.entrySet().iterator();
		    }
			
		    while (itSynset.hasNext()) {
		        Map.Entry<String, SynsetContents> pairsIdSyn_SynCont = itSynset.next();
		        SynsetContents synsetContent = pairsIdSyn_SynCont.getValue();
		        
		        String tmpoffset = synsetContent.getNewOffset();
		        if (tmpoffset.trim().length()==0) 
		        	tmpoffset="00000000";
		        newsynset=tmpoffset+" "+synsetContent.getLexFilenum().trim()+" ";
		        String wcntHex = "0"+Integer.toHexString(Integer.parseInt(synsetContent.getWCnt().trim()));
		        newsynset+=synsetContent.getPos().trim()+" "+wcntHex.substring(wcntHex.length()-2, wcntHex.length())+" "+synsetContent.getWord().trim()+" ";
		        newsynset+=synsetContent.getPCnt().trim()+" ";
		        
		        pointerContents="";	          
	    	    
	    	    ArrayList <PtrContents>listPtrContents = pointerMap.get(synsetContent.getSynsetReference());
	    	    if(listPtrContents != null) {
	    	    	for(int i=0; i<listPtrContents.size(); ++i){
		    	    	PtrContents ptrContents = listPtrContents.get(i);
	    	    		pointerContents+=ptrContents.getPointerSymbol().trim()+" "+ptrContents.getNewOffset().trim()+" ";
	    	    		pointerContents+=ptrContents.getPos().trim()+" "+ptrContents.getSource().trim()+" ";
		    	    }
	    	    }
	    	    
	    	    
	    	    newsynset+=pointerContents;
	    	    if (fileType.contains("verb")) 
	    	    	newsynset+=verbFrame;
	    	    if(synsetContent.getGloss().trim().length()>0) 
	    	    	newsynset+=" |"+" "+synsetContent.getGloss().trim();
	    	    newsynset+="  \n";
	    	    raf.append(newsynset);
	    	}
			raf.close();
		}
		catch (Exception e)
		{
			System.out.println("Caught_: Write dat file "+e.getMessage());
			e.printStackTrace();
		}
	}//end function
	
	/**
	 * This method write the index file
	 * @param fileType fileType the type of the data file to be written (noun/verb/adverb/adjective)
	 * @param EWNDestPath EWNDestPath the destination path
	 * @param encoding the encoding of the file to be written
	 */
	private void WriteIndexFile(String fileType, String EWNDestPath, String encoding)
	{
		String NewEWNIndexFile="";
		int p_cnt=0, sense_cnt=0, synset_cnt=0, tagsense_cnt=0;;
		String synset_offset="", pos="", lemma="", ptr_symbol="", index_entry="";
		try {
			
			if (fileType.contains("noun")) 
				{
				NewEWNIndexFile=EWNDestPath+File.separatorChar+"index.noun";
				pos="n";
				}
		    if (fileType.contains("verb")) 
		    	{
		    	NewEWNIndexFile=EWNDestPath+File.separatorChar+"index.verb";
		    	pos="v";
		    	}
		    if (fileType.contains("adverb")) 
		    	{
		    	NewEWNIndexFile=EWNDestPath+File.separatorChar+"index.adv";
		    	pos="r";
		    	
		    	}
		    if (fileType.contains("adjective"))
		    	{
		    	NewEWNIndexFile=EWNDestPath+File.separatorChar+"index.adj";
		    	pos="a";
		    	}
			//Charset charset = Charset.forName(JMWNL.getEncoding());
			Charset charset = Charset.forName(encoding);
		    FileOutputStream fos = new FileOutputStream(NewEWNIndexFile);
			BufferedWriter raf = new BufferedWriter(new OutputStreamWriter(fos, charset));
			
			Iterator <Map.Entry<String, ArrayList<String>>> itwordIndex = null;
			if (fileType.contains("noun")) {
				itwordIndex= nounWordIndex.entrySet().iterator();
			}
		    if (fileType.contains("verb")) {
		    	itwordIndex = verbWordIndex.entrySet().iterator();
	    	}
		    if (fileType.contains("adverb")) {
		    	itwordIndex = adverbWordIndex.entrySet().iterator();
	    	}
		    if (fileType.contains("adjective")) {
		    	itwordIndex = adjectiveWordIndex.entrySet().iterator();
	    	}
			
		    while (itwordIndex.hasNext()) {
		    	
		        Map.Entry <String, ArrayList<String>> pairswordSynsets = itwordIndex.next();

		        synset_cnt=0;
	        	synset_offset="";
	        	ptr_symbol=" ";
	        	p_cnt=0;
	        	
		        lemma=pairswordSynsets.getKey().trim();
		        
		        ArrayList<String> listSynsets = pairswordSynsets.getValue();
		        
		        TreeMap<String, SynsetContents> mapIdS_SynCont = null;
			    if (fileType.contains("noun")) 
			    	mapIdS_SynCont = nounMap;
			    if (fileType.contains("verb")) 
			    	mapIdS_SynCont = verbMap;
			    if (fileType.contains("adverb")) 
			    	mapIdS_SynCont = adverbMap;
			    if (fileType.contains("adjective")) 
			    	mapIdS_SynCont = adjectiveMap;
			    
			    SynsetContents synsetContent =  null;
			    for(int i=0; i< listSynsets.size(); ++i){
			    	synsetContent = mapIdS_SynCont.get(listSynsets.get(i));
			    	synsetContent.getPCnt();
			    	ArrayList <PtrContents>ptrContents = pointerMap.get(listSynsets.get(i));
			    	synset_offset+=synsetContent.getNewOffset()+" ";
			    	if(ptrContents == null){
			    		continue;
			    	}
			    	for(int k=0; k<ptrContents.size(); ++k){
			    		if(!ptr_symbol.contains(" "+ptrContents.get(k).getPointerSymbol()+" ")){
			    			ptr_symbol+=ptrContents.get(k).getPointerSymbol()+" ";
			    			++p_cnt;
			    		}
			    	}
		        	//synset_offset+=synsetContent.getNewOffset()+" ";
			    }
		        pos=synsetContent.getPos();
		        synset_cnt =listSynsets.size();
		        
		        sense_cnt = listSynsets.size();
		        
		        
		        index_entry=lemma.trim()+" "+pos.trim()+" "+synset_cnt+" "+p_cnt+" "+ptr_symbol.trim()+" "+sense_cnt+" "+tagsense_cnt+" "+synset_offset.trim()+"  \n";			    
			    raf.append(index_entry);
		    }// end WordIterator
			raf.close();
		}// end try
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}//end function
	
	/**
	 * This method write the exex file
	 * @param EWNDestPath the destination path
	 */
	private void WriteExcFiles(String EWNDestPath)
	{
		String NewEWNNounExcFile=EWNDestPath+File.separatorChar+"noun.exc";
		String NewEWNAdvExcFile=EWNDestPath+File.separatorChar+"adv.exc" ;
		String NewEWNAdjExcFile=EWNDestPath+File.separatorChar+"adj.exc";
		String NewEWNVerbExcFile=EWNDestPath+File.separatorChar+"verb.exc" ;
		String NewEWNIndexSenseFile=EWNDestPath+File.separatorChar+"index.sense" ;
		String FileContents="";
		try {
			FileContents="/*Exc files are mantained for compatibility with */\n";
			FileContents+="*WordNet structure and JWNL. *\n";
			RandomAccessFile rafNoun = new RandomAccessFile(NewEWNNounExcFile, "rw");
			rafNoun.writeBytes(FileContents);
			rafNoun.close();
			RandomAccessFile rafAdv = new RandomAccessFile(NewEWNAdvExcFile, "rw");
			rafAdv.writeBytes(FileContents);
			rafAdv.close();
			RandomAccessFile rafAdj = new RandomAccessFile(NewEWNAdjExcFile, "rw");
			rafAdj.writeBytes(FileContents);
			rafAdj.close();
			RandomAccessFile rafVerb = new RandomAccessFile(NewEWNVerbExcFile, "rw");
			rafVerb.writeBytes(FileContents);
			rafVerb.close();
			RandomAccessFile rafSense = new RandomAccessFile(NewEWNIndexSenseFile, "rw");
			rafSense.writeBytes(FileContents);
			rafSense.close();
		}// end try
		
		catch (Exception e)
		{
			System.out.println("Caught: WriteExcFiles "+e.getMessage());
		}
	}//end function
	/*
	private void getObjects()
	{
		Iterator itNoun = nounMap.entrySet().iterator();
	    while (itNoun.hasNext()) {
	       Map.Entry pairsSynset = (Map.Entry)itNoun.next();
	       SynsetContents synsetContents = (SynsetContents)pairsSynset.getValue();
        	
    	   System.out.println(synsetContents.getEqLinksWordnetOffset()  + " ");
    	   System.out.println("gloss get objects" + synsetContents.getGloss()  + " " );
    	   System.out.println(synsetContents.getLexFilenum()  + " " );
    	   System.out.println(synsetContents.getPCnt()  + " " );
    	   System.out.println(synsetContents.getPos()  + " " );
    	   System.out.println(synsetContents.getSynsetReference()  + " ####" );
    	   System.out.println(synsetContents.getNewOffset()  + " " );
    	   System.out.println(synsetContents.getWCnt()  + " " );
    	   System.out.println("word " + synsetContents.getWord()  + " " );
        	          
    	   Iterator <Map.Entry<String, PtrContents>>itPtr = pointerMap.entrySet().iterator();
    	   while (itPtr.hasNext()) {
    		   Map.Entry <String, PtrContents>pairsPtr = (Map.Entry<String, PtrContents>)itPtr.next();
    		   PtrContents ptrContents = pointerMap.get(pairsPtr.getKey());
      		   if(ptrContents.getSynsetReference()==synsetContents.getSynsetReference())
      		   {
      			   System.out.println( ptrContents.getNewOffset()+" "+ptrContents.getSense() + " " +ptrContents.getPointerSymbol() + " " +ptrContents.getSource() + " "+ptrContents.getSynsetReference() + " @@@"+ptrContents.getWord() + " ");
      		   } 
        	} 
        }
	    Iterator<Map.Entry<String, SynsetContents>> itVerb = verbMap.entrySet().iterator();
	    while (itVerb.hasNext()) {
	       Map.Entry<String, SynsetContents> pairsSynset = (Map.Entry<String, SynsetContents>)itVerb.next();
	       SynsetContents synsetContents = pairsSynset.getValue();
        	
    	   System.out.println(synsetContents.getEqLinksWordnetOffset()  + " ");
    	   System.out.println("gloss get objects" + synsetContents.getGloss()  + " " );
    	   System.out.println(synsetContents.getLexFilenum()  + " " );
    	   System.out.println(synsetContents.getPCnt()  + " " );
    	   System.out.println(synsetContents.getPos()  + " " );
    	   System.out.println(synsetContents.getSynsetReference()  + " ####" );
    	   System.out.println(synsetContents.getNewOffset()  + " " );
    	   System.out.println(synsetContents.getWCnt()  + " " );
    	   System.out.println("word " + synsetContents.getWord()  + " " );
        	          
    	   Iterator <Map.Entry<String, PtrContents>>itPtr = pointerMap.entrySet().iterator();
    	   while (itPtr.hasNext()) {
    		   Map.Entry <String, PtrContents>pairsPtr = (Map.Entry<String, PtrContents>)itPtr.next();
    		   PtrContents ptrContents = pointerMap.get(pairsPtr.getKey());
      		     if(ptrContents.getSynsetReference()==synsetContents.getSynsetReference())
      		     {
            	   System.out.println( ptrContents.getNewOffset()+" "+ptrContents.getSense() + " " +ptrContents.getPointerSymbol() + " " +ptrContents.getSource() + " "+ptrContents.getSynsetReference() + " @@@"+ptrContents.getWord() + " ");
      		     } 
        	} 
        }    
	    Iterator itAdjective = adjectiveMap.entrySet().iterator();
	    while (itAdjective.hasNext()) {
	       Map.Entry pairsSynset = (Map.Entry)itAdjective.next();
	       SynsetContents synsetContents = (SynsetContents)pairsSynset.getValue();
        	
    	   System.out.println(synsetContents.getEqLinksWordnetOffset()  + " ");
    	   System.out.println("gloss get objects" + synsetContents.getGloss()  + " " );
    	   System.out.println(synsetContents.getLexFilenum()  + " " );
    	   System.out.println(synsetContents.getPCnt()  + " " );
    	   System.out.println(synsetContents.getPos()  + " " );
    	   System.out.println(synsetContents.getSynsetReference()  + " ####" );
    	   System.out.println(synsetContents.getNewOffset()  + " " );
    	   System.out.println(synsetContents.getWCnt()  + " " );
    	   System.out.println("word " + synsetContents.getWord()  + " " );
        	          
    	   Iterator <Map.Entry<String, PtrContents>>itPtr = pointerMap.entrySet().iterator();
    	   while (itPtr.hasNext()) {
    		   Map.Entry <String, PtrContents>pairsPtr = (Map.Entry<String, PtrContents>)itPtr.next();
    		   PtrContents ptrContents = pointerMap.get(pairsPtr.getKey());
      		     if(ptrContents.getSynsetReference()==synsetContents.getSynsetReference())
      		     {
            	   System.out.println( ptrContents.getNewOffset()+" "+ptrContents.getSense() + " " +ptrContents.getPointerSymbol() + " " +ptrContents.getSource() + " "+ptrContents.getSynsetReference() + " @@@"+ptrContents.getWord() + " ");
      		     } 
        	} 
        }    
	    Iterator itAdverb = adverbMap.entrySet().iterator();
	    while (itAdverb.hasNext()) {
	       Map.Entry pairsSynset = (Map.Entry)itAdverb.next();
	       SynsetContents synsetContents = (SynsetContents)pairsSynset.getValue();
        	
    	   System.out.println(synsetContents.getEqLinksWordnetOffset()  + " ");
    	   System.out.println("gloss get objects" + synsetContents.getGloss()  + " " );
    	   System.out.println(synsetContents.getLexFilenum()  + " " );
    	   System.out.println(synsetContents.getPCnt()  + " " );
    	   System.out.println(synsetContents.getPos()  + " " );
    	   System.out.println(synsetContents.getSynsetReference()  + " ####" );
    	   System.out.println(synsetContents.getNewOffset()  + " " );
    	   System.out.println(synsetContents.getWCnt()  + " " );
    	   System.out.println("word " + synsetContents.getWord()  + " " );
    	          
    	   Iterator itPtr = pointerMap.entrySet().iterator();
    	   while (itPtr.hasNext()) {
      		     Map.Entry pairsPtr = (Map.Entry)itPtr.next();
      		     PtrContents ptrContents = (PtrContents)pairsPtr.getKey();
      		     if(ptrContents.getSynsetReference()==synsetContents.getSynsetReference())
      		     {
            	   System.out.println( ptrContents.getNewOffset()+" "+ptrContents.getSense() + " " +ptrContents.getPointerSymbol() + " " +ptrContents.getSource() + " "+ptrContents.getSynsetReference() + " @@@"+ptrContents.getWord() + " ");
      		     } 
        	} 
        }    
		
	}//end function
	*/
	/*
	private void getPointers()
	{
		Iterator <Map.Entry<String, PtrContents>>itPtr = pointerMap.entrySet().iterator();
		 while (itPtr.hasNext()) {
			 Map.Entry <String, PtrContents>pairsPtr = (Map.Entry<String, PtrContents>)itPtr.next();
			 PtrContents ptrContents = pointerMap.get(pairsPtr.getKey());
            	   System.out.println( ptrContents.getSense() + " " +ptrContents.getPointerSymbol() + " " +ptrContents.getSource() + " "+ptrContents.getSynsetReference() + " @@@"+ptrContents.getWord() + " "+ptrContents.getSense()+" "+ptrContents.getNewOffset());
        	} 
		
	}//end function
	*/
	
	/**
	 * This method print all the relations of the original files
	 */
	private void getRelations()
	{
		Iterator<Map.Entry<String, String>> itNounRel = nounRelationsMap.entrySet().iterator();
		System.out.println("  - Noun Relations:");
		while (itNounRel.hasNext()) {
		     Map.Entry <String, String>pairsRel = itNounRel.next();
		     System.out.println("      * "+pairsRel.getKey());
        	} 
		
		Iterator <Map.Entry<String, String>>itVerbRel = verbRelationsMap.entrySet().iterator();
		System.out.println("  - Verb Relations:");
		while (itVerbRel.hasNext()) {
		     Map.Entry <String, String>pairsRel = itVerbRel.next();
		     System.out.println("      * "+pairsRel.getKey());
        	} 
		
		Iterator <Map.Entry<String, String>>itAdjectiveRel = adjectiveRelationsMap.entrySet().iterator();
		System.out.println("  - Noun Relations:");
		while (itAdjectiveRel.hasNext()) {
		     Map.Entry <String, String>pairsRel = itAdjectiveRel.next();
		     System.out.println("      * "+pairsRel.getKey());
		  	} 
		
		Iterator <Map.Entry<String, String>>itAdverbRel = adverbRelationsMap.entrySet().iterator();
		System.out.println("  - Noun Relations:");
		while (itAdverbRel.hasNext()) {
		     Map.Entry <String, String>pairsRel = itAdverbRel.next();
		     System.out.println("      * "+pairsRel.getKey());
		} 
	}//end function
	
	/*
	private String analyze(String text)throws IOException{
        System.out.println("Analzying \"" + text + "\"");
            Analyzer analyzer = new SnowballAnalyzer(getLang.getLanguage(JWNL.getVersion().getLocale().getLanguage().toString()));
            System.out.println("\t" + analyzer.getClass().getName() + ":");
            TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
            while (true) {
                Token token = stream.next();
                if (token == null) break;
                text=token.termText().toString();
                System.out.print("[" + token.termText() + "] ");
                
            }
            return text;
        }
    */
	
}
