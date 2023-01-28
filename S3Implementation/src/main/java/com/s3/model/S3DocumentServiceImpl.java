package com.s3.model;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.s3.exception.S3RuntimeException;
import com.s3.service.DocumentService;
import com.s3.storageobjects.StorageDocumentObject;
import com.s3.storageobjects.StorageFolderObject;
import com.s3.storageobjects.StorageObject;
import com.s3.storageobjects.StorageS3Document;
import com.s3.storageobjects.StorageS3Folder;
import com.s3.util.AwsS3Util;

public class S3DocumentServiceImpl extends S3Bucket implements DocumentService
{

	Logger logger = LoggerFactory.getLogger(S3DocumentServiceImpl.class);

	public S3DocumentServiceImpl(AmazonS3 amazonS3, TransferManager transferManager, String bucketName)
	{
		s3Client = amazonS3;
		s3TransferManager = transferManager;
		s3BucketName = bucketName;
	}

	@Override
	public void createDocument(Map<String, Object> documentInfo)
	{
		final String fileName = (String) documentInfo.get(DOCUMENT_NAME);
		final InputStream inputStream = (InputStream) documentInfo.get(DOCUMENT_INPUTSTREAM);
		final String parentId = (String) documentInfo.get(PARENT_FOLDER_ID);

		String folderPath = AwsS3Util.appendPrefix(parentId) + fileName;
		folderPath = AwsS3Util.getProperPath(folderPath);

		// creating document in storage s3 folder object
		StorageFolderObject object = new StorageS3Folder(s3DataObject);
		object.createDocument(documentInfo, inputStream);
	}

	@Override
	public StorageFolderObject cretFolder(Map<String, Object> folderInfo)
	{
		final String folderName = (String) folderInfo.get(DocumentService.FOLDER_NAME);
		final String parentPath = (String) folderInfo.get(DocumentService.PARENT_FOLDER_ID);

		String folderFullPath = AwsS3Util.appendPrefix(parentPath) + AwsS3Util.appendPrefix(folderName);
		folderFullPath = AwsS3Util.getProperPath(folderFullPath);

		S3DataObject s3DataObject = getS3ObjectDetails(folderFullPath);

		return new StorageS3Folder(s3DataObject).creatFolder(folderInfo);
	}

	@Override
	public void deleteDocument(Map<String, Object> documentInfo)
	{

		final String documentIdPath = (String) documentInfo.get(PARENT_FOLDER_ID);
		final String documentName = (String) documentInfo.get(DOCUMENT_NAME);

		String folderFullPath = AwsS3Util.appendPrefix(documentIdPath) + documentName;
		folderFullPath = AwsS3Util.getProperPath(folderFullPath);

		try
		{
			deleteObject(s3BucketName, folderFullPath, 1);
		} catch (Exception e)
		{
			throw new S3RuntimeException(e);
		}

	}

	@Override
	public void deleteFolder(String folderName)
	{
		try
		{
			deleteObject(s3BucketName, folderName);
		} catch (Exception e)
		{
			throw new S3RuntimeException(e);
		}
	}

	@Override
	public StorageDocumentObject getDocument(String documentId)
	{
		
		S3DataObject s3DataObject = getS3ObjectDetails(documentId, 1);
		
		if(s3DataObject != null)
			return new StorageS3Document(s3DataObject);
		
		return null;
	}

	@Override
	public StorageFolderObject getFolder(String folderId)
	{
		S3DataObject s3DataObject = getS3ObjectDetails(folderId);
		
		if(s3DataObject != null)
			return new StorageS3Folder(s3DataObject); 
			
		return null;
	}

	@Override
	public StorageFolderObject getRootFolder()
	{
		return new StorageS3Folder(getS3ObjectDetails(AwsS3Util.ROOT_FOLDER));
	}

	@Override
	public StorageObject getObject(String objectId)
	{
		S3DataObject object = getS3ObjectDetails(objectId,1);
		
		if (object != null)
		{
			return AwsS3Util.convertTStorageObject(object);
		}
		
		return null;
	}

	@Override
	public StorageObject getObjectByPath(String path)
	{
		S3DataObject object = path.endsWith(AwsS3Util.FOLDER_SUFFIX) ? getS3ObjectDetails(path) : getS3ObjectDetails(path, 1);
		
		if (object != null)
		{
			return AwsS3Util.convertTStorageObject(object);
		}
		
		return null;
	}

	@Override
	public StorageFolderObject searFolderByPath(String folderPath)
	{
		String folderFullPath = AwsS3Util.appendPrefix(AwsS3Util.ROOT_FOLDER) + AwsS3Util.appendPrefix(folderPath);
		folderFullPath = folderFullPath.replaceAll("\\//", "/");
		
		final boolean isFolderExists =  s3Client.doesObjectExist(s3BucketName, folderFullPath);
		if (isFolderExists)
		{
			return new StorageS3Folder(getS3ObjectDetails(folderFullPath));
		}
		
		return null;
	}

}
