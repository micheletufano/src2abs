package edu.wm.cs.src2abs.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodePrinter {

	private Map<String, String> identifiers = new HashMap<>();
	private Map<String, String> stringLiteral = new HashMap<>();
	private Map<String, String> characterLiteral = new HashMap<>();
	private Map<String, String> integerLiteral = new HashMap<>();
	private Map<String, String> floatingPointLiteral = new HashMap<>();
	
	private static final String IDENT_PREFIX = "ID_";
	private static final String STRING_PREFIX = "STRING_";
	private static final String CHAR_PREFIX = "CHAR_";
	private static final String INT_PREFIX = "INT_";
	private static final String FLOAT_PREFIX = "FLOAT_";

	
	public CodePrinter(String mapFile) {
		importMaps(mapFile);
	}
	
	public String printCode(String lexedCode) {
		
		StringBuilder sb = new StringBuilder();
		
		String[] tokens = lexedCode.split(" ");
		
		for(String token : tokens) {
			if(token.startsWith(IDENT_PREFIX)) {
				sb.append(identifiers.get(token));
			} else if (token.startsWith(STRING_PREFIX)) {
				sb.append(stringLiteral.get(token));
			} else if (token.startsWith(CHAR_PREFIX)) {
				sb.append(characterLiteral.get(token));
			} else if (token.startsWith(INT_PREFIX)) {
				sb.append(integerLiteral.get(token));
			} else if (token.startsWith(FLOAT_PREFIX)) {
				sb.append(floatingPointLiteral.get(token));
			} else {
				sb.append(token);
			}
			sb.append(" ");
		}
		
		return sb.toString();	
	}
	
	
	private void importMaps(String mapFile) {
		
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(mapFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fillMap(identifiers, lines.get(0), lines.get(1));
		fillMap(characterLiteral, lines.get(2), lines.get(3));
		fillMap(floatingPointLiteral, lines.get(4), lines.get(5));
		fillMap(integerLiteral, lines.get(6), lines.get(7));
		fillStringMap(stringLiteral, lines.get(8), lines.get(9));	
		
	}
	
	
	private void fillMap(Map<String, String> map, String keys, String values) {
		
		if(keys.isEmpty()) {
			return;
		}
			
		String[] keysSplitted = keys.split(",");
		String[] valuesSplitted = values.split(",");
				
		for(int i = 0; i < keysSplitted.length; i++) {
			map.put(valuesSplitted[i], keysSplitted[i]);
		}		
	}
	
	
	private void fillStringMap(Map<String, String> map, String keys, String values) {
		
		if(keys.isEmpty()) {
			return;
		}
		
		String[] keysSplitted = keys.substring(1).split("\",\"");
		String[] valuesSplitted = values.split(",");
				
		for(int i = 0; i < keysSplitted.length; i++) {
			map.put(valuesSplitted[i], "\""+keysSplitted[i]+"\"");
		}		
	}
	
	
	
}
