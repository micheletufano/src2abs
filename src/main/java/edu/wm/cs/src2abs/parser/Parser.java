package edu.wm.cs.src2abs.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.type.Type;

import edu.wm.cs.src2abs.code.SourceCodeAnalyzer;


public class Parser {

	public enum CodeGranularity { METHOD, CLASS; }
	private Set<String> types = new HashSet<String>();
	private Set<String> methods = new HashSet<String>();
	private CodeGranularity granularity; 
	
	public Parser() {
		this.granularity = CodeGranularity.METHOD;
	}
	
	public Parser(CodeGranularity granularity) {
		this.granularity = granularity;
	}
	
	
	public void parseFile(String filePath) {
		
		String sourceCode = SourceCodeAnalyzer.readSourceCode(filePath);

		//Remove comments and annotations
		sourceCode = SourceCodeAnalyzer.removeCommentsAndAnnotations(sourceCode);
		
		parse(sourceCode);
	}
	
	
	public void parseCode(String sourceCode) {
		//Remove comments and annotations
		sourceCode = SourceCodeAnalyzer.removeCommentsAndAnnotations(sourceCode);
		
		parse(sourceCode);
	}
	
	
	
	
	public void parse(String sourceCode) {

		
		//Check Granularity
		String sourceCodeClass = "";
		if(granularity == CodeGranularity.METHOD) {
			sourceCodeClass = "public class DummyClass {" + sourceCode + "}";
		} else {
			sourceCodeClass = sourceCode;
		}		

		// create compilation unit
		CompilationUnit cu = JavaParser.parse(sourceCodeClass);
		
		// create set of types
		List<Type> type = cu.getNodesByType(Type.class).stream().collect(Collectors.toList());
		for(Type t: type) {
			String[] addtypes = filterString(t.asString());
			for(int i=0; i < addtypes.length; i++) {
				types.add(addtypes[i]);
			}
		}

		// create set of methods and insert declared methods
		List<MethodDeclaration> method = cu.getNodesByType(MethodDeclaration.class).stream().collect(Collectors.toList());
		for(MethodDeclaration m: method) {
			methods.add(m.getNameAsString());
		}

		// insert referenced methods into methods set
		List<MethodCallExpr> methodcall = cu.getNodesByType(MethodCallExpr.class).stream().collect(Collectors.toList());
		for(MethodCallExpr mc: methodcall) {
			methods.add(mc.getNameAsString());
		}

		// insert scope of methods into types
		List<MethodCallExpr> methodscope = cu.getNodesByType(MethodCallExpr.class).stream().collect(Collectors.toList());
		for(MethodCallExpr ms: methodscope) {
			
			// is a scope is present in the method call
			if(ms.getScope().isPresent()) {
				
				// if the scope is a field access expression
				if(ms.getScope().get().isFieldAccessExpr()) {
					String field = ms.getScope().get().toString();
					String[] fieldsplit = field.split("\\.");
					String[] letters = (fieldsplit[fieldsplit.length - 1]).split("");
					if(!letters[0].equals(letters[0].toLowerCase())) {
						String[] addtypes = filterString(field);
						types.addAll(Arrays.asList(addtypes));
					}
				}
				
				// if the scope is a name expression
				if(ms.getScope().get().isNameExpr()) {
					String name = ms.getScope().get().toString();
					String[] namesplit = name.split("\\.");
					String[] letters = namesplit[namesplit.length - 1].split("");
					if(!letters[0].equals(letters[0].toLowerCase())) {
						String[] addtypes = filterString(name);
						types.addAll(Arrays.asList(addtypes));
					}
				}
			}
		}
		
		// insert scope of methods into types
		List<MethodReferenceExpr> methodref = cu.getNodesByType(MethodReferenceExpr.class).stream().collect(Collectors.toList());
		for(MethodReferenceExpr mf: methodref) {
			methods.add(mf.getIdentifier());
		}
		
	}
	
	public String[] filterString(String typeString){
		String[] listString;
		typeString = typeString.replaceAll("\\[", " ");
		typeString = typeString.replaceAll("\\]", " ");
		typeString = typeString.replaceAll("\\(", " ");
		typeString = typeString.replaceAll("\\)", " ");
		typeString = typeString.replaceAll(">", " ");
		typeString = typeString.replaceAll("<", " ");
		typeString = typeString.replaceAll("\\{", " ");
		typeString = typeString.replaceAll("\\}", " ");
		typeString = typeString.replaceAll("\\,", " ");
		typeString = typeString.replaceAll("\\?", " ");
		typeString = typeString.replaceAll("extends", " ");
		typeString = typeString.replaceAll("implements", " ");
		listString = typeString.split(" ");
		return listString;
	}
	
	// getter method for getting the types set
	public Set<String> getTypes(){
		return types;
	}
	
	// getter method for getting the methods set
	public Set<String> getMethods(){
		return methods;
	}
	
}
