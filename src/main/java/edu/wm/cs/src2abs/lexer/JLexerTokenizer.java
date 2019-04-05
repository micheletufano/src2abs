package edu.wm.cs.src2abs.lexer;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import edu.wm.cs.compiler.tools.generators.scanners.JavaLexer;
import edu.wm.cs.src2abs.code.SourceCodeAnalyzer;


public class JLexerTokenizer {

	public static final String ERROR_LEXER = "<ERROR>";
	public static final String SPACED_DOT = " . ";
	private int count_identifiers = 0;
	private int count_types = 0;
	private int count_methods = 0;
	private int count_vars = 0;
	private int count_character = 0;
	private int count_floatingpoint = 0;
	private int count_integer = 0;
	private int count_string = 0;
	private Map<String, String> identifiers = new HashMap<>();
	private Map<String, String> stringLiteral = new HashMap<>();
	private Map<String, String> characterLiteral = new HashMap<>();
	private Map<String, String> integerLiteral = new HashMap<>();
	private Map<String, String> floatingPointLiteral = new HashMap<>();

	private Map<String, String> typeMap = new HashMap<>();
	private Map<String, String> methodMap = new HashMap<>();
	private Map<String, String> varMap = new HashMap<>();

	//Parser sets
	private Set<String> idioms;
	private Set<String> types;
	private Set<String> methods;




	public String tokenize(String filePath) {	

		List<Token> tokens = null;
		try {
			tokens = readTokens(filePath);
		}catch(StackOverflowError e){
			System.err.println("STACKOVERFLOW DURING LEXICAL ANALYSIS!");
			return ERROR_LEXER;
		}
		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < tokens.size(); i++) {
			String token = "";
			Token t = tokens.get(i);

			if(t.getType() == JavaLexer.Identifier) {

				String tokenName = t.getText();
				int j = i + 1;

				boolean expectDOT = true;
				while(j < tokens.size()) {
					Token nextToken = tokens.get(j);
					if(expectDOT) {
						if(nextToken.getType() == JavaLexer.DOT) {
							tokenName += nextToken.getText();
							expectDOT = false;
						} else {
							i = j-1;
							break;
						}
					} else {
						if(nextToken.getType() == JavaLexer.Identifier) {
							tokenName += nextToken.getText();
							expectDOT = true;
						} else {
							i = j-1;
							break;
						}
					}
					j++;
				}
				

				token = analyzeIdentifier(tokenName, tokens, i);
			}
			else if (t.getType() == JavaLexer.CharacterLiteral) {
				token = getCharacterID(t);
			}
			else if (t.getType() == JavaLexer.FloatingPointLiteral) {
				token = getFloatingPointID(t);
			}
			else if (t.getType() == JavaLexer.IntegerLiteral) {
				token = getIntegerID(t);
			}
			else if (t.getType() == JavaLexer.StringLiteral) {
				token = getStringID(t);
			}
			else {
				token = t.getText();
			}	

			sb.append(token + " ");
		}

		return sb.toString().trim();	
	}



	private static  List<Token> readTokens(String filePath) {
		JavaLexer jLexer = null;
		//Read source code
		try {
			String sourceCode = SourceCodeAnalyzer.readSourceCode(filePath);

			//Remove comments and annotations
			sourceCode = SourceCodeAnalyzer.removeCommentsAndAnnotations(sourceCode);

			InputStream inputStream = new ByteArrayInputStream(sourceCode.getBytes(StandardCharsets.UTF_8.name()));
			jLexer = new JavaLexer(new ANTLRInputStream(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Extract tokens
		List<Token> tokens = new ArrayList<>();
		for (Token t = jLexer.nextToken(); t.getType() != Token.EOF; t = jLexer.nextToken()) {
			tokens.add(t);
		}

		return tokens;
	}


	/**
	 * Checks whether the source code in the file contains a subset of the identifiers/literals token of the previous tokenized code
	 * @param filePath
	 * @return
	 */
	public boolean containsKnownTokens(String filePath) {

		List<Token> tokens = null;
		try {
			tokens = readTokens(filePath);
		}catch(StackOverflowError e){
			System.err.println("STACKOVERFLOW DURING LEXICAL ANALYSIS!");
			return false;
		}

		for(int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);

			if(t.getType() == JavaLexer.Identifier) {

				String tokenName = t.getText();

				int j = i + 1;

				boolean expectDOT = true;
				while(j < tokens.size()) {
					Token nextToken = tokens.get(j);
					if(expectDOT) {
						if(nextToken.getType() == JavaLexer.DOT) {
							tokenName += nextToken.getText();
							expectDOT = false;
						} else {
							i = j-1;
							break;
						}
					} else {
						if(nextToken.getType() == JavaLexer.Identifier) {
							tokenName += nextToken.getText();
							expectDOT = true;
						} else {
							i = j-1;
							break;
						}
					}
					j++;
				}
				if(!identifiers.containsKey(tokenName)) {
					System.out.println("UNKOWN ID: "+tokenName);
					return false;
				}

			}
			else if (t.getType() == JavaLexer.CharacterLiteral) {
				if(!characterLiteral.containsKey(t.getText())) {
					System.out.println("UNKOWN CHAR: "+t.getText());
					return false;
				}
			}
			else if (t.getType() == JavaLexer.FloatingPointLiteral) {
				if(!floatingPointLiteral.containsKey(t.getText())) {
					System.out.println("UNKOWN FLOAT: "+t.getText());
					return false;
				}
			}
			else if (t.getType() == JavaLexer.IntegerLiteral) {
				if(!integerLiteral.containsKey(t.getText())) {
					System.out.println("UNKOWN INT: "+t.getText());
					return false;
				}
			}
			else if (t.getType() == JavaLexer.StringLiteral) {
				if(!stringLiteral.containsKey(t.getText())) {
					System.out.println("UNKOWN STRING: "+t.getText());
					return false;
				}
			}
		}

		return true;
	}






	public void exportMaps(String outFile) {
		List<String> lines = new ArrayList<>();

		//lines.addAll(getKeysAndValues(identifiers));
		lines.addAll(getKeysAndValues(typeMap));
		lines.addAll(getKeysAndValues(methodMap));
		lines.addAll(getKeysAndValues(varMap));
		lines.addAll(getKeysAndValues(characterLiteral));
		lines.addAll(getKeysAndValues(floatingPointLiteral));
		lines.addAll(getKeysAndValues(integerLiteral));
		lines.addAll(getKeysAndValues(stringLiteral));

		try {
			Files.write(Paths.get(outFile), lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getKeysAndValues(Map<String, String> map){
		List<String> keysAndVals = new ArrayList<>();

		String keys = getListOfValues(map.keySet());
		String values = getListOfValues(map.values());

		keysAndVals.add(keys);
		keysAndVals.add(values);

		return keysAndVals;		
	}

	private String getListOfValues(Collection<String> collection) {

		StringBuilder sb = new StringBuilder();
		for(String s : collection) {
			sb.append(s+",");
		}

		return sb.toString();
	}



	private String analyzeIdentifier(String token, List<Token> tokens, int i) {

		// idiom
		if(idioms.contains(token)) {
			return token;
		}

		//Split the token
		String[] tokenParts = token.split("\\.");

		if(tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if(idioms.contains(lastPart)) {
				if(idioms.contains(firstPart)) {
					// idiom . idiom
					return firstPart + SPACED_DOT + lastPart; 

				} else if(types.contains(firstPart)) {
					// type_# . idiom
					return getTypeID(firstPart)	+ SPACED_DOT + lastPart;

				} else {
					// var_# . idiom
					return getVarID(firstPart) + SPACED_DOT + lastPart;
				}
			}
		}


		if(types.contains(token)) {
			// type_#
			return getTypeID(token);
		}
		
		//Check if it could be a method (the next token is a parenthesis)
		boolean couldBeMethod = false;
		if(i+1 < tokens.size()) {
			Token t = tokens.get(i+1);
			if(t.getType() == JavaLexer.LPAREN) {
				couldBeMethod = true;
			}
		}
		//MethodReference check (Type : : Method)
		if(i > 2) {
			Token t1 = tokens.get(i-1);
			Token t2 = tokens.get(i-2);
			
			if(t1.getType() == JavaLexer.COLON && t2.getType() == JavaLexer.COLON) {
				couldBeMethod = true;
			}
		}		

		if(methods.contains(token) && couldBeMethod) {
			// method_#
			return getMethodID(token);
		}


		if(tokenParts.length > 1) {
			String lastPart = tokenParts[tokenParts.length-1];
			String firstPart = token.substring(0, token.length()-lastPart.length()-1);

			if(methods.contains(lastPart) && couldBeMethod) {
				if(idioms.contains(firstPart)) {
					// idiom . method_#
					return firstPart + SPACED_DOT + getMethodID(lastPart);

				} else if (types.contains(firstPart)) {
					// type . method_#
					return getTypeID(firstPart)	+ SPACED_DOT + getMethodID(lastPart);

				} else {
					// var_# . method_#
					return getVarID(firstPart) + SPACED_DOT + getMethodID(lastPart);
				}
			}
		}
		
		// var_#
		return getVarID(token);

	}




	//------------------ IDs ----------------------


	private String getTypeID(String token) {
		if (typeMap.containsKey(token)){
			return typeMap.get(token);
		} else {
			count_types += 1;
			String ID = "TYPE_" + count_types;
			typeMap.put(token, ID);
			return ID;
		}
	}

	private String getVarID(String token) {
		if (varMap.containsKey(token)){
			return varMap.get(token);
		} else {
			count_vars += 1;
			String ID = "VAR_" + count_vars;
			varMap.put(token, ID);
			return ID;
		}
	}

	private String getMethodID(String token) {
		if (methodMap.containsKey(token)){
			return methodMap.get(token);
		} else {
			count_methods += 1;
			String ID = "METHOD_" + count_methods;
			methodMap.put(token, ID);
			return ID;
		}
	}


	//------------------ LITERALS ----------------------

	private String getCharacterID(Token t) {
		if(idioms.contains(t.getText())) {
			return t.getText();
		} else if (characterLiteral.containsKey(t.getText())) {
			return characterLiteral.get(t.getText());
		} else {
			count_character += 1;
			String Id = "CHAR_" + count_character;
			characterLiteral.put(t.getText(), Id);
			return Id;
		}
	}

	private String getFloatingPointID(Token t) {
		if(idioms.contains(t.getText())) {
			return t.getText();
		} else if (floatingPointLiteral.containsKey(t.getText())) {
			return floatingPointLiteral.get(t.getText());
		} else {
			count_floatingpoint += 1;
			String Id = "FLOAT_" + count_floatingpoint;
			floatingPointLiteral.put(t.getText(), Id);
			return Id;
		}
	}

	private String getIntegerID(Token t) {
		if(idioms.contains(t.getText())) {
			return t.getText();
		} else if (integerLiteral.containsKey(t.getText())) {
			return integerLiteral.get(t.getText());
		} else {
			count_integer += 1;
			String Id = "INT_" + count_integer;
			integerLiteral.put(t.getText(), Id);
			return Id;
		}
	}

	private String getStringID(Token t) {
		if(idioms.contains(t.getText())) {
			return t.getText();
		} else if (stringLiteral.containsKey(t.getText())) {
			return stringLiteral.get(t.getText());
		} else {
			count_string += 1;
			String Id = "STRING_" + count_string;
			stringLiteral.put(t.getText(), Id);
			return Id;
		}
	}


	//------------------ SETTERS ----------------------

	public void setIdioms(Set<String> idioms) {
		this.idioms = idioms;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	public void setMethods(Set<String> methods) {
		this.methods = methods;
	}








	private String getQualidiedIdentifierId(String token) {
		if (identifiers.containsKey(token)) {
			return identifiers.get(token);
		} else {
			count_identifiers += 1;
			String Id = "ID_" + count_identifiers;
			identifiers.put(token, Id);
			return Id;
		}
	}

	private String getIdentifierId(Token t) {
		if (identifiers.containsKey(t.getText())) {
			return identifiers.get(t.getText());
		} else {
			count_identifiers += 1;
			String Id = "ID_" + count_identifiers;
			identifiers.put(t.getText(), Id);
			return Id;
		}
	}


}
