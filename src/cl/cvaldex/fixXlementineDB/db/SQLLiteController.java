package cl.cvaldex.fixXlementineDB.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import cl.cvaldex.fixXlementineDB.dto.SongDTO;

public class SQLLiteController {
	
	public static Collection<SongDTO> getWrongSongs( String dbFileName ) {
		Collection<SongDTO> fileNames = new ArrayList<SongDTO>();
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT artist, album, title, filename FROM songs WHERE length < 0;" );
			
			SongDTO song = null;
			
			while ( rs.next() ) {
				song = new SongDTO();
				song.setArtist(rs.getString("artist"));
				song.setAlbum(rs.getString("album"));
				song.setTitle(rs.getString("title"));
				song.setFileName(rs.getString("filename"));
				song.setUpdate(true);
				
				fileNames.add(song);
			}
			
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		
		return fileNames;

	}
	
	public static void updateSongs(String dbFileName , Collection<SongDTO> filesToFix){
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);
			connection.setAutoCommit(false);
			System.out.println("Opened database successfully");

			String updateQuery = "UPDATE songs SET length = ? , bitrate = ? , samplerate = ? WHERE artist = ? AND album = ? and title = ?;";
			
			for(SongDTO song : filesToFix){
				if(song.isUpdate()){
					preparedStatement = connection.prepareStatement(updateQuery);
					preparedStatement.setLong(1, song.getLength());
					preparedStatement.setInt(2, song.getBitrate());
					preparedStatement.setInt(3, song.getSampleRate());
					preparedStatement.setString(4 , song.getArtist());
					preparedStatement.setString(5 , song.getAlbum());
					preparedStatement.setString(6 , song.getTitle());

					preparedStatement.executeUpdate();
				}
			}

			connection.commit();

			preparedStatement.close();
			connection.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public static boolean existRow( String dbFileName , SongDTO song ) {
		boolean existRow = false;
		Connection c = null;
		PreparedStatement preparedStatement = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			preparedStatement = c.prepareStatement( "SELECT * FROM songs WHERE artist = ? AND album = ? and title = ?;" );
			preparedStatement.setString(1 , song.getArtist());
			preparedStatement.setString(2 , song.getAlbum());
			preparedStatement.setString(3 , song.getTitle());
			
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while ( rs.next() ) {
				existRow = true;
			}
			
			rs.close();
			preparedStatement.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		
		return existRow;
	}
}
