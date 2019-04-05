package edu.wm.cs.src2abs.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CodeTranslator {
	
	public static void translate(String lexedCodeFile, String out, String mapsDir) {
		List<String> translatedCodeLines = new ArrayList<>();
		List<String> lexedCodeLines = null;
		
		try {
			lexedCodeLines = Files.readAllLines(Paths.get(lexedCodeFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < lexedCodeLines.size(); i++) {
			
			CodePrinter codePrinter = new CodePrinter(mapsDir+File.separator+i);
			
			String code = codePrinter.printCode(lexedCodeLines.get(i));
			translatedCodeLines.add(code);			
		}
		
		try {
			Files.write(Paths.get(out), translatedCodeLines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
