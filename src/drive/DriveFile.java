package drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class DriveFile extends GDrive{
	private String fileID;
	private String fileName;
	private String filePath;
	private String fileType;
	private String fileExtension;
	private boolean isFolder;
	private ArrayList<DriveFile> fileParents;
	private ArrayList<DriveFile> fileChildren;
	private File gFile;

	
	DriveFile(File file) throws GeneralSecurityException, IOException{
		try {
			gFile=file;
		
		this.fileID=gFile.getId();
		fileName=gFile.getName();
		fileType=gFile.getMimeType();
		fileExtension=gFile.getFileExtension();
		fileChildren = new ArrayList<>();
		fileParents= new ArrayList<>();
//		System.out.println(gFile.getParents());
//		if(gFile.getParents()!=null) {
//			fileParents=gFile.getParents();
//		}else {
//			fileParents=new ArrayList<>();
//		}
		
//		System.out.println(file+" initialised");
		} catch(Exception e) {
			System.err.println(e);
			System.out.println(file+"(DriveFile) has failed initialisation");
		}
	}
	
	DriveFile(String fileid) throws GeneralSecurityException, IOException{
		try {
		
		GDrive googleDrive = new GDrive();
		
		gFile =googleDrive.gDriveObject.files().get(fileid).execute();
		fileID=fileid;
		fileName=gFile.getName();
		System.out.println(fileName);
		fileType=gFile.getMimeType();
		fileExtension=gFile.getFileExtension();

		fileChildren = new ArrayList<>();
		fileParents = new ArrayList<>();


//		System.out.println(gFile.getParents());

		
		System.out.println(fileid+" initialised");
		} catch(Exception e) {
			System.err.println(e);
			System.out.println(fileid+"(DriveFile) has failed initialisation");
		}
	}

	public String toString() {
		return fileID;
	}
	public void setChildren(ArrayList<DriveFile> fileList){
		if(!fileList.isEmpty()) {
			
		
		for(DriveFile file : fileList) {
			fileChildren.add(file);
		}
		}
//		fileChildren=fileList;
	}
	
	public void setParents(ArrayList<DriveFile> fileList){
		if(!fileList.isEmpty()) {
			
		
		for(DriveFile file : fileList) {
			fileParents.add(file);
		}
		}
//		fileChildren=fileList;
	}
	public ArrayList<DriveFile> getChildren(){
		return fileChildren;
	}
	public ArrayList<DriveFile> getParents() {
		return fileParents;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public String getID() {
		return fileID;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFilePath(String pathOfFile) {
		filePath=pathOfFile;
	}
	public String getFilePath() {
		return filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public File getFileObject() {
		return gFile;
		
	}


//	public boolean download() {
//		
//	}
//	
//	public boolean convert() {
//		
//	}

}
