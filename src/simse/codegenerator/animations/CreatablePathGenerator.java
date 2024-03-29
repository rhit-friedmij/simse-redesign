package simse.codegenerator.animations;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CreatablePathGenerator {

	private File dir;
	
	public CreatablePathGenerator(File dir) {
		this.dir = dir;
	}

	public void generate() {
		// TODO Auto-generated method stub
	    File creatablePathFile = new File(dir,
	            ("simse\\animation\\CreatablePath.java"));
	    
	        if (creatablePathFile.exists()) {
	        	creatablePathFile.delete(); // delete old version of file
	        }
		
	    try {
		
		FileReader reader = new FileReader("res\\static\\animations\\CreatablePath.txt");
		FileWriter writer = new FileWriter(creatablePathFile);
	
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
