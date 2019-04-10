# src2abs
`src2abs` is a tool that *abstracts* Java source code.

It transforms this source code:
```
public static void main(String[] args) {
    console.println("Hello, World!");
}
```
into this abstract textual representation:
```
public static void METHOD_1 ( TYPE_1 [ ] VAR_1 ) { VAR_2 . METHOD_2 ( STRING_1 ) ; }
```
This abstract representations contains:
- Java Keywords;
- Code Separators;
- IDs in place of identifiers and literals; 
- Idioms (optionally).

### How it works
`src2abs` uses a Java Lexer to read and tokenize the source code. A Java Parser analyzes the code and discerns the type of each identifier and literal in the source code. Next, `src2abs` replaces each identifiers and literals in the stream of tokens with a unique ID which represents the type and role of the identifier/literal in the code.

Each ID `<TYPE>_#` is formed by a prefix (_i.e.,_ `<TYPE>_`) whcih represents the type and role of the identifier/literal, and a numerical ID (_i.e.,_ `#`) which is assigned sequentially when reading the code. Note that these IDs are reused when the same identifier/literal appears again in the stream of tokens. Here is the list of supported IDs: 

*Identifiers*
- `TYPE_#`
- `METHOD_#`
- `VAR_#`

*Literals*
- `INT_#`
- `FLOAT_#`
- `CHAR_#`
- `STRING_#`

### Idioms
There are some identifiers and literals that occur so often in source code that they can almost be considered keywords of the language. For example, the variable names `i`, `index`, the method names `toString()`, `indexOf()`, literals such as `0`, `\n`, `1`, etc., provide meaningful semantic information that can be helpful in a variety of tasks. We refer to these frequent identifiers and literals as *idioms*.

`src2abs` allows to specify a list of idioms (either identifier or literal values) that will be kept in the abstract representation and not replaced with IDs. For example, if the idioms `String` (a common Java type) and `args` (a common variable name) are specified, then `src2abs` will generate the following abstract source code for the previous example:
```
public static void METHOD_1 ( String [ ] args ) { VAR_1 . METHOD_2 ( STRING_1 ) ; }
```





## Installation
Clone the project and enter in the corresponding folder:
```
git clone https://github.com/micheletufano/src2abs.git
cd src2abs
```
Use Maven to install dependencies and generate the runnable jar:
```
mvn clean
mvn install:install-file -Dfile="lib/javalexer.jar" -DgroupId="edu.wm.cs" -DartifactId="javalexer" -Dversion="1" -Dpackaging="jar"
mvn package
```
The generated jar is located in the target folder:
```
target/src2abs-0.1-jar-with-dependencies.jar
```


## Usage
`src2abs` supports two usage modes:
- *Single mode*: abstracts a single piece of source code;
- *Pair mode*: abstracts two pieces of source code. It reuses the IDs already generated for shared identifiers/literals in the pair.

The single mode is suggested when analyzing code in isolation. The pair mode is recommended when analyzing the changes/evolution of the same piece of source code in a commit/revision.
Both modes can abstract code at two levels of granularities:
- Method
- Class

### Single mode
```
java -jar src2abs-0.1-jar-with-dependencies.jar single <code_granularity> <input_code_path> <output_abstract_path> <idioms_path>
```

Arguments:
- `<code_granularity>`: code granularity (*i.e.,* either `method` or `class`);
- `<input_code_path>`: path of the file containing the source code to abstract;
- `<output_abstract_path>`: path of the file (to be created) where the abstract source code will be saved; 
- `<idioms_path>`: path of the file containing the list of idioms.

### Pair mode
```
java -jar src2abs-0.1-jar-with-dependencies.jar pair <code_granularity> <input_code_A_path> <input_code_B_path> <output_abstract_A_path> <output_abstract_B_path> <idioms_path>
```
Arguments:
- `<code_granularity>`: code granularity (*i.e.,* either `method` or `class`);
- `<input_code_A_path>`: path of the *first* file containing the source code to abstract;
- `<input_code_B_path>`: path of the *second* file containing the source code to abstract;
- `<output_abstract_A_path>`: path of the file (to be created) where the *first* abstract source code will be saved;
- `<output_abstract_B_path>`: path of the file (to be created) where the *second* abstract source code will be saved;
- `<idioms_path>`: path of the file containing the list of idioms.

## Credits
`src2abs` was built by [Michele Tufano](http://www.cs.wm.edu/~mtufano/) and [Cody Watson](http://www.cs.wm.edu/~cawatson/) and used and adapted in the context of the following research projects. If you are using `src2abs` for research purposes, please cite:

- [1] On Learning Meaningful Code Changes via Neural Machine Translation
- [2] An Empirical Study on Learning Bug-Fixing Patches in the Wild via Neural Machine Translation

## Bibliography
### [1] On Learning Meaningful Code Changes via Neural Machine Translation
```
@inproceedings{Tufano-Learning-CodeChanges,
    Author = {Michele Tufano and Jevgenija Pantiuchina and Cody Watson and Gabriele Bavota and Denys Poshyvanyk},
    title = {On Learning Meaningful Code Changes via Neural Machine Translation},
    booktitle = {Proceedings of the 41st International Conference on Software Engineering},
    series = {ICSE '19},
    year = {2019},
    location = {Montréal, Candada},
    numpages = {12}
}
```
### [2] An Empirical Study on Learning Bug-Fixing Patches in the Wild via Neural Machine Translation
```
@article{DBLP:journals/corr/abs-1812-08693,
  author    = {Michele Tufano and Cody Watson and Gabriele Bavota and Massimiliano Di Penta and Martin White and Denys Poshyvanyk},
  title     = {An Empirical Study on Learning Bug-Fixing Patches in the Wild via Neural Machine Translation},
  journal   = {CoRR},
  volume    = {abs/1812.08693},
  year      = {2018}
}
```
