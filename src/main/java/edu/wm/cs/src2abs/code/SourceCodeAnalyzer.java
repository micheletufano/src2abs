package edu.wm.cs.src2abs.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SourceCodeAnalyzer {

	
	
	public static String readSourceCode(String filePath) {
		
		String sourceCode = "";
		
		try {
			sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sourceCode;	
	}
	
	
	public static String removeCommentsAndAnnotations(String sourceCode) {
		//remove comments
		sourceCode = sourceCode.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");

		//remove annotations
		sourceCode = sourceCode.replaceAll("@.+", "");

		return sourceCode;
	}
	
}
