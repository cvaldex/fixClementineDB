package cl.cvaldex.fixXlementineDB.dto;

public class SongDTO {
	String fileName;
	long length;
	int bitrate;
	int sampleRate;
	String artist;
	String album;
	String title;
	boolean update;
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(artist);
		builder.append(" - ");
		builder.append(album);
		builder.append(" - ");
		builder.append(title);
		builder.append(" - ");
		builder.append(fileName);
		builder.append(" - ");
		builder.append(bitrate);
		builder.append(" - ");
		builder.append(length);
		
		return builder.toString();
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public int getBitrate() {
		return bitrate;
	}
	public void setBitrate(int bitrate) {
		this.bitrate = bitrate;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isUpdate() {
		return update;
	}
	public void setUpdate(boolean update) {
		this.update = update;
	}
}
