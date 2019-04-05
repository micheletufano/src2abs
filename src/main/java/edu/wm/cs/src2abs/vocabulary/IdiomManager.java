package edu.wm.cs.src2abs.vocabulary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdiomManager {

	public static Set<String> readIdioms(String filePath){
		
		Set<String> idioms = new HashSet<>();

		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			idioms = stream.collect(Collectors.toSet());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return idioms;
		
	}
	
	
}
