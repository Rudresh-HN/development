package com.s3.storageobjects;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Upload;
import com.s3.exception.S3RuntimeException;
import com.s3.model.S3Bucket;
import com.s3.model.S3DataObject;
import com.s3.service.DocumentService;
import com.s3.util.AwsS3Util;

public class StorageS3Folder extends S3Bucket implements StorageFolderObject
{

	Logger logger = LoggerFactory.getLogger(StorageS3Folder.class);

	public final S3DataObject s3DataObject;

	public StorageS3Folder(S3DataObject s3DataObject1)
	{
		this.s3DataObject = s3DataObject1;
	}

	@Override
	public StorageDocumentObject createDocument(Map<String, Object> properties, InputStream inputStream)
	{
		final String fileName = (String) properties.get(DocumentService.DOCUMENT_NAME);

		String folderFullPath = "";

		if (getId() != null && getId().length() > 0)
		{
			folderFullPath = AwsS3Util.appendPrefix(AwsS3Util.getProperPath(getId())) + fileName;
		} else
		{
			folderFullPath = AwsS3Util.appendPrefix(AwsS3Util.ROOT_FOLDER) + fileName;
		}

		try
		{

			final File convertedFile = new File(fileName);
			FileUtils.copyInputStreamToFile(inputStream, convertedFile);

			final long start = System.currentTimeMillis();
			logger.info("(S3) Object upload ({}) started", fileName);

			final PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, folderFullPath, convertedFile);

			final Upload upload = s3TransferManager.upload(putObjectRequest);

			upload.waitForCompletion();

			final long end = System.currentTimeMillis();

			final String log = String.format(
					"(S3) %s upload completed in %s ms (%ss)", fileName, (end - start), (double) (end - start) / 1000
			);

			logger.info(log);

		} catch (Exception e)
		{
			throw new S3RuntimeException(e);
		} finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (IOException e2)
				{

				}
			}
		}

		return new StorageS3Document(getS3ObjectDetails(folderFullPath, 1));
	}

	@Override
	public StorageFolderObject creatFolder(Map<String, Object> properties)
	{

		final String folderName = (String) properties.get(DocumentService.DOCUMENT_NAME);

		String folderFullPath = "";

		if (getId() != null && getId().length() > 0)
		{
			folderFullPath = AwsS3Util.appendPrefix(AwsS3Util.getProperPath(getId()))
					+ AwsS3Util.appendPrefix(folderName);
		} else
		{
			folderFullPath = AwsS3Util.appendPrefix(AwsS3Util.ROOT_FOLDER) + AwsS3Util.appendPrefix(folderName);
			;
		}

		folderFullPath = folderFullPath.replaceAll("\\//", "/");

		final ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0L);
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		try
		{
			final boolean isFolderExists = s3Client.doesObjectExist(folderName, folderFullPath);

			if (!isFolderExists)
			{
				final PutObjectRequest putObjectRequest = new PutObjectRequest(
						s3BucketName, folderFullPath, emptyContent, metadata
				);

				s3Client.putObject(putObjectRequest);
				logger.info("Folder {} is created.", folderName);

				return new StorageS3Folder(getS3ObjectDetails(folderFullPath));
			}

		} catch (Exception e)
		{
			throw new S3RuntimeException(e);
		}

		logger.info("Folder {} already exists.", folderName);

		return new StorageS3Folder(getS3ObjectDetails(folderFullPath));

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
		return Arrays.asList(new StorageS3Folder(getS3ObjectDetails(getParentId())));
	}

	@Override
	public List<String> getPaths()
	{
		return s3DataObject.getPaths();
	}

	@Override
	public Iterable<StorageObject> getChildern()
	{
		Iterable<StorageObject> objects = listObjectsInPrefix(getId());
		return objects;
	}

	@Override
	public Integer getChildernCount()
	{

		ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request();
		listObjectsV2Request.withBucketName(s3BucketName).withPrefix(AwsS3Util.getProperPath(getPath()))
				.withDelimiter(AwsS3Util.FOLDER_SUFFIX).setStartAfter(AwsS3Util.getProperPath(getPath()));

		ListObjectsV2Result objectsV2Result;
		int count = 0;
		
		do
		{
			objectsV2Result =  s3Client.listObjectsV2(listObjectsV2Request);
			count += objectsV2Result.getObjectSummaries().size();
			listObjectsV2Request.setContinuationToken(objectsV2Result.getNextContinuationToken());
			
		} while (objectsV2Result.isTruncated());
		
		// Add folder count
		count += objectsV2Result.getObjectSummaries().size();
		
		return count;
	}

	@Override
	public String getParentId()
	{
		return getParentPath(AwsS3Util.getProperPath(getId()));
	}

	@Override
	public String getPath()
	{
		return s3DataObject.getPath();
	}

	@Override
	public String getId()
	{
		return s3DataObject.getId();
	}

}
