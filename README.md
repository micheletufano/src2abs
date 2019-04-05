# src2abs
`src2abs` is a tool that *abstracts* Java source code.

It transforms this source code:
```
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }}
```

into this abstract textual representation:
```
public class VAR_1 { public static void METHOD_1 ( TYPE_1 [ ] VAR_2 ) { VAR_3 . METHOD_2 ( STRING_1 ) ; } }
```





## Installation
```
mvn clean
mvn install:install-file -Dfile="lib/javalexer.jar" -DgroupId="edu.wm.cs" -DartifactId="javalexer" -Dversion="1" -Dpackaging="jar"
mvn package
```

## Usage

### Single mode
```
java -jar src2abs-0.1-jar-with-dependencies.jar single <code_granularity> <input_code_path> <output_abstract_path> <idioms_path>
```

Example


### Pair mode
```
java -jar src2abs-0.1-jar-with-dependencies.jar pair <code_granularity> <input_code_A_path> <input_code_B_path> <output_abstract_A_path> <output_abstract_B_path> <idioms_path>
```
