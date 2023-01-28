package com.s3.storageobjects;

import java.io.InputStream;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import com.s3.exception.S3RuntimeException;
import com.s3.model.S3Bucket;
import com.s3.model.S3DataObject;

public class StorageS3Document extends S3Bucket implements StorageDocumentObject
{

	
	private final S3DataObject s3DataObject;
	
	public StorageS3Document(S3DataObject s3DataObject)
	{
		this.s3DataObject = s3DataObject;
	}

	@Override
	public void delete()
	{
		try
		{
			deleteObject(s3BucketName, s3DataObject.getId(), 1);
			
		} catch (Exception e)
		{
			throw new S3RuntimeException(e);
		}
	}

	@Override
	public String getName()
	{
		return s3DataObject.getName();
	}

	@Override
	public GregorianCalendar getLastModificationDate()
	{
		return s3DataObject.getLastModificationDate();
	}

	@Override
	public List<StorageFolderObject> getParent()
	{
		return Arrays.asList(new StorageS3Folder(getS3ObjectDetails(getParentPath(getId()))));
	}

	@Override
	public List<String> getPaths()
	{
		return s3DataObject.getPaths();
	}

	@Override
	public String getId()
	{
		return s3DataObject.getId();
	}

	@Override
	public void deleteDocument()
	{
		delete();
	}

	@Override
	public InputStream getContentStream()
	{
		return getS3InputStream(s3DataObject.getPath());
	}

	@Override
	public String getFileName()
	{
		return s3DataObject.getName();
	}

	@Override
	public String getContentMimeType()
	{
		return s3DataObject.getContentMimeType();
	}

	@Override
	public String getContnetUrl()
	{
		return s3DataObject.getContentUrl();
	}

	@Override
	public StorageDocumentObject rename(String fileName)
	{
		throw new NotImplementedException("Not Implemented for aws S3");
	}

}
