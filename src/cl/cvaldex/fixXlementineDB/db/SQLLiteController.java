package cl.cvaldex.fixXlementineDB.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import cl.cvaldex.fixXlementineDB.dto.SongDTO;

public class SQLLiteController {
	
	private static Connection getConnection(String dbFileName) throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);
		connection.setAutoCommit(false);
		System.out.println("Opened database successfully");
		
		return connection;
	}
	
	
	public static Collection<SongDTO> getWrongSongs( String dbFileName ) {
		Collection<SongDTO> wrongSongs = new ArrayList<SongDTO>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection(dbFileName);

			Collection<SongDTO> wrongAlbums = getWrongAlbums(dbFileName);
			String selectQuery = "SELECT artist , album , title , filename FROM songs WHERE artist = ? AND album = ?;";
			
			
			for(SongDTO wrongAlbumSampleSong : wrongAlbums){
				preparedStatement = connection.prepareStatement(selectQuery);
				preparedStatement.setString(1, wrongAlbumSampleSong.getArtist());
				preparedStatement.setString(2, wrongAlbumSampleSong.getAlbum());
				
				ResultSet rs = preparedStatement.executeQuery();

				SongDTO song = null;

				while ( rs.next() ) {
					song = new SongDTO();
					song.setArtist(rs.getString("artist"));
					song.setAlbum(rs.getString("album"));
					song.setTitle(rs.getString("title"));
					song.setFileName(rs.getString("filename"));
					song.setUpdate(true);

					wrongSongs.add(song);
				}
				
				rs.close();
				preparedStatement.close();
			}
			
			
			connection.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return wrongSongs;

	}
	
	public static Collection<SongDTO> getWrongAlbums( String dbFileName ) {
		Collection<SongDTO> wrongAlbums = new ArrayList<SongDTO>();
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection(dbFileName);

			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT DISTINCT artist, album FROM songs WHERE length < 0;" );
			
			SongDTO song = null;
			
			while ( rs.next() ) {
				song = new SongDTO();
				song.setArtist(rs.getString("artist"));
				song.setAlbum(rs.getString("album"));
				
				wrongAlbums.add(song);
			}
			
			rs.close();
			stmt.close();
			connection.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return wrongAlbums;
	}
	
	public static void updateSongs(String dbFileName , Collection<SongDTO> rowsToFix){
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int rowsFixed = 0;
		
		System.out.println("Registros a actualizar: " + rowsToFix.size());
		
		try {
			connection = getConnection(dbFileName);

			String updateQuery = "UPDATE songs SET length = ? , bitrate = ? , samplerate = ? WHERE artist = ? AND album = ? and title = ?;";
			
			for(SongDTO song : rowsToFix){
				if(song.isUpdate()){
					preparedStatement = connection.prepareStatement(updateQuery);
					preparedStatement.setLong(1, song.getLength());
					preparedStatement.setInt(2, song.getBitrate());
					preparedStatement.setInt(3, song.getSampleRate());
					preparedStatement.setString(4 , song.getArtist());
					preparedStatement.setString(5 , song.getAlbum());
					preparedStatement.setString(6 , song.getTitle());

					preparedStatement.executeUpdate();
					rowsFixed++;
					
					System.out.println(song.toString());
				}
			}

			connection.commit();

			preparedStatement.close();
			connection.close();
			System.out.println("Registros actualizados: " + rowsFixed);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static boolean existRow( String dbFileName , SongDTO song ) {
		boolean existRow = false;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection(dbFileName);

			preparedStatement = connection.prepareStatement( "SELECT * FROM songs WHERE artist = ? AND album = ? and title = ?;" );
			preparedStatement.setString(1 , song.getArtist());
			preparedStatement.setString(2 , song.getAlbum());
			preparedStatement.setString(3 , song.getTitle());
			
			
			ResultSet rs = preparedStatement.executeQuery();
			
			while ( rs.next() ) {
				existRow = true;
			}
			
			rs.close();
			preparedStatement.close();
			connection.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return existRow;
	}
}
