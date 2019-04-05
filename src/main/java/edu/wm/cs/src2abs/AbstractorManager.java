package edu.wm.cs.src2abs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import edu.wm.cs.src2abs.lexer.JLexerTokenizer;
import edu.wm.cs.src2abs.parser.Parser;
import edu.wm.cs.src2abs.vocabulary.IdiomManager;

public class AbstractorManager {


	public void abstractCode(Parser.CodeGranularity granularity, String inputCodePath, String outputCodePath, String idiomsFilePath) {

		//Check inputs
		checkInputs(inputCodePath, idiomsFilePath, outputCodePath);
		String mapOutputFile = outputCodePath+".map";
		
		//Idioms
		Set<String> idioms = IdiomManager.readIdioms(idiomsFilePath);

		//Parser
		Parser parser = new Parser(granularity);
		try {
			parser.parseFile(inputCodePath);
		} catch(StackOverflowError e){
			System.err.println("StackOverflow during parsing!");
		} catch (Exception e) {
			System.err.println("Parsing ERROR!");
		}


		//Tokenizer
		JLexerTokenizer tokenizer = new JLexerTokenizer();

		//System.out.println("Types: "+parser.getTypes());
		//System.out.println("Methods: "+parser.getMethods());

		tokenizer.setTypes(parser.getTypes());
		tokenizer.setMethods(parser.getMethods());
		tokenizer.setIdioms(idioms);

		String abstractCode = tokenizer.tokenize(inputCodePath);


		//Write output files
		writeAbstractCode(abstractCode, outputCodePath);
		tokenizer.exportMaps(mapOutputFile);
		
		System.out.println("Source Code Abstracted successfully!");
		System.out.println("Abstracted Code: "+outputCodePath);
		System.out.println("Mapping: "+mapOutputFile);
	}

	public void abstractCodePair(Parser.CodeGranularity granularity, String inputCodePath1, String inputCodePath2, String outputCodePath1, String outputCodePath2, String idiomsFilePath) {

		//Check inputs
		checkInputs(inputCodePath1, idiomsFilePath, outputCodePath1);
		checkInputs(inputCodePath2, idiomsFilePath, outputCodePath2);
		String mapOutputFile = outputCodePath1+".map";
		
		//Idioms
		Set<String> idioms = IdiomManager.readIdioms(idiomsFilePath);

		//Parser
		Parser parser = new Parser(granularity);
		try {
			parser.parseFile(inputCodePath1);
			parser.parseFile(inputCodePath2);
		} catch(StackOverflowError e){
			System.err.println("StackOverflow during parsing!");
		} catch (Exception e) {
			System.err.println("Parsing ERROR!");
		}


		//Tokenizer
		JLexerTokenizer tokenizer = new JLexerTokenizer();

		//System.out.println("Types: "+parser.getTypes());
		//System.out.println("Methods: "+parser.getMethods());

		tokenizer.setTypes(parser.getTypes());
		tokenizer.setMethods(parser.getMethods());
		tokenizer.setIdioms(idioms);

		String abstractCode1 = tokenizer.tokenize(inputCodePath1);
		String abstractCode2 = tokenizer.tokenize(inputCodePath2);

		//Write output files
		writeAbstractCode(abstractCode1, outputCodePath1);
		writeAbstractCode(abstractCode2, outputCodePath2);
		tokenizer.exportMaps(mapOutputFile);
		
		System.out.println("Source Code Abstracted successfully!");
		System.out.println("Abstracted Code: "+outputCodePath1 + " and " + outputCodePath2);
		System.out.println("Mapping: "+mapOutputFile);
	}
	
	
	private void writeAbstractCode(String abstractCode, String outputCodePath) {
		try {
			Files.write(Paths.get(outputCodePath), abstractCode.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void checkInputs(String inputCodePath, String idiomsFilePath, String outputCodePath) {
		checkFileExists(inputCodePath, "Input code file does not exist: ");
		checkFileExists(idiomsFilePath, "Idiom file does not exist: ");
		checkParentFolderExists(outputCodePath, "Output folder does not exist: ");
	}

	private void checkParentFolderExists(String filePath, String error) {
		if(!Files.isDirectory(Paths.get(filePath).getParent())) {
			try {
				throw new FileNotFoundException(error+filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void checkFileExists(String filePath, String error) {
		if(!fileExists(filePath)){
			try {
				throw new FileNotFoundException(error+filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean fileExists(String path) {
		return new File(path).isFile();
	}

}
