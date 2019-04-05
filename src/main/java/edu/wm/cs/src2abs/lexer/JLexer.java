package edu.wm.cs.src2abs.lexer;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.DefaultParser;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.ParseException;

import edu.wm.cs.compiler.tools.generators.scanners.JavaLexer;

public class JLexer extends SimpleFileVisitor<Path> {

	private boolean normalize = false;
	private List<String> filesAnalyzed = new ArrayList<>();
	private Map<String, Integer> vocabulary = new HashMap<>();
	private int count = 0;
	private Map<String, String> Identifiers = new HashMap<>();
	
	public String visitFile2(Path path) throws IOException {	
		InputStream inputStream = new FileInputStream(path.toAbsolutePath().toString());
		JavaLexer jLexer = new JavaLexer(new ANTLRInputStream(inputStream));
		
		StringBuilder sb = new StringBuilder();
		
		for (Token t = jLexer.nextToken(); t.getType() != Token.EOF; t = jLexer.nextToken()) {
			String token = "";
			if(t.getType() == JavaLexer.Identifier) {
				token = getIdentifierID(t);
			} else {
				token = t.getText();
			}
			sb.append(token + " ");
		}
		
		return sb.toString();	
	}
	
	private String getIdentifierID(Token t) {
		if (Identifiers.containsKey(t.getText())) {
			return Identifiers.get(t.getText());
		} else {
			count += 1;
			String Id = "$" + count;
			Identifiers.put(t.getText(), Id);
			return Id;
		}
	}
	
	
	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {
		if (!attr.isRegularFile() || !path.getFileName().toString().endsWith(".java") || attr.size() == 0L)
			return CONTINUE;
		
		InputStream inputStream = new FileInputStream(path.toAbsolutePath().toString());
		JavaLexer jLexer = new JavaLexer(new ANTLRInputStream(inputStream));
		
		StringJoiner line = new StringJoiner(" ");
		List<String> lines = new ArrayList<>();
		
		int previousLine = 1;
		
		for (Token t = jLexer.nextToken(); t.getType() != Token.EOF; t = jLexer.nextToken()) {
			System.out.println(t.getText() + " " + jLexer.getTokenNames()[t.getType()]);
			
			this.count += 1;
			String Id = "$" + Integer.toString(this.count);
			
			if (t.getLine() == previousLine) {
				line.add(this.getText(t));
			} else {
				lines.add(line.toString());
				line = new StringJoiner(" ");
				line.add(this.getText(t));
				previousLine = t.getLine();
			}
		}
		lines.add(line.toString()); // Flush last line.
		lines.removeIf(String::isEmpty); // Remove first element (if necessary).
		
		Path lexPath = Paths.get(path.toAbsolutePath().toString().replaceAll(".java$", ".lex"));
		Files.write(lexPath, lines, StandardCharsets.UTF_8);

		this.filesAnalyzed.add(path.getFileName().toString());
		
		return CONTINUE;
	}

	private String getText(Token token) {
        String text = token.getText();
        
		if (this.normalize) {
			switch (token.getType()) {
			//case JavaLexer.BooleanLiteral:
			//	text = "<BOOLEAN_LITERAL>";
            //	break;
			case JavaLexer.CharacterLiteral:
				text = "<CHARACTER_LITERAL>";
            	break;
			case JavaLexer.FloatingPointLiteral:
				text = "<FLOATING_POINT_LITERAL>";
            	break;
			case JavaLexer.IntegerLiteral:
				text = "<INTEGER_LITERAL>";
            	break;
			//case JavaLexer.NullLiteral:
			//	text = "<NULL_LITERAL>";
            //	break;
			case JavaLexer.StringLiteral:
				text = "<STRING_LITERAL>";
            	break;
			}
		}

		this.vocabulary.compute(text, (k, v) -> v = token.getType());
        return text;
	}

	public static void main(String[] args) throws IOException {
		JLexer lexer = new JLexer();
		String file = "/scratch/cawatson.scratch/input/program.java";
		String out = lexer.visitFile2(Paths.get(file));
		System.out.println(out);
	}
	
	
//	public static void mainOld(String[] args) throws IOException, ParseException {
//		Option input = Option.builder("i")
//							 .longOpt("input")
//							 .desc("TODO")
//							 .required()
//							 .hasArg()
//							 .argName("TODO")
//							 .build();
//		Option output = Option.builder("o")
//							  .longOpt("output")
//							  .desc("TODO")
//							  .required()
//							  .hasArg()
//							  .argName("TODO")
//							  .build();
//		Option version = Option.builder("v")
//							   .longOpt("version")
//							   .desc("TODO")
//							   .required()
//							   .hasArg()
//							   .argName("TODO")
//							   .build();
//		Options options = new Options();
//		options.addOption(input);
//		options.addOption(output);
//		options.addOption(version);
//		options.addOption("n", "normalize", false, "TODO");
//
//		CommandLineParser parser = new DefaultParser();
//		CommandLine cmd;
//		try {
//			cmd = parser.parse(options, args);
//		} catch (ParseException e) {
//			HelpFormatter formatter = new HelpFormatter();
//			formatter.printHelp("java -jar path/to/jlexer.jar", options, true);
//			throw e;
//		}
//
//        JLexer jLexer = new JLexer();
//
//		if (cmd.hasOption("n"))
//			jLexer.normalize = true;
//
//		Files.walkFileTree(Paths.get(cmd.getOptionValue("i")), jLexer);
//
//		String out = cmd.getOptionValue("o") + File.separator + cmd.getOptionValue("v");
//
//		Path log = Paths.get(out + ".log");
//		Files.createDirectories(log.getParent());
//		Files.write(log, jLexer.filesAnalyzed, StandardCharsets.UTF_8);
//
//		Path vocab = Paths.get(out + ".vocab");
//		Files.createDirectories(vocab.getParent());
//		List<String> histogram = jLexer.vocabulary.entrySet().stream()
//			.sorted(Map.Entry.comparingByValue())
//			.map(Object::toString)
//			.collect(Collectors.toList());
//		Files.write(vocab, histogram, StandardCharsets.UTF_8);
//	}

}
