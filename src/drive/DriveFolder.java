package drive;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class DriveFolder extends GDrive {
	private String folderID;
	private String folderName;
	private String folderPath;
	private String folderType;
	private File gFolder;
	
	private ArrayList<String> parentFolders = new ArrayList<String>();

	
	
	DriveFolder(File file) throws GeneralSecurityException, IOException{
		try {
			gFolder=file;
		
		this.folderID=gFolder.getId();
		folderName=gFolder.getName();
		folderType=gFolder.getMimeType();
		parentFolders=(ArrayList<String>) gFolder.getParents();

		
		
		System.out.println(gFolder+" initialised");
		} catch(Exception e) {
			System.err.println(e);
			System.out.println(gFolder+"(DriveFile) has failed initialisation");
		}
		
	}
	public String toString() {
		return folderID;
	}
	public String getID() {
		return folderID;
	}
	public String getType() {
		return folderType;
	}
	public String getFolderName() {
		return folderName;
	}
	public String getFolderPath() {
		return folderPath;
	}
//	public boolean download() {
//		
//	}


	
	

}



