package cl.cvaldex.fixXlementineDB.backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DBBackup {
	public static void backupFile(String fileName) throws IOException{
		File tempFile = new File(fileName);
		
		if(tempFile.exists()){
			String dir = tempFile.getParent();
			System.out.println("Directorio de copia: " + dir);
			StringBuilder builder = new StringBuilder();
			builder.append(dir);
			builder.append(File.separator);
			int lastDot = tempFile.getName().lastIndexOf('.');
			builder.append(tempFile.getName().substring(0 , lastDot));
			builder.append(".");
			builder.append(System.currentTimeMillis());
			builder.append(tempFile.getName().substring(lastDot));
			
			//System.out.println("copiando de: " + fileName + "     a       " + builder.toString());
			
			Path sourceFile = Paths.get(fileName);
			Path targetFile = Paths.get(builder.toString());
			System.out.println("Realizando backup del arhivo " + fileName);
			Files.copy(sourceFile, targetFile,StandardCopyOption.REPLACE_EXISTING);
		}
		else{
			System.out.println("Archivo de origen no existe");
		}
	}
	
	public static void main(String [] args) throws IOException{
		backupFile("/Users/cvaldesc/Library/Application Support/Clementine/clementine.db");
	}
}
