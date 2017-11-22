import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import cl.cvaldex.fixXlementineDB.backup.DBBackup;
import cl.cvaldex.fixXlementineDB.db.SQLLiteController;
import cl.cvaldex.fixXlementineDB.dto.SongDTO;

/**
 *
 * @author cvaldesc
 *
 */

public class FixClementineDBMain {
	public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, IOException{
		String dbFileName = "/Users/cvaldesc/Library/Application Support/Clementine/clementine.db";
		
		DBBackup.backupFile(dbFileName);

		System.out.println("Obteniendo registros a actualizar");

		Collection<SongDTO> songsToFix = SQLLiteController.getWrongSongs(dbFileName);
		int archivosNoEncontrados = 0;
		int archivosErroneos = 0;

		System.out.println("Recopilando información desde archivos");

		for(SongDTO song : songsToFix){
			Mp3File mp3file = null;

			try{
				mp3file = new Mp3File(getFixedFileName(song.getFileName()));
				
				song.setBitrate(mp3file.getBitrate());
				song.setLength(mp3file.getLengthInSeconds() * 1000000000);
				song.setSampleRate(mp3file.getSampleRate());
			}
			catch(java.io.FileNotFoundException fnfe){
				System.out.println("Archivo no encontrado: " + getFixedFileName(song.getFileName()));
				archivosNoEncontrados++;
				song.setUpdate(false);
				continue;
			}
			catch(com.mpatric.mp3agic.InvalidDataException ide){
				System.out.println("Archivo erróneo: " + getFixedFileName(song.getFileName()));
				archivosErroneos++;
				song.setUpdate(false);
				continue;
			}
		}

		System.out.println("Archivos no entrados: " + archivosNoEncontrados);
		System.out.println("Archivos erroneos: " + archivosErroneos);

		System.out.println("Actualizando canciones");

		SQLLiteController.updateSongs(dbFileName, songsToFix);

		System.out.println("Fin del proceso");
	}
	
	public static String getFixedFileName(String fileName) throws UnsupportedEncodingException{
		return URLDecoder.decode(fileName, "UTF-8").replace("file://" , "");
	}
}
