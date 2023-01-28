package com.s3.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.common.collect.Lists;
import com.s3.exception.S3RuntimeException;
import com.s3.storageobjects.StorageObject;
import com.s3.storageobjects.StorageS3Document;
import com.s3.storageobjects.StorageS3Folder;
import com.s3.util.AwsS3Util;

public abstract class S3Bucket
{
	public static String s3BucketName;
	public static AmazonS3 s3Client;
	public static TransferManager s3TransferManager;
	
	S3DataObject s3DataObject;
	Logger logger = LoggerFactory.getLogger(S3Bucket.class);
	
	public void deleteObject(final String bucketName, final String s3ObjectKey, final Integer... isDocument) throws Exception
	{
		final long start = System.currentTimeMillis();
		logger.info("(S3) Object Deletion ({}) started", s3ObjectKey);
		final Integer isS3Document = isDocument.length > 0 ? isDocument[0] : 0;
		String objectKey = (isS3Document == 1) ? s3ObjectKey : AwsS3Util.appendPrefix(s3ObjectKey);
		objectKey = AwsS3Util.getProperPath(objectKey);

		if (objectKey.endsWith(AwsS3Util.FOLDER_SUFFIX))
		{
			// Delete all documents within folder
			final ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
			listObjectsRequest.withBucketName(bucketName).withPrefix(objectKey).setStartAfter(objectKey);

			ListObjectsV2Result objects;
			do
			{
				final ArrayList<KeyVersion> keys = new ArrayList<KeyVersion>();
				objects = s3Client.listObjectsV2(listObjectsRequest);

				final List<S3ObjectSummary> docObjs = objects.getObjectSummaries();

				for (final S3ObjectSummary os2 : docObjs)
				{
					if (!os2.getKey().endsWith(AwsS3Util.FOLDER_SUFFIX))
					{
						keys.add(new KeyVersion(os2.getKey()));
					}
				}

				if (!keys.isEmpty())
				{
					// Delete the specified versions of the sample objects.
					final DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName).withKeys(
						keys).withQuiet(false);

					s3Client.deleteObjects(multiObjectDeleteRequest);
				}
				listObjectsRequest.setContinuationToken(objects.getNextContinuationToken());
			}
			while (objects.isTruncated());

			// Finally delete all folders
			final ListObjectsV2Request listFoldersRequest = new ListObjectsV2Request();
			listFoldersRequest.withBucketName(bucketName).withPrefix(objectKey);

			final ArrayList<KeyVersion> keys = new ArrayList<KeyVersion>();
			final ListObjectsV2Result folders = s3Client.listObjectsV2(listFoldersRequest);

			final List<S3ObjectSummary> folderObjs = folders.getObjectSummaries();

			for (final S3ObjectSummary folder : folderObjs)
			{
				keys.add(new KeyVersion(folder.getKey()));
			}
			if (!keys.isEmpty())
			{
				// Delete the specified versions of the sample objects.
				final DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName).withKeys(
					keys).withQuiet(false);

				s3Client.deleteObjects(multiObjectDeleteRequest);
			}

		}
		s3Client.deleteObject(bucketName, objectKey);

		final long end = System.currentTimeMillis();
		logger.info("(S3) Object deletion completed in {} ms ({} s)", (end - start), (double) (end - start) / 1000);
	}
	
	public S3DataObject getAWSS3ObjectDetailsFromSummary(final String keyName, final GregorianCalendar gDate, final long... documentSize)
	{
		final String objectKey = keyName;
		final long s3isS3Document = documentSize.length > 0 ? documentSize[0] : 0;

		S3DataObject s3DataObject = null;
		try
		{
			final String docType = s3isS3Document > 0 ? "folder" : "document";

			final String[] arrObjectPath = objectKey.split("\\/");
			final String objectUrl = ((AmazonS3Client) s3Client).getResourceUrl(s3BucketName, objectKey);

			String fileName = "";
			if (arrObjectPath.length != 0)
			{
				fileName = arrObjectPath[arrObjectPath.length - 1];
			}
			final String fileType = fileName.toLowerCase();

			final String PathID = (objectKey.substring(0, 5).equals("root/") && objectKey.length() > 5) ? objectKey
				.replaceAll("root/", "/") : objectKey;

			// path and ids both are same
			s3DataObject = new S3DataObject(fileName, PathID, PathID, docType, objectUrl, fileType, gDate, Arrays.asList(PathID));
		}
		catch (final Exception e)
		{
			logger.error("The specified key (" + objectKey + ") does not exist", e.getMessage());
			throw new S3RuntimeException(e);
		}

		return s3DataObject;
	}
	
	public String getParentPath(final String objectPath)
	{
		String parentPath = "";
		final String[] array = objectPath.split("\\/");

		for (int i = 0; i <= array.length - 1; i++)
		{
			// System.out.println(array[i]);
			final String childObjectName = array[array.length - 1];

			if (!childObjectName.equals(array[i]))
			{
				parentPath = parentPath + array[i] + "/";
			}
		}

		return parentPath;
	}
	
	public InputStream getS3InputStream(final String objectKey)
	{
		S3Object s3Object = null;
		InputStream objectData = null;
		try
		{
			s3Object = s3Client.getObject(s3BucketName, AwsS3Util.getProperPath(objectKey));
			objectData = s3Object.getObjectContent();

			// Store stream to memory and close the S3 stream connection
			final byte[] byteArray = IOUtils.toByteArray(objectData);

			return new ByteArrayInputStream(byteArray);
		}
		catch (final Exception e)
		{
			logger.error("The specified key (" + objectKey + ") does not exist", e.getMessage());
			throw new S3RuntimeException(e);
		}
		finally
		{
			try
			{
				objectData.close();
				s3Object.close();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public S3DataObject getS3ObjectDetails(final String keyName, final Integer... isDocument)
	{

		String objectKey = keyName;
		final Integer isS3Document = isDocument.length > 0 ? isDocument[0] : 0;
		objectKey = AwsS3Util.getS3ObjectKey(objectKey);
		objectKey = (isS3Document == 1) ? objectKey : AwsS3Util.appendPrefix(objectKey);

		ObjectMetadata s3Object = null;
		S3DataObject s3DataObject = null;
		try
		{
			s3Object = s3Client.getObjectMetadata(s3BucketName, objectKey);
			s3DataObject = new S3DataObject();
			final GregorianCalendar gc = new GregorianCalendar();
			final String docType = (isS3Document == 1) ? "document" : "folder";

			final String[] arrObjectPath = objectKey.split("\\/");
			final String objectUrl = ((AmazonS3Client) s3Client).getResourceUrl(s3BucketName, objectKey);

			if (arrObjectPath.length != 0)
			{
				s3DataObject.setName(arrObjectPath[arrObjectPath.length - 1]);
			}

			final String PathID = (objectKey.substring(0, 5).equals("root/") && objectKey.length() > 5) ? objectKey
				.replaceAll("root/", "/") : objectKey;

			s3DataObject.setId(PathID);
			s3DataObject.setPath(PathID);
			s3DataObject.setPaths(Arrays.asList(PathID));
			s3DataObject.setType(docType);
			gc.setTime(s3Object.getLastModified());
			s3DataObject.setLastModificationDate(gc);

			// Setting content properties
			s3DataObject.setContentUrl(objectUrl);
			s3DataObject.setContentMimeType(s3Object.getContentType());
		}
		catch (final Exception e)
		{
			logger.error("The specified key (" + objectKey + ") does not exist", e.getMessage());
			throw new S3RuntimeException(e);
		}

		return s3DataObject;
	}
	
	public Collection<StorageObject> listObjectsInPrefix(final String prefix)
	{
		final ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
		final String fullPrefix = AwsS3Util.getProperPath(AwsS3Util.appendPrefix(prefix));

		if (fullPrefix.equals(AwsS3Util.FOLDER_SUFFIX))
		{
			listObjectsRequest.withBucketName(s3BucketName).withDelimiter(AwsS3Util.FOLDER_SUFFIX).setStartAfter(
					AwsS3Util.FOLDER_SUFFIX);
		}
		else
		{
			listObjectsRequest.withBucketName(s3BucketName).withPrefix(fullPrefix).withDelimiter(AwsS3Util.FOLDER_SUFFIX)
				.setStartAfter(AwsS3Util.FOLDER_SUFFIX);
		}

		ListObjectsV2Result objects;
		final Collection<StorageObject> documents = Lists.newArrayList();
		boolean getFolders = true;

		final long start = System.currentTimeMillis();

		do
		{
			objects = s3Client.listObjectsV2(listObjectsRequest);

			if (getFolders)
			{

				for (final String os : objects.getCommonPrefixes())
				{
					documents.add(new StorageS3Folder(getAWSS3ObjectDetailsFromSummary(os, null)));
				}
				getFolders = false;
			}

			final List<S3ObjectSummary> objectsSummaries = objects.getObjectSummaries();
			for (final S3ObjectSummary os2 : objectsSummaries)
			{
				if (!os2.getKey().equals(fullPrefix))
				{
					final GregorianCalendar gc = new GregorianCalendar();
					gc.setTime(os2.getLastModified());

					documents.add(new StorageS3Document(getAWSS3ObjectDetailsFromSummary(os2.getKey(), gc, os2
						.getSize())));
				}
			}
			listObjectsRequest.setContinuationToken(objects.getNextContinuationToken());
		}
		while (objects.isTruncated());

		if (logger.isDebugEnabled())
		{
			final long end = System.currentTimeMillis();

			final String log = String.format("(S3) Folder's (%s) children retrieved in %s ms (%s s)", fullPrefix,
				(end - start), (double) (end - start) / 1000);
			logger.debug(log);
		}

		return documents;
	}

	
}
