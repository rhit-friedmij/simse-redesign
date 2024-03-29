package simse.codegenerator.animations;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SpriteAnimationGenerator {

	private File dir;
	
	public SpriteAnimationGenerator(File dir) {
		this.dir = dir;
	}

	public void generate() {
		// TODO Auto-generated method stub
	    File spriteAnimationFile = new File(dir,
	            ("simse\\animation\\SpriteAnimation.java"));
	    
	        if (spriteAnimationFile.exists()) {
	        	spriteAnimationFile.delete(); // delete old version of file
	        }
		
		try {
	        
		FileReader reader = new FileReader("res\\static\\animations\\SpriteAnimation.txt");
		FileWriter writer = new FileWriter(spriteAnimationFile);
	
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
