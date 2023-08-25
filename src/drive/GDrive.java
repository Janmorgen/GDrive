package drive;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.print.DocFlavor.URL;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.io.Resources;

public class GDrive {
	
	private String ACCOUNT;
	private String LOCAL_DIR;
	private String DOWNLOAD_PATH;
	private String FILE_SEPERATOR;
	
	public static Drive gDriveObject;
	
	 private static final String APPLICATION_NAME = "Java Drive Interface";
	 
	    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	 
	    // Directory to store user credentials for this application.

	 
	    //
	    // Global instance of the scopes required by this quickstart. If modifying these
	    // scopes, delete your previously saved credentials/ folder.
	    //
	    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	 
	    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
	 
//	        java.net.URL resource = GDrive.class.getResource("/resources/client_secret.json");
//	        System.out.println(resource.toString());
	        
//	        java.io.File clientSecretFilePath=new java.io.File(resource.toString().replace("file:", ""));
	        java.io.File clientSecretFilePath=new java.io.File(LOCAL_DIR+FILE_SEPERATOR+"ClientSecrets"+FILE_SEPERATOR+"client_secret.json");
	        

	 
	        if (!clientSecretFilePath.exists()) {
	        	System.out.println(clientSecretFilePath.toString());
	            throw new FileNotFoundException();
	        }
	 
	        // Load client secrets.
	        InputStream in = new FileInputStream(clientSecretFilePath);
	 
	        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
	 
	        // Build flow and trigger user authorization request.
	        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
	                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(LOCAL_DIR+FILE_SEPERATOR+"Credentials")))
	                        .setAccessType("offline").build();
	 
	        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	    }
	 
	    public static void main(String... args) throws IOException, GeneralSecurityException {
	    	System.out.println("Program started  @ " + System.currentTimeMillis());
	    	GDrive driveInterface = new GDrive();
	    	ArrayList<DriveFile> files=driveInterface.listAllFiles();
	    	
	    	driveInterface.initialiseFileAttributes(files);
	    	ArrayList<DriveFile> dontHaveParents = new ArrayList<>();
	    	for(DriveFile file: files){
	    		if(file.getParents()==null||file.getParents().isEmpty()) {
	    			dontHaveParents.add(file);
	    		}
	    		
	    	}
//	    	for(DriveFile file:dontHaveParents) {
//	    		driveInterface.bottomUpFileCreation(file);
	    		driveInterface.printFileStructure();

//	    	}
	    	
	    	
	    	
//	    	driveInterface.setFileChildren(files);
//	    	driveInterface.setFileParents(files);
	    	
//	    	ArrayList<DriveFile> dfiles= driveInterface.listAllFiles();

//	    	driveInterface.printFileStructure();
	    	
//	    	
//	    	for(DriveFile DFILE : dfiles){
//	    		System.out.println(DFILE.getFileName()+"  "+DFILE.getID());
//	    	}
//	    	
//	    	ArrayList<DriveFolder> dfolders= driveInterface.listAllFolders();
//	    	
//	    	for(DriveFolder DFILE : dfolders){
//	    		System.out.println(DFILE.getFolderName()+"  "+DFILE.getID());
//	    	}


	    }
	    public void bottomUpFileCreation(DriveFile file) {
	    	new java.io.File(DOWNLOAD_PATH).mkdirs();
    		java.io.File baseFile = new java.io.File(file.getFilePath());
 
    		try {

    			downloadFile(file);
//				baseFile.createNewFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(file.getChildren()!=null&&!file.getChildren().isEmpty()) {
	    		for(DriveFile cFile:file.getChildren()) {
	    			bottomUpFileCreation(cFile);
	    		}
    		}
	    }
		public static String getExtension(java.io.File file) {
			if(!file.getAbsolutePath().isEmpty()) {
				return "";
			}
			String extension = file.getName().lastIndexOf('.')>0 ? file.getName().toLowerCase().substring(file.getName().lastIndexOf('.')) : "";

			System.out.println("EXTENSION: "+extension);
			return extension;
		}
	
	GDrive() throws GeneralSecurityException, IOException{
		LOCAL_DIR=System.getProperty("user.dir");
		FILE_SEPERATOR=System.getProperty("file.separator");
		DOWNLOAD_PATH= LOCAL_DIR+FILE_SEPERATOR+"DRIVE_DOWNLOADS";

		
        // 1: Create CREDENTIALS_FOLDER

		 
        // 2: Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
 
        // 3: Read client_secret.json file & create Credential object.
        Credential credential = getCredentials(HTTP_TRANSPORT);
 
        // 5: Create Google Drive Service.
        gDriveObject = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                .setApplicationName(APPLICATION_NAME).build();
		
        
	}
	
	public String toString() {
		return gDriveObject.toString();
	}
	public String getApplicationName() {
		return gDriveObject.getApplicationName();
	}
	public Drive getDriveObject() {
		return gDriveObject;
	}
	
	public ArrayList<DriveFile> listAllFiles() throws IOException{

		
		Files.List request = gDriveObject.files().list().setPageSize(1000).setFields("nextPageToken, files(id, name, mimeType, parents)");
		List<File> result= new ArrayList<File>();
		ArrayList<DriveFile> dFiles= new ArrayList<>();
		do
		 {
			FileList files = request.execute();
			result.addAll(files.getFiles());
			request.setPageToken(files.getNextPageToken());
		}while(request.getPageToken()!=null&&request.getPageToken().length()>0);
		
		for (File file : result) {
			try {
				dFiles.add(new DriveFile(file));
				
			}catch(Exception p) {
				System.err.println(p);
			}	
		}
		
		System.out.println("All files retrieved @ " + System.currentTimeMillis());
		return dFiles;
		
	}
	public ArrayList<DriveFile> listAllGoogleFiles() throws IOException{
		ArrayList<DriveFile> allFiles=listAllFiles();
		ArrayList<DriveFile> googleFiles=new ArrayList<>();
		
		for(DriveFile file:allFiles) {
			if(file.getFileType().contains("application/vnd.google-apps")) {
				
				googleFiles.add(file);
				
			}			
			
		}
		
		return googleFiles;
		
	}
	public ArrayList<DriveFolder> listAllFolders() throws IOException{
		
		Files.List request = gDriveObject.files().list().setPageSize(1000).setFields("nextPageToken, files(id, name, mimeType, parents)");
		List<File> result= new ArrayList<File>();
		ArrayList<DriveFolder> dFiles= new ArrayList<>();
		do
		 {
			FileList files = request.execute();
			result.addAll(files.getFiles());
			request.setPageToken(files.getNextPageToken());
		}while(request.getPageToken()!=null&&request.getPageToken().length()>0);
		
		for (File file : result) {
			if(file.getMimeType().contains("application/vnd.google-apps.folder")) {
				try {
					dFiles.add(new DriveFolder(file));
					
				}catch(Exception p) {
					System.err.println(p);
				}
			}
		}
		
		return dFiles;
	}
//	public ArrayList<DriveFile> listAllGoogleFiles() throws IOException{
//		ArrayList<DriveFile> allFiles=listAllFiles();
//		ArrayList<DriveFile> googleFiles=new ArrayList<>();
//		
//		for(DriveFile file:allFiles) {
//			if(file.getFileType().contains("application/vnd.google-apps")) {
//				
//				googleFiles.add(file);
//				
//			}			
//			
//		}
//		
//		return googleFiles;
//		
//	}
	public ArrayList<DriveFile> listFiles(DriveFolder folder){
		return null;
		
	}
	
	public void getChildrenfromFile(DriveFile file, int recursionStep) {
		ArrayList<DriveFile> fileChildren=file.getChildren();
		if(!fileChildren.isEmpty()) {
			for(DriveFile dFile: fileChildren) {
				System.out.println(repeatedKeys('-',recursionStep)+dFile.getFileName());
				if(!dFile.getChildren().isEmpty()) {
					getChildrenfromFile(dFile,recursionStep+1);
				}
			}
		}
	}
	
	public void printFileStructure() throws IOException {
		ArrayList<DriveFile> files=listAllFiles();
		setFileChildren(files);
		
		for(DriveFile file: files) {
			if((!file.getChildren().isEmpty())&&(file.getParents().isEmpty())) {
				getChildrenfromFile(file,0);
				
			}
		}
		
	}
	
	public void initialiseFileAttributes() throws IOException {
		ArrayList<DriveFile> files = listAllFiles();
//		ExecutorService workerPool=Executors.newFixedThreadPool(12);
//
//		workerPool.submit(new Runnable() {
//			public void run() {
				try {
					setFileChildren(files);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				}
//			
//		} );	
//		workerPool.submit(new Runnable() {
//			public void run() {
				try {
					setFileParents(files);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				}
//			
//		} );	
//		
//		workerPool.shutdown();
//		while(!workerPool.isTerminated()) {
//			try {
//				TimeUnit.SECONDS.sleep(5);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		setFilePath(files);
	}
	public void initialiseFileAttributes(ArrayList<DriveFile> files) throws IOException {

		ExecutorService workerPool=Executors.newFixedThreadPool(12);

		workerPool.submit(new Runnable() {
			public void run() {
				try {
					setFileChildren(files);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			
		} );	
		workerPool.submit(new Runnable() {
			public void run() {
				try {
					setFileParents(files);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			
		} );	
		
		workerPool.shutdown();
		while(!workerPool.isTerminated()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All files initialised @ " + System.currentTimeMillis());
		setFilePath(files);
	}
	//TODO what the hell is this self referencial line on 387 and 396
	public void setFilePath(ArrayList<DriveFile> files){
		for(DriveFile file: files) {
			if(file.getParents()==null||file.getParents().isEmpty()) {
				filePathGenerator(DOWNLOAD_PATH,file);
			}
		}
		

	}
	
	public void filePathGenerator(String previousPath,DriveFile file) {
		if (file.getFileName().contains("/")) {
			previousPath+=(FILE_SEPERATOR+file.getFileName().replace('/', ' ').trim());
		}else {
			previousPath+=(FILE_SEPERATOR+file.getFileName());
		}
		
		file.setFilePath(previousPath);

		if(file.getChildren()!=null&&!file.getChildren().isEmpty()) {
			for(DriveFile files:file.getChildren()) {
				filePathGenerator(previousPath,files);
			}
		}
	}
	
	public void setFileChildren() throws IOException {
		ArrayList<DriveFile> files=listAllFiles();

		for(DriveFile file: files) {
			ArrayList<DriveFile> children= new ArrayList<>();

			for(DriveFile Sfile:files) {
				if(Sfile.getFileObject().getParents()!=null && Sfile.getFileObject().getParents().contains(file.getID())) {
					System.out.println(Sfile.getFileName()+" is a child of "+ file.getFileName());
					children.add(Sfile);
				}
			}

			file.setChildren(children);
		}
	}
	
	public void setFileChildren(ArrayList<DriveFile> files) throws IOException {
		ExecutorService workerPool=Executors.newFixedThreadPool(5);
		for(DriveFile file: files) {
			workerPool.submit(new Runnable() {
				public void run() {
					ArrayList<DriveFile> children= new ArrayList<>();

					for(DriveFile Sfile:files) {
						if(Sfile.getFileObject().getParents()!=null && !Sfile.getFileObject().getParents().isEmpty()) {
							int childIndex=Sfile.getFileObject().getParents().indexOf(file.getID());
							if(childIndex!=-1) {
								children.add(Sfile);
							}
							
						}
					}

					file.setChildren(children);
					
					
					}
				
			} );
		}
		
		workerPool.shutdown();
		while(!workerPool.isTerminated()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setFileParents() throws IOException {
		ArrayList<DriveFile> files=listAllFiles();
		for(DriveFile file:files) {
			ArrayList<DriveFile> parents = new ArrayList<>();
			
			
			if(file.getFileObject().getParents()!=null&&(!file.getFileObject().getParents().isEmpty())) {
				String parentIDs=file.getFileObject().getParents().get(0);
				for(DriveFile Dfile:files) {
					if(Dfile.getID().equals(parentIDs)) {
						parents.add(Dfile);
					}
				}

			}
			file.setParents(parents);
		}
	}
	
	public void setFileParents(ArrayList<DriveFile> files) throws IOException {
		ExecutorService workerPool=Executors.newFixedThreadPool(5);

		for(DriveFile file:files) {
			workerPool.submit(new Runnable() {
				public void run() {
				ArrayList<DriveFile> parents = new ArrayList<>();
				
				
				if(file.getFileObject().getParents()!=null&&(!file.getFileObject().getParents().isEmpty())) {
					String parentIDs=file.getFileObject().getParents().get(0);
					for(DriveFile Dfile:files) {
						if(Dfile.getID().equals(parentIDs)) {
							parents.add(Dfile);
						}
					}
	
				}
				file.setParents(parents);
				}
				
			} );
		}
	
		workerPool.shutdown();
		while(!workerPool.isTerminated()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean downloadFile(DriveFile fileID) throws IOException {
		String fileMimeType=fileID.getFileType();
		if		 (fileMimeType.contains("vnd.google-apps.folder")) {
			  if(!new java.io.File(fileID.getFilePath()).exists()) {
				new java.io.File(fileID.getFilePath()).mkdirs();
			}
				System.out.println(fileID.getFilePath()+ " has been written.");
				return true;
		}
		OutputStream outputStream =null;
		try {
			
			if (fileMimeType.contains("vnd.google-apps.file")) {
				outputStream = new FileOutputStream(fileID.getFilePath()+".pdf");
				gDriveObject.files().export(fileID.getID(), "application/pdf").executeMediaAndDownloadTo(outputStream);
			}else if (fileMimeType.contains("vnd.google-apps.document")) {
				
				outputStream = new FileOutputStream(fileID.getFilePath()+".pdf");
				gDriveObject.files().export(fileID.getID(), "application/pdf").executeMediaAndDownloadTo(outputStream);
				outputStream = new FileOutputStream(fileID.getFilePath()+".docx");
				gDriveObject.files().export(fileID.getID(), "application/vnd.openxmlformats-officedocument.wordprocessingml.document").executeMediaAndDownloadTo(outputStream);

			}else if (fileMimeType.contains("vnd.google-apps.presentation")) {
				outputStream = new FileOutputStream(fileID.getFilePath()+".pdf");
				gDriveObject.files().export(fileID.getID(), "application/pdf").executeMediaAndDownloadTo(outputStream);
				outputStream = new FileOutputStream(fileID.getFilePath()+".pptx");
				gDriveObject.files().export(fileID.getID(), "application/vnd.openxmlformats-officedocument.presentationml.presentation").executeMediaAndDownloadTo(outputStream);

			}else if (fileMimeType.contains("vnd.google-apps.spreadsheet")) {
				outputStream = new FileOutputStream(fileID.getFilePath()+".pdf");
				gDriveObject.files().export(fileID.getID(), "application/pdf").executeMediaAndDownloadTo(outputStream);
				outputStream = new FileOutputStream(fileID.getFilePath()+".xlsx");
				gDriveObject.files().export(fileID.getID(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").executeMediaAndDownloadTo(outputStream);

			}else {
				outputStream = new FileOutputStream(fileID.getFilePath());
				gDriveObject.files().get(fileID.getID()).executeMediaAndDownloadTo(outputStream);
			}
		}catch (Exception Error) {
			FileWriter errorLogFile = new FileWriter("MissedFiles.txt", true);
			System.err.println();
			System.out.println("Failed to download file\n"+fileID.getFileName()+"\n§§§§§§§§§§§");
			errorLogFile.append(fileID.getID() +" : "+fileID.getFilePath()+"\n");
			errorLogFile.close();
			outputStream.close();
			return false;
		}
		outputStream.close();
		System.out.println(fileID.getFilePath()+ " has been written.");
		return true;
//		
//		String fileId = "1ZdR3L3qP4Bkq8noWLJHSr_iBau0DNT4Kli4SxNc2YEo";
//		OutputStream outputStream = new ByteArrayOutputStream();
//		driveService.files().export(fileId, "application/pdf")
//		    .executeMediaAndDownloadTo(outputStream);
	}
	

	
	

		
	
	public static String repeatedKeys(char key, int repeatAmount) {
		
		String string="";
		for(int i=0;i<repeatAmount;i++) {		
			string+=key;
		}
		return string;
	}
		

//	public download() {}
//	public download() {}
//	public upload() {}
//	public upload() {}
//	public sync() {}
//	public sync() {}
//	
//	public convert() {}
//	
	

}
