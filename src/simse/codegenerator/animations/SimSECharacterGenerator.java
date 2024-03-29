package simse.codegenerator.animations;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SimSECharacterGenerator {

	private File dir;
	
	public SimSECharacterGenerator(File dir) {
		this.dir = dir;
	}

	public void generate() {
		// TODO Auto-generated method stub
	    File simSECharacterFile = new File(dir,
	            ("simse\\animation\\SimSECharacter.java"));
	    
	        if (simSECharacterFile.exists()) {
	        	simSECharacterFile.delete(); // delete old version of file
	        }
		
		try {
	        
		FileReader reader = new FileReader("res\\static\\animations\\SimSECharacter.txt");
		FileWriter writer = new FileWriter(simSECharacterFile);
	
		String fileContents = "";
		int index;
		
		while ((index = reader.read()) != -1) {
			fileContents += (char)index;
		}
		writer.write(fileContents);
		
		reader.close();
		writer.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
