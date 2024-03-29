package simse.codegenerator.animations;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CharacterIdleBackGenerator {

	private File dir;
	
	public CharacterIdleBackGenerator(File dir) {
		this.dir = dir;
	}

	public void generate(){
		// TODO Auto-generated method stub
	    File characterIdleBackFile = new File(dir,
	            ("simse\\animation\\CharacterIdleBack.java"));
	    
	        if (characterIdleBackFile.exists()) {
	        	characterIdleBackFile.delete(); // delete old version of file
	        }
		
		try {
			
		FileReader reader = new FileReader("res\\static\\animations\\CharacterIdleBack.txt");
		FileWriter writer = new FileWriter(characterIdleBackFile);
	
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
