package Security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class VirtualFile{

	private static final int DEFAULT_BUFFER_SIZE = 1024;
	private int bufferIndex = 0;
	private String fileName = "";
	private String filePath = "";
	private int contentLength = 0;
	private int maxContentSize = 0;
	
	private List<byte[]> buffer = new ArrayList<byte[]>();
	
	public VirtualFile(){
		this("", 0);
	}
	public VirtualFile(String fileName, int maxContentSize){
		this("", fileName, maxContentSize);
	}
	
	public VirtualFile(String filePath, String fileName, int maxContentSize){
		setFilePath(filePath);
		setFileName(fileName);
		setMaxContentSize(maxContentSize);
	}
	
	public String getFileName(){
   	return fileName;
   }
	public void setFileName(String fileName){
   	this.fileName = fileName;
   }
	public String getFilePath(){
   	return filePath;
   }
	public void setFilePath(String filePath){
   	this.filePath = filePath;
   }
	public int getMaxContentSize(){
   	return maxContentSize;
   }
	public void setMaxContentSize(int maxContentSize){
   	this.maxContentSize = maxContentSize;
   }

	public void write(int byteRead, byte[] data) throws Exception{
		if((maxContentSize > 0) && ((contentLength + byteRead) > maxContentSize)){
			throw new Exception();
		}
		else{
			if(byteRead > data.length){
				byteRead = data.length;
			}
			byte[] trimmedData = new byte[byteRead];
			
			for(int i = 0; i < trimmedData.length; i++){
		      trimmedData[i] = data[i];
	      }
			buffer.add(trimmedData);
			contentLength += byteRead;
		}
	}
	
	public byte[] read(){
		return read(bufferIndex++);
	}
	
	public byte[] read(int index){
		if(index < buffer.size()){
			return buffer.get(index);
		}
		else{
			return null;
		}
	}
	
	public int getBufferSize(){
		return buffer.size();
	}
	
	public void loadBuffer(File file) throws Exception{
		FileInputStream input = null;
		try{
			input = new FileInputStream(file);
			loadBuffer(input);
		}
		catch(IOException ex){
			//logger.log(Level.SEVERE, "Unable to load buffer", ex);
			log.error("Exception occured in VirtualFile.loadBuffer :: Unable to load buffer", ex);
		}
		finally{
			//DCalFSUtil.close(input);
		}
	}
	
	public void loadBuffer(InputStream input) throws Exception{
		clear();

		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int byteRead = -1;
		try{
			while((byteRead = input.read(buffer)) > -1){
         	write(byteRead, buffer);
         }
      }
      catch (IOException e){
			clear();
			// logger.log(Level.SEVERE, "Unable to load buffer", e);
			log.error("Exception occured in VirtualFile.loadBuffer :: Unable to load buffer", e);
      }
	}
	
	public void clear(){
		buffer.clear();
		contentLength = 0;
		bufferIndex = 0;
	}
	
	public int getLength(){
		return contentLength;
   }
	
}
