package com.s3.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.s3.storageobjects.StorageFolderObject;

@Service
public class ContentService
{

	@Autowired
	DocumentService documentService;

	public StorageFolderObject getRootFolder()
	{
		return documentService.getRootFolder();
	}

	public void addFileToS3(String key, MultipartFile[] documents)
	{
		for (MultipartFile multipartFile : documents)
		{
			File convFile = new File(multipartFile.getOriginalFilename());
			FileOutputStream fileOutputStream;
			
			try
			{
				fileOutputStream = new FileOutputStream(convFile);
				fileOutputStream.write(multipartFile.getBytes());
				fileOutputStream.close();
				
				// Setting document info and uploading doc into s3
				Map<String, Object> documentInfo = new HashMap<>();
				documentInfo.put(DocumentService.PARENT_FOLDER_ID, key);
				documentInfo.put(DocumentService.DOCUMENT_NAME, multipartFile.getOriginalFilename());
				documentInfo.put(DocumentService.DOCUMENT_INPUTSTREAM, new FileInputStream(convFile));
				
				// Always deleting the existing file and create new one
				documentService.deleteDocument(documentInfo);
				
				// Creating a new document
				documentService.createDocument(documentInfo);
				
			} catch (Exception e)
			{
				// TODO: handle exception
			}
			
		}
	}

	public StorageFolderObject checkAndCreateFolder(String folderName)
	{
		if (folderName.isEmpty())
		{
			return documentService.getRootFolder();
		}
		
		StorageFolderObject parentFolder = documentService.searFolderByPath(folderName);
		Map<String, Object> folderInfo = new HashMap<>();
		
		if (parentFolder == null)
			folderInfo.put(DocumentService.PARENT_FOLDER_ID, documentService.searFolderByPath(folderName).getId());
		
		folderInfo.put(DocumentService.FOLDER_NAME, folderName);
		
		parentFolder = documentService.cretFolder(folderInfo);

		return parentFolder;		
	}

}
