/**
 * Java WordNet Library (JWNL)
 * See the documentation for copyright information.
 */

/*

	author: John Didion
	contributors: Alexandra Tudorache

*/

package it.uniroma2.art.jmwnl.ewn.data;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.list.PointerTargetTreeNode;
import net.didion.jwnl.data.list.PointerTargetTreeNodeList;

import java.util.Iterator;

/**
 * This class constains static methods for performing various pointer operations. A pointer from one synset/word to
 * another connotes a relationship between those words. The type of the relationship is specified by the type
 * of pointer. See the WordNet documentation for information on pointer types. To avoid confusion with
 * the <code>Relationship</code> class, these relationships will be referred to as links.
 */
public final class EWNPointerUtils {
	/**
	 * Representation of infinite depth. Used to tell the pointer operations to
	 * return all links to an infinite depth.
	 */
	public static final int INFINITY = Integer.MAX_VALUE;
	private static final EWNPointerUtils INSTANCE = new EWNPointerUtils();

	public static EWNPointerUtils getInstance() {
		return INSTANCE;
	}

	private EWNPointerUtils() {
	}

	/** Get the immediate parents of <code>synset</code> */
	public PointerTargetNodeList getDirectHypernyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.HYPERNYM);
	}

	/** Get all of the ancestors of <code>synset</code> */
	public PointerTargetTree getHypernymTree(Synset synset) throws JWNLException {
		return getHypernymTree(synset, INFINITY);
	}

	/** Get all of the ancestors of <code>synset</code> to depth <code>depth</code> */
	public PointerTargetTree getHypernymTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.HYPERNYM, depth));
	}

	/** Get the immediate children of <code>synset</code> */
	public PointerTargetNodeList getDirectHyponyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.HYPONYM);
	}

	/** Get all of the children of <code>synset</code> */
	public PointerTargetTree getHyponymTree(Synset synset) throws JWNLException {
		return getHyponymTree(synset, INFINITY);
	}

	/** Get all of the children of <code>synset</code> to depth <code>depth</code> */
	public PointerTargetTree getHyponymTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.HYPONYM, depth));
	}

	//
	// other general operations
	//

	/** Get <code>synset</code>'s siblings (the hyponyms of its hypernyms) */
	public PointerTargetNodeList getCoordinateTerms(Synset synset) throws JWNLException {
		PointerTargetNodeList list = new PointerTargetNodeList();
		for (Iterator itr = getDirectHypernyms(synset).iterator(); itr.hasNext();) {
			list.addAll(getPointerTargets(((PointerTargetNode) itr.next()).getSynset(), PointerType.HYPONYM));
		}
		return list;
	}

	/** Get the words that mean the opposite of <code>synset</code> */
	public PointerTargetNodeList getAntonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.ANTONYM);
	}

	/** Get the words that mean the opposite of <code>synset</code> and the immediate synonyms of those words */
	public PointerTargetTree getExtendedAntonyms(Synset synset) throws JWNLException {
		return getExtendedAntonyms(synset, 1);
	}

	/** Find all antonyms of <code>synset</code>, and all synonyms of those antonyms to depth <code>depth</code>. */
	public PointerTargetTree getExtendedAntonyms(Synset synset, int depth) throws JWNLException {
		PointerTargetTreeNodeList list = new PointerTargetTreeNodeList();
		if (synset.getPOS() == POS.ADJECTIVE) {
			PointerTargetNodeList antonyms = getAntonyms(synset);
			list = makePointerTargetTreeList(antonyms, PointerType.SIMILAR_TO, PointerType.ANTONYM, depth, false);
		}
		return new PointerTargetTree(new PointerTargetTreeNode(synset, list, null));
	}

	/** Get the immediate antonyms of all words that mean the same as <code>synset</code>. */
	public PointerTargetTree getIndirectAntonyms(Synset synset) throws JWNLException {
		return getIndirectAntonyms(synset, 1);
	}

	/** Get the antonyms of all words that mean the same as <code>synset</code> to depth <code>depth</code>.*/
	public PointerTargetTree getIndirectAntonyms(Synset synset, int depth) throws JWNLException {
		PointerTargetTreeNodeList list = new PointerTargetTreeNodeList();
		if (synset.getPOS() == POS.ADJECTIVE) {
			PointerTargetNodeList synonyms = getSynonyms(synset);
			list = makePointerTargetTreeList(synonyms, PointerType.ANTONYM, PointerType.ANTONYM, depth, false);
		}
		return new PointerTargetTree(new PointerTargetTreeNode(synset, list, null));
	}

	/** Get the attributes of <code>synset</code> */
	public PointerTargetNodeList getAttributes(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.ATTRIBUTE);
	}

	/** Find what words are related to <code>synset</code> */
	public PointerTargetNodeList getAlsoSees(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.SEE_ALSO);
	}

	/** Find all See Also relations to depth <code>depth</code>.*/
	public PointerTargetTree getAlsoSeeTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.SEE_ALSO, depth));
	}

	//
	// noun operations
	//

	/** Get meronyms of <code>synset</code>. */
	public PointerTargetNodeList getMeronyms(Synset synset) throws JWNLException {
		PointerTargetNodeList list = new PointerTargetNodeList();
		list.addAll(getPartMeronyms(synset));
		list.addAll(getMemberMeronyms(synset));
		list.addAll(getSubstanceMeronyms(synset));
		return list;
	}

	/** Get part meronyms of <code>synset</code> */
	public PointerTargetNodeList getPartMeronyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.PART_MERONYM);
	}

	/** Get member meronyms of <code>synset</code> */
	public PointerTargetNodeList getMemberMeronyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.MEMBER_MERONYM);
	}

	/** Get substance meronyms of <code>synset</code> */
	public PointerTargetNodeList getSubstanceMeronyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.SUBSTANCE_MERONYM);
	}

	/** Get meronyms of <code>synset</code> and of all its ancestors */
	public PointerTargetTree getInteritedMeronyms(Synset synset) throws JWNLException {
		return getInheritedMeronyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get meronyms of each synset, to depth <code>pointerDepth</code> starting at
	 * <code>synset</code> and going for all of <code>synset</code>'s ancestors to depth
	 * <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedMeronyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		PointerType[] types = new PointerType[3];
		types[0] = PointerType.PART_MERONYM;
		types[1] = PointerType.MEMBER_MERONYM;
		types[2] = PointerType.SUBSTANCE_MERONYM;
		return makeInheritedTree(synset, types, null, pointerDepth, ancestorDepth, false);
	}

	/** Get part meronyms of <code>synset</code> and of all its ancestors */
	public PointerTargetTree getInheritedPartMeronyms(Synset synset) throws JWNLException {
		return getInheritedPartMeronyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get part meronyms of each synset, to depth <code>pointerDepth</code>, starting at
	 * <code>synset</code> and going for all of <code>synset</code>'s ancestors to depth
	 * <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedPartMeronyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		return makeInheritedTree(synset, PointerType.PART_MERONYM, null, pointerDepth, ancestorDepth);
	}

	/** Get member meronyms of synset and of its ancestors */
	public PointerTargetTree getInheritedMemberMeronyms(Synset synset) throws JWNLException {
		return getInheritedMemberMeronyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get member meronyms of each synset, to depth <code>pointerDepth</code>, starting at
	 * <code>synset</code> and going for all of <code>synset</code>'s ancestors to depth
	 * <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedMemberMeronyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		return makeInheritedTree(synset, PointerType.MEMBER_MERONYM, null, pointerDepth, ancestorDepth);
	}

	/** Get substance meronyms of <code>synset</code> and of its ancestors */
	public PointerTargetTree getInheritedSubstanceMeronyms(Synset synset) throws JWNLException {
		return getInheritedSubstanceMeronyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get substance meronyms of each synset, to depth <code>pointerDepth</code>, starting at
	 * <code>synset</code> and going for all of <code>synset</code>'s ancestors to depth
	 * <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedSubstanceMeronyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		return makeInheritedTree(synset, PointerType.SUBSTANCE_MERONYM, null, pointerDepth, ancestorDepth);
	}

	/** Get holonyms of <code>synset</code> */
	public PointerTargetNodeList getHolonyms(Synset synset) throws JWNLException {
		PointerTargetNodeList list = new PointerTargetNodeList();
		list.addAll(getPartHolonyms(synset));
		list.addAll(getMemberHolonyms(synset));
		list.addAll(getSubstanceHolonyms(synset));
		return list;
	}

	/** Get part holonyms of <code>synset</code> */
	public PointerTargetNodeList getPartHolonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.PART_HOLONYM);
	}

	/** Get member holonyms of <code>synset</code> */
	public PointerTargetNodeList getMemberHolonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.MEMBER_HOLONYM);
	}

	/** Get substance holonyms of <code>synset</code> */
	public PointerTargetNodeList getSubstanceHolonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.SUBSTANCE_HOLONYM);
	}

	/** Get holonyms of <code>synset</code> and of all its ancestors */
	public PointerTargetTree getInheritedHolonyms(Synset synset) throws JWNLException {
		return getInheritedHolonyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get holonyms of each synset, to depth <code>pointerDepth</code>, starting at <code>synset</code>
	 * and going for all of <code>synset</code>'s ancestors to depth <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedHolonyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		PointerType[] types = new PointerType[3];
		types[0] = PointerType.PART_HOLONYM;
		types[1] = PointerType.MEMBER_HOLONYM;
		types[2] = PointerType.SUBSTANCE_HOLONYM;
		return makeInheritedTree(synset, types, null, pointerDepth, ancestorDepth, false);
	}

	/** Get part holonyms of <code>synset</code> and of all its ancestors */
	public PointerTargetTree getInheritedPartHolonyms(Synset synset) throws JWNLException {
		return getInheritedPartHolonyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get part holonyms of each synset, to depth <code>pointerDepth</code>, starting at <code>synset</code>
	 * and going for all of <code>synset</code>'s ancestors to depth <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedPartHolonyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		return makeInheritedTree(synset, PointerType.PART_HOLONYM, null, pointerDepth, ancestorDepth);
	}

	/** Get member holonyms of <code>synset</code> and of all its ancestors */
	public PointerTargetTree getInheritedMemberHolonyms(Synset synset) throws JWNLException {
		return getInheritedMemberHolonyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get member holonyms of each synset, to depth <code>pointerDepth</code>, starting at <code>synset</code>
	 * and going for all of <code>synset</code>'s ancestors to depth <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedMemberHolonyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		return makeInheritedTree(synset, PointerType.MEMBER_HOLONYM, null, pointerDepth, ancestorDepth);
	}

	/** Get substance holonyms of <code>synset</code> and of all its ancestors */
	public PointerTargetTree getInheritedSubstanceHolonyms(Synset synset) throws JWNLException {
		return getInheritedSubstanceHolonyms(synset, INFINITY, INFINITY);
	}

	/**
	 * Get substance holonyms of each synset, to depth <code>pointerDepth</code>, starting at <code>synset</code>
	 * and going for all of <code>synset</code>'s ancestors to depth <code>ancestorDepth</code>.
	 */
	public PointerTargetTree getInheritedSubstanceHolonyms(Synset synset, int pointerDepth, int ancestorDepth)
	    throws JWNLException {
		return makeInheritedTree(synset, PointerType.SUBSTANCE_HOLONYM, null, pointerDepth, ancestorDepth);
	}

	//
	// Verb Operations
	//

	/** Find direct entailments of <code>synset</code> */
	public PointerTargetNodeList getEntailments(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.ENTAILMENT);
	}

	/** Find all entailments for <code>synset</code> */
	public PointerTargetTree getEntailmentTree(Synset synset) throws JWNLException {
		return getEntailmentTree(synset, INFINITY);
	}

	/** Find all entailments for <code>synset</code> to depth <code>depth</code> */
	public PointerTargetTree getEntailmentTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.ENTAILMENT, depth));
	}

	/** Find direct entailed bys of <code>synset</code> */
	public PointerTargetNodeList getEntailedBy(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.ENTAILED_BY);
	}

	/** Find all entailed bys of <code>synset</code>. */
	public PointerTargetTree getEntailedByTree(Synset synset) throws JWNLException {
		return getEntailedByTree(synset, INFINITY);
	}

	/** Find all entailed bys of <code>synset</code> to depth <code>depth</code>. */
	public PointerTargetTree getEntailedByTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.ENTAILED_BY, depth));
	}

	/** Find direct cause links of <code>synset</code> */
	public PointerTargetNodeList getCauses(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.CAUSE);
	}

	/** Find all cause links for <code>synset</code>.*/
	public PointerTargetTree getCauseTree(Synset synset) throws JWNLException {
		return getCauseTree(synset, INFINITY);
	}

	/** Find all cause links for <code>synset</code> to depth <code>depth</code>.*/
	public PointerTargetTree getCauseTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.CAUSE, depth));
	}

	/** Get the group that this verb belongs to. */
	public PointerTargetNodeList getVerbGroup(Synset synset) throws JWNLException {
		// We need to go through all this hastle because
		// 1. a verb does not always have links to all the verbs in its group
		// 2. two verbs in the same group sometimes have reciprocal links, and we want
		//    to make sure that each verb synset appears in the final list only once

		PointerTargetNodeList nodes = new PointerTargetNodeList();
		nodes.add(new PointerTargetNode(synset, PointerType.VERB_GROUP));
		int maxIndex = 0;
		int index = -1;
		do {
			index++;
			PointerTargetNode node = (PointerTargetNode) nodes.get(index);
			for (Iterator itr = getPointerTargets(node.getSynset(), PointerType.VERB_GROUP).iterator(); itr.hasNext();) {
				PointerTargetNode testNode = (PointerTargetNode) itr.next();
				if (!nodes.contains(testNode)) {
					nodes.add(testNode);
					maxIndex++;
				}
			}
		} while (index < maxIndex);

		return nodes;
	}

	//
	// Adjective Operations
	//

	/** Find participle of links of <code>synset</code> */
	public PointerTargetNodeList getParticipleOf(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.PARTICIPLE_OF);
	}

	/** Find derrived links of <code>synset</code> */
	public PointerTargetNodeList getDerived(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.DERIVED);
	}

	/**
	 * Get the synonyms for <code>synset</code>. This is meant for adjectives. Synonyms to
	 * nouns and verbs are just their hypernyms.
	 */
	public PointerTargetNodeList getSynonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.SIMILAR_TO);
	}

	/** Get all the synonyms of <code>synset</code> to depth <code>depth</code>. */
	public PointerTargetTree getSynonymTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.SIMILAR_TO, null, depth, false));
	}

	// General relation functions

	/** Get all the pointer targets of <var>synset</var> of type <var>type</var>. */
	private PointerTargetNodeList getPointerTargets(Synset synset, PointerType type) throws JWNLException {
		return new PointerTargetNodeList(synset.getTargets(type));
	}

	/**
	 * Make a nested list of pointer targets to the default depth, starting at <code>synset</code>. Each
	 * level of the list is related to the previous level by a pointer of type <var>searchType</var>.
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(Synset set, PointerType searchType)
	    throws JWNLException {
		return makePointerTargetTreeList(set, searchType, INFINITY);
	}

	/**
	 * Make a nested list of pointer targets to depth <var>depth</var>, starting at <code>synset</code>. Each
	 * level of the list is related to the previous level by a pointer of type <var>searchType</var>.
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(Synset set, PointerType searchType, int depth)
	    throws JWNLException {
		return makePointerTargetTreeList(set, searchType, null, depth, true);
	}

	/**
	 * Make a nested list of pointer targets to depth <var>depth</var>, starting at <code>synset</code>. Each
	 * level of the list is related to the previous level by a pointer of type <var>searchType</var>.
	 * @param labelType the type used to label each pointer target in the tree
	 * @param allowRedundancies if true, duplicate items will be included in the tree
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(Synset set, PointerType searchType,
	                                                           PointerType labelType, int depth,
	                                                           boolean allowRedundancies) throws JWNLException {
		PointerType[] searchTypes = new PointerType[1];
		searchTypes[0] = searchType;
		return makePointerTargetTreeList(set, searchTypes, labelType, depth, allowRedundancies);
	}

	/**
	 * Make a nested list of pointer targets to the default depth, starting at <code>synset</code>. Each
	 * level of the list is related to the previous level by one of the pointer types specified by
	 * <var>searchTypes</var>.
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(Synset set, PointerType[] searchTypes)
	    throws JWNLException {
		return makePointerTargetTreeList(set, searchTypes, INFINITY);
	}

	/**
	 * Make a nested list of pointer targets to depth <var>depth</var>, starting at <code>synset</code>. Each
	 * level of the list is related to the previous level by one of the pointer types specified by
	 * <var>searchTypes</var>.
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(Synset set, PointerType[] searchTypes, int depth)
	    throws JWNLException {
		return makePointerTargetTreeList(set, searchTypes, null, depth, true);
	}

	/**
	 * Make a nested list of pointer targets to depth <var>depth</var>, starting at <code>synset</code>. Each
	 * level of the list is related to the previous level by one of the pointer types specified by
	 * <var>searchTypes</var>.
	 * @param labelType the type used to label each pointer target in the tree
	 * @param allowRedundancies if true, duplicate items will be included in the tree
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(Synset synset, PointerType[] searchTypes,
	                                                           PointerType labelType, int depth,
	                                                           boolean allowRedundancies) throws JWNLException {
		return makePointerTargetTreeList(synset, searchTypes, labelType, depth, allowRedundancies, null);
	}

	/**
	 * Make a nested list of pointer targets to depth <var>depth</var>, starting at each <code>synset</code> in
	 * <var>list</var>. Each level of the list is related to the previous level by a pointer of type
	 * <var>searchType</var>.
	 * @param labelType the type used to label each pointer target in the tree
	 * @param allowRedundancies if true, duplicate items will be included in the tree
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(PointerTargetNodeList list, PointerType searchType,
	                                                           PointerType labelType, int depth,
	                                                           boolean allowRedundancies) throws JWNLException {
		PointerType[] searchTypes = new PointerType[1];
		searchTypes[0] = searchType;
		return makePointerTargetTreeList(list, searchTypes, labelType, depth, allowRedundancies);
	}

	/**
	 * Make a nested list of pointer targets to depth <var>depth</var>, starting at each <code>synset</code> in
	 * <var>list</var>. Each level of the list is related to the previous level by one of the pointer types specified
	 * by <var>searchTypes</var>.
	 * @param labelType the type used to label each pointer target in the tree
	 * @param allowRedundancies if true, duplicate items will be included in the tree
	 */
	public PointerTargetTreeNodeList makePointerTargetTreeList(PointerTargetNodeList list, PointerType[] searchTypes,
	                                                           PointerType labelType, int depth,
	                                                           boolean allowRedundancies) throws JWNLException {
		PointerTargetTreeNodeList treeList = new PointerTargetTreeNodeList();
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			PointerTargetNode node = (PointerTargetNode) itr.next();
			treeList.add(node.getPointerTarget(),
			             makePointerTargetTreeList(node.getSynset(), searchTypes, labelType, depth, allowRedundancies),
			             labelType);
		}
		return treeList;
	}

	private PointerTargetTreeNodeList makePointerTargetTreeList(Synset synset, PointerType[] searchTypes,
	                                                            PointerType labelType, int depth,
	                                                            boolean allowRedundancies,
	                                                            PointerTargetTreeNode parent) throws JWNLException {
	    depth--;
		PointerTargetTreeNodeList list = new PointerTargetTreeNodeList();
		for (int i = 0; i < searchTypes.length; i++) {
			PointerType type = searchTypes[i];
			PointerTargetNodeList targets = new PointerTargetNodeList(synset.getTargets(type));
			if (targets.size() > 0) {
				for (Iterator itr = targets.iterator(); itr.hasNext();) {
                    PointerTargetNode ptr = (PointerTargetNode)itr.next();
                    ptr.getSynset();
                    PointerTargetTreeNode node =
					    new PointerTargetTreeNode(ptr.getPointerTarget(),
					                              labelType == null ? type : labelType, parent);
					if (allowRedundancies || !list.contains(node)) {
						if (depth != 0) {
							node.setChildTreeList(makePointerTargetTreeList(node.getSynset(), searchTypes, labelType,
							                                                depth, allowRedundancies, node));
						}
          				list.add(node);
					}
				}
			}
		}
		return list;
	}

	/**
	 * Create a hypernym tree starting at <var>synset</var>, and add to each node a nested list pointer targets of type
	 * <var>searchType</var>, starting at the node's pointer target. This method uses the default depths.
	 */
	public PointerTargetTree makeInheritedTree(Synset synset, PointerType searchType) throws JWNLException {
		return makeInheritedTree(synset, searchType, null, INFINITY, INFINITY);
	}

	/**
	 * Create a hypernym tree starting at <var>synset</var>, and add to each node a nested list pointer targets of type
	 * <var>searchType</var>, starting at the node's pointer target.
	 * @param pointerDepth the depth to which to search for each pointer list
	 * @param ancestorDepth the depth to which to go to in the hypernym list
	 */
	public PointerTargetTree makeInheritedTree(Synset synset, PointerType searchType, PointerType labelType,
	                                           int pointerDepth, int ancestorDepth) throws JWNLException {
		return makeInheritedTree(synset, searchType, labelType, pointerDepth, ancestorDepth, true);
	}

	/**
	 * Create a hypernym tree starting at <var>synset</var>, and add to each node a nested list pointer targets of type
	 * <var>searchType</var>, starting at the node's pointer target.
	 * @param pointerDepth the depth to which to search for each pointer list
	 * @param ancestorDepth the depth to which to go to in the hypernym list
	 * @param allowRedundancies if true, duplicate items are allowed in the list
	 */
	public PointerTargetTree makeInheritedTree(Synset synset, PointerType searchType, PointerType labelType,
	                                           int pointerDepth, int ancestorDepth, boolean allowRedundancies)
	    throws JWNLException {
		PointerType[] searchTypes = new PointerType[1];
		searchTypes[0] = searchType;
		return makeInheritedTree(synset, searchTypes, labelType, pointerDepth, ancestorDepth, allowRedundancies);
	}

	/**
	 * Create a hypernym tree starting at <var>synset</var>, and add to each node a nested list pointer targets of
	 * the types specified in <var>searchTypes</var>, starting at the node's pointer target. This method uses the
	 * default depths.
	 */
	public PointerTargetTree makeInheritedTree(Synset synset, PointerType[] searchTypes) throws JWNLException {
		return makeInheritedTree(synset, searchTypes, null, INFINITY, INFINITY);
	}

	/**
	 * Create a hypernym tree starting at <var>synset</var>, and add to each node a nested list pointer targets of
	 * the types specified in <var>searchTypes</var>, starting at the node's pointer target.
	 * @param pointerDepth the depth to which to search for each pointer list
	 * @param ancestorDepth the depth to which to go to in the hypernym list
	 */
	public PointerTargetTree makeInheritedTree(Synset synset, PointerType[] searchTypes, PointerType labelType,
	                                           int pointerDepth, int ancestorDepth) throws JWNLException {
		return makeInheritedTree(synset, searchTypes, labelType, pointerDepth, ancestorDepth, true);
	}

	/**
	 * Create a hypernym tree starting at <var>synset</var>, and add to each node a nested list pointer targets of
	 * the types specified in <var>searchTypes</var>, starting at the node's pointer target.
	 * @param pointerDepth the depth to which to search for each pointer list
	 * @param ancestorDepth the depth to which to go to in the hypernym list
	 * @param allowRedundancies if true, duplicate items are allowed in the list
	 */
	public PointerTargetTree makeInheritedTree(Synset synset, PointerType[] searchTypes, PointerType labelType,
	                                           int pointerDepth, int ancestorDepth, boolean allowRedundancies)
	    throws JWNLException {
		PointerTargetTree hypernyms = getHypernymTree(synset, INFINITY);
		return makeInheritedTree(hypernyms, searchTypes, labelType, pointerDepth, ancestorDepth, allowRedundancies);
	}

	/**
	 * Turn an existing tree into an inheritance tree.
	 * @param tree the tree to convert
	 * @param searchTypes the pointer types to include in the pointer lists
	 * @param labelType the <code>PointerType</code> with which to label each pointer
	 * @param pointerDepth the depth to which to search for each pointer list
	 * @param ancestorDepth the depth to which to go to in <code>tree</code>
	 * @param allowRedundancies if true, duplicate items are allowed in the list
	 */
	public PointerTargetTree makeInheritedTree(PointerTargetTree tree, PointerType[] searchTypes,
	                                           PointerType labelType, int pointerDepth, int ancestorDepth,
	                                           boolean allowRedundancies) throws JWNLException {
		PointerTargetTreeNode root = tree.getRootNode();
		root.setPointerTreeList(makePointerTargetTreeList(root.getSynset(), searchTypes, labelType, pointerDepth, allowRedundancies));
		root.setChildTreeList(makeInheritedTreeList(root.getChildTreeList(), searchTypes, labelType, pointerDepth,
		                                            ancestorDepth, allowRedundancies));
		return new PointerTargetTree(root);
	}

	/**
	 * Turn an existing tree list into an inheritance tree list.
	 * @param list the tree list to convert
	 * @param searchTypes the pointer types to include in the pointer lists
	 * @param labelType the <code>PointerType</code> with which to label each pointer
	 * @param pointerDepth the depth to which to search for each pointer list
	 * @param ancestorDepth the depth to which to go to in <code>tree</code>
	 * @param allowRedundancies if true, duplicate items are allowed in the list
	 */
	public PointerTargetTreeNodeList makeInheritedTreeList(PointerTargetTreeNodeList list,
	                                                       PointerType[] searchTypes, PointerType labelType,
	                                                       int pointerDepth, int ancestorDepth,
	                                                       boolean allowRedundancies) throws JWNLException {
		ancestorDepth--;
		PointerTargetTreeNodeList inherited = new PointerTargetTreeNodeList();
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			PointerTargetTreeNode node = (PointerTargetTreeNode) itr.next();
			if (allowRedundancies || !inherited.contains(node)) {
				if (ancestorDepth == 0) {
					inherited.add(node.getPointerTarget(),
					              null,
					              makePointerTargetTreeList(node.getSynset(), searchTypes, labelType, pointerDepth, allowRedundancies),
					              PointerType.HYPERNYM);
				} else {
					inherited.add(node.getPointerTarget(),
					              makeInheritedTreeList(node.getChildTreeList(), searchTypes, labelType,
					                                    pointerDepth, ancestorDepth, allowRedundancies),
					              makePointerTargetTreeList(node.getSynset(), searchTypes, labelType, pointerDepth, allowRedundancies),
					              PointerType.HYPERNYM);
				}
			}
		}
		return inherited;
	}
	
	
	//WN 2.1 Methods
    //TODO check this methods!!!
	 /** Get the instance hypernyms of <code>synset</code> */
	
    public PointerTargetNodeList getInstanceHypernyms(Synset synset) throws JWNLException {
        return getPointerTargets(synset, PointerType.INSTANCE_HYPERNYM);
    }
    

    
    /** Get all of the ancestors of instance <code>synset</code> */
    public PointerTargetTree getInstanceHypernymTree(Synset synset) throws JWNLException {
        //place here first method which gets an instance_hypernym list, and then gets a hypernym tree from each of the instance_hypernyms
        return getInstanceHypernymTree(synset, INFINITY);
    }
	
    /** Get all of the ancestors of instance <code>synset</code> to depth <code>depth</code> */
    public PointerTargetTree getInstanceHypernymTree(Synset synset, int depth) throws JWNLException {
        //place here first method which gets an instance_hypernym list, and then gets a hypernym tree from each of the instance_hypernyms
    	PointerTargetNodeList dhyps = getInstanceHypernyms(synset);
    	return new PointerTargetTree(synset, 
    			makePointerTargetTreeList(dhyps, PointerType.HYPERNYM, PointerType.HYPERNYM, depth, true));
    }
    
	 /** Get the instances of <code>synset</code> */
	
    public PointerTargetNodeList getInstanceHyponyms(Synset synset) throws JWNLException {
        return getPointerTargets(synset, PointerType.INSTANCES_HYPONYM);
    }
    
    
    /** Get all of the ancestors of instance <code>synset</code> */
	public PointerTargetTree getInstanceHyponymTree(Synset synset) throws JWNLException {
    	//place here first method which gets an instance_hypernym list, and then gets a hypernym tree from each of the instance_hypernyms    	
    	return getInstanceHyponymTree(synset, INFINITY);
    }
    
    /** Get all of the ancestors of instance <code>synset</code> to depth <code>depth</code> */
	public PointerTargetTree getInstanceHyponymTree(Synset synset, int depth) throws JWNLException {
        //place here first method which gets an instance_hyponym list, and then gets a hyponym tree from each of the instance_hypernyms
    	PointerTargetNodeList dhypos = getInstanceHyponyms(synset);
    	return new PointerTargetTree(synset, 
    			makePointerTargetTreeList(dhypos, PointerType.HYPONYM, PointerType.HYPONYM, depth, true));
    }
    /*
	///troponyms
	// Get the immediate children of <code>synset</code> 
	public PointerTargetNodeList getDirectTroponyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.TROPONYM);
	}

	// Get all of the children of <code>synset</code> 
	public PointerTargetTree getTroponymTree(Synset synset) throws JWNLException {
		return getTroponymTree(synset, INFINITY);
	}

	// Get all of the children of <code>synset</code> to depth <code>depth</code> 
	public PointerTargetTree getTroponymTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, PointerType.TROPONYM, depth));
	}
    */
	//category and usage
	public PointerTargetNodeList getCategoryDomain(Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.CATEGORY_DOMAIN);
	}
	
	public PointerTargetNodeList getCategoryMemeber(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.CATEGORY_MEMBER);
	}
	
	public PointerTargetNodeList getUsageDomain(Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.USAGE_DOMAIN);
	}
	
	public PointerTargetNodeList getUsageMemeber(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.USAGE_MEMBER);
	}

	public PointerTargetNodeList getPertainyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, PointerType.PERTAINYM);
	}
	
	//EWN Methods
	
	public PointerTargetNodeList getXposSynonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.XPOS_SIMILAR_TO);
	}

	/** Get all the synonyms of <code>synset</code> to depth <code>depth</code>. */
	public PointerTargetTree getXposSynonymTree(Synset synset, int depth) throws JWNLException {
		return new PointerTargetTree(synset, makePointerTargetTreeList(synset, EWNPointerTypes.XPOS_SIMILAR_TO, null, depth, false));
	}
	
	/** Get the words that mean the opposite of <code>synset</code> */
	public PointerTargetNodeList getNearAntonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.NEAR_ANTONYM);
	}
	
	/** Get the words that mean the opposite of <code>synset</code> */
	public PointerTargetNodeList getXPOSNearAntonyms(Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.XPOS_NEAR_ANTONYM);
	}
	 
	public PointerTargetNodeList getHasDerived (Synset synset)throws JWNLException {
		return getPointerTargets(synset, PointerType.DERIVED);
	}
	
	public PointerTargetNodeList getMannerOf (Synset synset)throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.MANNER_OF );
	}
	
	public PointerTargetNodeList getInManner (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.IN_MANNER );
	}
	
	public PointerTargetNodeList getBelongsToClass (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.BELONGS_TO_CLASS);
	}
	
	public PointerTargetNodeList getCoAgentInstrument (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.CO_AGENT_INSTRUMENT );
	}
	
	public PointerTargetNodeList getCoInstrumentAgent (Synset synset)throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.CO_INSTRUMENT_AGENT );
	}
	
	public PointerTargetNodeList getCoRole (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.CO_ROLE );
	}
	
	public PointerTargetNodeList getFuzzynym (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.FUZZYNYM );
	}
	
	public PointerTargetNodeList getHasHoloLocation (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_HOLO_LOCATION );
	}
	
	public PointerTargetNodeList getHasHoloPortion (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_HOLO_PORTION );
	}
	
	public PointerTargetNodeList getHasHoloMadeof (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_HOLO_MADEOF );
	}
	
	public PointerTargetNodeList getHasHolonym (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_HOLONYM );
	}
	
	public PointerTargetNodeList getHasMeroLocation (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_MERO_LOCATION );
	}
	
	public PointerTargetNodeList getHasMeroPortion (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_MERO_PORTION );
	}
	
	public PointerTargetNodeList getHasMeroMadeof (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_MERO_MADEOF );
	}
	
	public PointerTargetNodeList getHasMeronym (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_MERONYM );
	}
	
	public PointerTargetNodeList getRole (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE );
	}
	
	public PointerTargetNodeList getRoleAgent (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_AGENT );
	}
	
	public PointerTargetNodeList getRoleInstrument (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_INSTRUMENT );
	}
	
	public PointerTargetNodeList getRolePatient (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_PATIENT );
	}
	
	public PointerTargetNodeList getRoleResult (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_RESULT );
	}
	
	public PointerTargetNodeList getDerivation (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.DERIVATION );
	}
	
	public PointerTargetNodeList getStateOf (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.STATE_OF );
	}
	
	public PointerTargetNodeList getIsDerivedFrom (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.IS_DERIVED_FROM );
	}
	
	public PointerTargetNodeList getRoleDirection (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_DIRECTION );
	}
	
	public PointerTargetNodeList getRoleLocation (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_LOCATION );
	}
	
	public PointerTargetNodeList getRoleSourceDirection (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_SOURCE_DIRECTION );
	}
	
	public PointerTargetNodeList getRoleTargetDirection (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.ROLE_TARGET_DIRECTION );
	}
	
	public PointerTargetNodeList getBeInState (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.BE_IN_STATE );
	}
	
	
	public PointerTargetNodeList getHasSubevent (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.HAS_SUBEVENT );
	}
	
	public PointerTargetNodeList getInvolved (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED );
	}
	
	public PointerTargetNodeList getInvolvedAgent (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_AGENT );
	}
	
	public PointerTargetNodeList getInvolvedInstrument (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_INSTRUMENT );
	}
	
	public PointerTargetNodeList getInvolvedLocation (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_LOCATION );
	}
	
	public PointerTargetNodeList getInvolvedPatient (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_PATIENT );
	}
	
	public PointerTargetNodeList getInvolvedResult (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_RESULT );
	}
	
	public PointerTargetNodeList getInvolvedSourceDirection (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_SOURCE_DIRECTION );
	}
	
	public PointerTargetNodeList getIsSubeventOf (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.IS_SUBEVENT_OF );
	}
	
	public PointerTargetNodeList getIsCausedBy (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.IS_CAUSED_BY );
	}
	
	public PointerTargetNodeList getXposFuzzynym (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.XPOS_FUZZYNYM );
	}
	
	public PointerTargetNodeList getHasXposHyponym (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.XPOS_HYPONYM );
	}
	
	public PointerTargetNodeList getHasXposHyperonym (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.XPOS_HYPERNYM );
	}
	
	public PointerTargetNodeList getInvolvedDirection (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_DIRECTION );
	}
	
	public PointerTargetNodeList getInvolvedTargetDirection (Synset synset) throws JWNLException {
		return getPointerTargets(synset, EWNPointerTypes.INVOLVED_TARGET_DIRECTION );
	}


	////////////End EWN relations
	
	
}