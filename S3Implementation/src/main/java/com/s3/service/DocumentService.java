package com.s3.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.s3.storageobjects.StorageDocumentObject;
import com.s3.storageobjects.StorageFolderObject;
import com.s3.storageobjects.StorageObject;

@Service
public interface DocumentService
{
	
	final static String PARENT_FOLDER_ID = "parent_folder_id";
	final static String DOCUMENT_NAME = "document_name";
	final static String DOCUMENT_ID = "document_id";
	final static String FOLDER_NAME = "folder_name";
	final static String FOLDER_ID = "folder_id";
	final static String DOCUMENT_CONTENT_TYPE = "document_content_type";
	final static String DOCUMENT_INPUTSTREAM = "document_inputstream";
	final static String DOCUMENT_SIZE = "document_size";
	final static String CREATED_BY = "created_by";
	
	void createDocument(Map<String, Object> documentInfo);
	
	StorageFolderObject cretFolder(Map<String, Object> folderInfo);
	
	void deleteDocument(Map<String, Object> documentInfo);
	
	void deleteFolder(String folderName);
	
	StorageDocumentObject getDocument(String documentId);
	
	StorageFolderObject getFolder(String folderId);
	
	StorageFolderObject getRootFolder();
	
	StorageObject getObject(String objectId);
	
	StorageObject getObjectByPath(String path);
	
	StorageFolderObject searFolderByPath(String folderPath);
	
}
