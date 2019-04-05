package edu.wm.cs.src2abs.vocabulary;

import java.util.Set;

public class VocabularyChecker {
	
	private static final String VAR = "VAR_";
	private static final String TYPE = "TYPE_";
	private static final String METHOD = "METHOD_";
	private static final String STRING = "STRING_";
	private static final String CHAR = "CHAR_";
	private static final String INT = "INT_";
	private static final String FLOAT = "FLOAT_";

	
	
	public String compareVocabularies(String afterTokenization, String beforeTokenization) {
		
		Vocabulary afterVoc = buildVocabulary(afterTokenization);
		Vocabulary beforeVoc = buildVocabulary(beforeTokenization);

		return compareVocabularies(afterVoc, beforeVoc);
	}
	
	
	public String compareVocabularies(Vocabulary afterVoc, Vocabulary beforeVoc) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(compareSets(afterVoc.vars, beforeVoc.vars)+",");
		sb.append(compareSets(afterVoc.types, beforeVoc.types)+",");
		sb.append(compareSets(afterVoc.methods, beforeVoc.methods)+",");
		sb.append(compareSets(afterVoc.strings, beforeVoc.strings)+",");
		sb.append(compareSets(afterVoc.chars, beforeVoc.chars)+",");
		sb.append(compareSets(afterVoc.ints, beforeVoc.ints)+",");
		sb.append(compareSets(afterVoc.floats, beforeVoc.floats));
	
		return sb.toString();		
	}
	
	
	
	public String compareSets(Set<String> afterSet, Set<String> beforeSet) {
		
		String comparison = afterSet.size()+","+beforeSet.size()+","+afterSet.containsAll(beforeSet)+","+beforeSet.containsAll(afterSet);
		
		return comparison;
	}
	
	
	
	
	public Vocabulary buildVocabulary(String tokenization) {
		
		Vocabulary vocabulary = new Vocabulary();
		
		String[] tokens = tokenization.split(" ");
		
		for(String token : tokens) {
			if(token.startsWith(VAR)) {
				vocabulary.vars.add(token);
			} else if(token.startsWith(TYPE)) {
				vocabulary.types.add(token);
			} else if(token.startsWith(METHOD)) {
				vocabulary.methods.add(token);
			} else if(token.startsWith(STRING)) {
				vocabulary.strings.add(token);
			} else if(token.startsWith(CHAR)) {
				vocabulary.chars.add(token);
			} else if(token.startsWith(INT)) {
				vocabulary.ints.add(token);
			} else if(token.startsWith(FLOAT)) {
				vocabulary.floats.add(token);
			}
		}
		
//		System.out.println(vocabulary.vars);
//		System.out.println(vocabulary.types);
//		System.out.println(vocabulary.methods);
//		System.out.println(vocabulary.strings);
//		System.out.println(vocabulary.chars);
//		System.out.println(vocabulary.ints);
//		System.out.println(vocabulary.floats);
		
		return vocabulary;		
	}

}
