/* MicroJava Parser Tester
   ========================
   Place this file in a subdirectory MJ
   Compile with
     javac MJ\Scanner.java MJ\Parser.java MJ\TestParser.java
   Run with
     java MJ.TestParser <inputFileName>
*/
package MJ;

import java.io.*;

public class TestParser {

	// Main method of the parser tester
	public static void main(String args[]) {
	
		String source = "C:\\Users\\Dell\\Desktop\\semestar6\\kompajleri_predavanja\\Domaci1 (1)\\test1.txt";
		System.out.println(source);
		
		try {
			Scanner.init(new InputStreamReader(new FileInputStream(source)));
			Parser.parse();
			System.out.println(Parser.errors + " errors detected");
		} catch (IOException e) {
			System.out.println("-- cannot open input file " + source);
		}
	}

}
