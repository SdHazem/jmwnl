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

package it.uniroma2.art.jmwnl.ewn.data;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.util.Resolvable;

import java.io.Serializable;
import java.util.*;

/**
 * @author Alexandra Tudorache <tudorache@info.uniroma2.it>
 * @author Armando Stellato <stellato@info.uniroma2.it>
 * @author Andrea Turbati <turbati@info.uniroma2.it>
 * 
 * This class is the EWN version of the original JWNL <code>PointerType</code>, but limited to the static list of pointers which
 * are characteristic to EuroWordNet.
 * Use of PointerType as definition class for instances of specific pointers is still delegated to the original JWNL class
 */
public final class EWNPointerTypes implements Serializable {

    private static final long serialVersionUID = 2114971198733397137L;
    
    // Flags for tagging a pointer type with the POS types it apples to.
	private static final int N = 1;
	private static final int V = 2;
	private static final int ADJ = 4;
	private static final int ADV = 8;
	private static final int LEXICAL = 16;
	
	//WordNet modified pointers to include other POS
	public static final PointerType CAUSE = new PointerType ("CAUSE", "CAUSE_KEY", N | V);
	public static final PointerType DERIVED = new PointerType ("DERIVED", "DERIVED_KEY", N | V | ADV);
	public static final PointerType SIMILAR_TO = new PointerType("SIMILAR", "SIMILAR_KEY",  N | V | ADJ | ADV);
	
	//EWN Pointers
	// Nouns
	public static final PointerType BELONGS_TO_CLASS = new PointerType("BELONGS_TO_CLASS", "BELONGS_TO_CLASS_KEY", N);
	public static final PointerType CATEGORY_DOMAIN = new PointerType ("CATEGORY_DOMAIN", "CATEGORY_DOMAIN_KEY", N);
	public static final PointerType CO_AGENT_INSTRUMENT = new PointerType("CO_AGENT_INSTRUMENT", "CO_AGENT_INSTRUMENT_KEY", N);
	public static final PointerType CO_INSTRUMENT_AGENT = new PointerType("CO_INSTRUMENT_AGENT", "CO_INSTRUMENT_AGENT_KEY", N);
	public static final PointerType CO_ROLE = new PointerType("CO_ROLE", "CO_ROLE_KEY", N);
	public static final PointerType FUZZYNYM = new PointerType("FUZZYNYM", "FUZZYNYM_KEY", N);
	public static final PointerType HAS_HOLO_LOCATION = new PointerType("HAS_HOLO_LOCATION", "HAS_HOLO_LOCATION_KEY", N);
	public static final PointerType HAS_HOLO_PORTION = new PointerType("HAS_HOLO_PORTION", "HAS_HOLO_PORTION_KEY", N);
	public static final PointerType HAS_HOLO_MADEOF = new PointerType("HAS_HOLO_MADEOF", "HAS_HOLO_MADEOF_KEY", N);
	public static final PointerType HAS_HOLO_MEMBER = new PointerType("HAS_HOLO_MEMBER", "HAS_HOLO_MEMBER_KEY", N);
	public static final PointerType HAS_MERO_LOCATION = new PointerType("HAS_MERO_LOCATION", "HAS_MERO_LOCATION_KEY", N);
	public static final PointerType HAS_MERO_PORTION = new PointerType("HAS_MERO_PORTION", "HAS_MERO_PORTION_KEY", N);
	public static final PointerType HAS_MERO_MADEOF = new PointerType("HAS_MERO_MADEOF", "HAS_MERO_MADEOF_KEY", N);
	public static final PointerType HAS_MERO_MEMBER = new PointerType("HAS_MERO_MEMBER", "HAS_MERO_MEMBER_KEY", N);
	public static final PointerType HAS_HOLONYM = new PointerType("HAS_HOLONYM", "HAS_HOLONYM_KEY", N);
	public static final PointerType HAS_MERONYM = new PointerType("HAS_MERONYM", "HAS_MERONYM_KEY", N);
	public static final PointerType INVOLVED = new PointerType("INVOLVED", "INVOLVED_KEY", N );
	public static final PointerType INVOLVED_INSTRUMENT = new PointerType("INVOLVED_INSTRUMENT", "INVOLVED_INSTRUMENT_KEY", N );
	public static final PointerType INVOLVED_PATIENT = new PointerType("INVOLVED_PATIENT", "INVOLVED_PATIENT_KEY", N );
	public static final PointerType INVOLVED_SOURCE_DIRECTION = new PointerType("INVOLVED_SOURCE_DIRECTION", "INVOLVED_SOURCE_DIRECTION_KEY", N );
	public static final PointerType ROLE = new PointerType("ROLE", "ROLE_KEY", N);
	public static final PointerType ROLE_AGENT = new PointerType("ROLE_AGENT", "ROLE_AGENT_KEY", N);
	public static final PointerType ROLE_INSTRUMENT = new PointerType("ROLE_INSTRUMENT", "ROLE_INSTRUMENT_KEY", N);
	public static final PointerType ROLE_PATIENT = new PointerType("ROLE_PATIENT", "ROLE_PATIENT_KEY", N);
	public static final PointerType ROLE_RESULT = new PointerType("ROLE_RESULT", "ROLE_RESULT_KEY", N);
	public static final PointerType USAGE_DOMAIN = new PointerType("USAGE_DOMAIN", "USAGE_DOMAIN_KEY", N);
	public static final PointerType XPOS_HYPERNYM = new PointerType("XPOS_HYPERNYM", "XPOS_HYPERNYM_KEY", N);
	public static final PointerType XPOS_ANTONYM = new PointerType("XPOS_ANTONYM", "XPOS_ANTONYM_KEY", N );
	
	// Verbs    
	public static final PointerType XPOS_HYPONYM = new PointerType("XPOS_HYPONYM", "XPOS_HYPONYM_KEY", V);
	public static final PointerType IN_MANNER = new PointerType("IN_MANNER", "IN_MANNER_KEY", V);
	public static final PointerType INVOLVED_DIRECTION = new PointerType("INVOLVED_DIRECTION", "INVOLVED_DIRECTION_KEY", V);
	public static final PointerType INVOLVED_TARGET_DIRECTION = new PointerType("INVOLVED_TARGET_DIRECTION", "INVOLVED_TARGET_DIRECTION_KEY", V);
	
	// Nouns and Verbs
	public static final PointerType BE_IN_STATE = new PointerType("BE_IN_STATE", "BE_IN_STATE_KEY", N | V);
	public static final PointerType HAS_SUBEVENT = new PointerType("HAS_SUBEVENT", "HAS_SUBEVENT_KEY", N | V);
	public static final PointerType INVOLVED_AGENT = new PointerType("INVOLVED_AGENT", "INVOLVED_AGENT_KEY", N | V);
	public static final PointerType INVOLVED_LOCATION = new PointerType("INVOLVED_LOCATION", "INVOLVED_LOCATION_KEY", N | V);
	public static final PointerType INVOLVED_RESULT = new PointerType("INVOLVED_RESULT", "INVOLVED_RESULT_KEY", N | V);
	public static final PointerType IS_SUBEVENT_OF = new PointerType("IS_SUBEVENT_OF", "IS_SUBEVENT_OF_KEY", N | V);
	
	//Adverbs
	public static final PointerType DERIVED_FROM_ADJECTIVE = new PointerType ("DERIVED_FROM_ADJECTIVE", "DERIVED_FROM_ADJECTIVE_KEY", ADV);
			
	// Nouns & Adjectives
	public static final PointerType DERIVATION = new PointerType("DERIVATION", "DERIVATION_KEY", N | ADJ);
	public static final PointerType STATE_OF = new PointerType("STATE_OF", "STATE_OF_KEY", N | ADJ);
	public static final PointerType DERIVATIONALLY_RELATED_FORM = new PointerType ("DERIVATIONALLY_RELATED_FORM", "DERIVATIONALLY_RELATED_FORM_KEY", N | ADJ);
	
	// Nouns & Adverbs
	public static final PointerType ROLE_DIRECTION = new PointerType("ROLE_DIRECTION", "ROLE_DIRECTION_KEY", N | ADV);
	public static final PointerType ROLE_LOCATION = new PointerType("ROLE_LOCATION", "ROLE_LOCATION_KEY", N | ADV);
	public static final PointerType ROLE_SOURCE_DIRECTION = new PointerType("ROLE_SOURCE_DIRECTION", "ROLE_SOURCE_DIRECTION_KEY", N | ADV);
	public static final PointerType ROLE_TARGET_DIRECTION = new PointerType("ROLE_TARGET_DIRECTION", "ROLE_TARGET_DIRECTION_KEY", N | ADV);
	
	
	// General Relations
	public static final PointerType HAS_DERIVED = new PointerType("HAS_DERIVED", "HAS_DERIVED_KEY", N | V | ADJ | ADV);
	public static final PointerType HAS_HYPERONYM = new PointerType("HAS_HYPERONYM", "HAS_HYPERONYM_KEY", N | V | ADJ);
	public static final PointerType HAS_HYPONYM = new PointerType("HAS_HYPONYM", "HAS_HYPONYM_KEY", N | V | ADJ);
	public static final PointerType IS_CAUSED_BY = new PointerType("IS_CAUSED_BY", "IS_CAUSED_BY_KEY", N | V | ADJ);
	public static final PointerType IS_DERIVED_FROM = new PointerType("IS_DERIVED_FROM", "IS_DERIVED_FROM_KEY", N | ADJ | ADV);
	public static final PointerType NEAR_ANTONYM = new PointerType("NEAR_ANTONYM", "NEAR_ANTONYM_KEY", N | V | ADJ | ADV);
	public static final PointerType NEAR_SYNONYM = new PointerType("NEAR_SYNONYM", "NEAR_SYNONYM_KEY", N | V | ADJ | ADV);
	public static final PointerType MANNER_OF = new PointerType("MANNER_OF", "MANNER_OF_KEY", N | V | ADJ | ADV);
	public static final PointerType XPOS_NEAR_ANTONYM = new PointerType("XPOS_NEAR_ANTONYM", "XPOS_NEAR_ANTONYM_KEY", N | V | ADJ | ADV);
	public static final PointerType XPOS_FUZZYNYM = new PointerType("XPOS_FUZZYNYM", "XPOS_FUZZYNYM_KEY", N | V | ADJ | ADV);
	public static final PointerType XPOS_SIMILAR_TO = new PointerType("XPOS_SIMILAR", "XPOS_SIMILAR_KEY", N | V | ADJ);

	
	
	/** A list of all <code>PointerType</code>s. */
	private static final List<PointerType> ALL_TYPES = Collections.unmodifiableList(Arrays.asList(new PointerType[] {
        PointerType.ANTONYM, PointerType.HYPERNYM, PointerType.HYPONYM, PointerType.ATTRIBUTE, PointerType.SEE_ALSO, PointerType.ENTAILMENT, PointerType.ENTAILED_BY, PointerType.VERB_GROUP,
        PointerType.MEMBER_MERONYM, PointerType.SUBSTANCE_MERONYM, PointerType.PART_MERONYM, PointerType.MEMBER_HOLONYM, PointerType.SUBSTANCE_HOLONYM, PointerType.PART_HOLONYM,
        PointerType.PARTICIPLE_OF, PointerType.NOMINALIZATION, PointerType.CATEGORY, PointerType.REGION, PointerType.USAGE, PointerType.CATEGORY_MEMBER,
        PointerType.REGION_MEMBER, PointerType.USAGE_MEMBER, PointerType.INSTANCE_HYPERNYM, PointerType.INSTANCES_HYPONYM, PointerType.PERTAINYM,//TROPONYM added from WordNet 2.1, 
        //WordNet modified pointers to include other POS
        CAUSE, DERIVED, SIMILAR_TO,  
        //EWN Pointers
        BELONGS_TO_CLASS, CATEGORY_DOMAIN, CO_AGENT_INSTRUMENT, CO_INSTRUMENT_AGENT, CO_ROLE, FUZZYNYM, HAS_HOLO_LOCATION, HAS_HOLO_PORTION,
        HAS_HOLO_MADEOF, HAS_HOLO_MEMBER, HAS_MERO_LOCATION, HAS_MERO_PORTION, HAS_MERO_MADEOF, HAS_MERO_MEMBER, HAS_HOLONYM, HAS_MERONYM,
        INVOLVED, INVOLVED_INSTRUMENT, INVOLVED_PATIENT, INVOLVED_SOURCE_DIRECTION, ROLE, ROLE_AGENT, ROLE_INSTRUMENT, ROLE_PATIENT,
        ROLE_RESULT, USAGE_DOMAIN, XPOS_HYPERNYM, XPOS_ANTONYM, XPOS_HYPONYM, IN_MANNER, INVOLVED_DIRECTION, INVOLVED_TARGET_DIRECTION,
        BE_IN_STATE, HAS_SUBEVENT, INVOLVED_AGENT, INVOLVED_LOCATION, INVOLVED_RESULT, IS_SUBEVENT_OF, DERIVED_FROM_ADJECTIVE,
        DERIVATION, STATE_OF, DERIVATIONALLY_RELATED_FORM, ROLE_DIRECTION, ROLE_LOCATION, ROLE_SOURCE_DIRECTION, ROLE_TARGET_DIRECTION,
        HAS_DERIVED, HAS_HYPERONYM, HAS_HYPONYM, IS_CAUSED_BY, IS_DERIVED_FROM, NEAR_ANTONYM, NEAR_SYNONYM, MANNER_OF, XPOS_NEAR_ANTONYM,
        XPOS_FUZZYNYM, XPOS_SIMILAR_TO
    }));


    private static boolean _initialized = false;

    public static void initialize() {
        if (!_initialized) {
            PointerType.setPointerTypes(ALL_TYPES);          
            _initialized = true;
        }
    }

    //standard WN simmetries should already have been stated in JWNL PointerTypes class
    static {
    	PointerType.setSymmetric(NEAR_ANTONYM, NEAR_ANTONYM);
    	PointerType.setSymmetric(XPOS_NEAR_ANTONYM, XPOS_NEAR_ANTONYM);
    	PointerType.setSymmetric(NEAR_SYNONYM, NEAR_SYNONYM);
    	PointerType.setSymmetric(BE_IN_STATE, STATE_OF);
    	PointerType.setSymmetric(CAUSE, IS_CAUSED_BY);
    	PointerType.setSymmetric(HAS_HOLO_MEMBER, HAS_MERO_MEMBER);
    	PointerType.setSymmetric(HAS_SUBEVENT, IS_SUBEVENT_OF);
    	PointerType.setSymmetric(XPOS_SIMILAR_TO, XPOS_SIMILAR_TO);
	}
    
    public static List <PointerType>getAllPointerTypes() {
        return ALL_TYPES;
    }
}