import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import cl.cvaldex.fixXlementineDB.db.SQLLiteController;
import cl.cvaldex.fixXlementineDB.dto.SongDTO;

/**
 *
 * @author cvaldesc
 * TODO Backup del archivo actual
 *
 */

public class FixClementineDBMain {
	public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, IOException{
		String dbFileName = "/Users/cvaldesc/Library/Application Support/Clementine/clementine.db";

		System.out.println("Obteniendo registros a actualizar");

		Collection<SongDTO> songsToFix = SQLLiteController.getWrongSongs(dbFileName);
		int archivosNoEncontrados = 0;
		int archivosErroneos = 0;

		System.out.println("Recopilando información desde archivos");

		for(SongDTO song : songsToFix){
			URL url = new URL(song.getFileName());
			Mp3File mp3file = null;

			try{
				mp3file = new Mp3File(url.getFile().replaceAll("%20", " "));

				song.setBitrate(mp3file.getBitrate());
				song.setLength(mp3file.getLengthInSeconds() * 1000000000);
				song.setSampleRate(mp3file.getSampleRate());
			}
			catch(java.io.FileNotFoundException fnfe){
				archivosNoEncontrados++;
				song.setUpdate(false);
				continue;
			}
			catch(com.mpatric.mp3agic.InvalidDataException ide){
				System.out.println("Archivo erróneo: " + song.getFileName());
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
}
