package com.s3.util;

import com.s3.model.S3DataObject;
import com.s3.storageobjects.StorageObject;
import com.s3.storageobjects.StorageS3Document;
import com.s3.storageobjects.StorageS3Folder;

public class AwsS3Util
{
	public static final String FOLDER_SUFFIX = "/";
	public static final String ROOT_FOLDER = "root";

	public static String appendPrefix(String key)
	{

		if (!key.isEmpty() && !key.endsWith(FOLDER_SUFFIX))
		{
			key += FOLDER_SUFFIX;
		}
		return key;
	}

	public static String getProperPath(String fullParentFolderPath)
	{

		String fullPath = fullParentFolderPath.replace("\\", "/");

		if (!fullPath.contains("root/"))
		{
			fullPath = ROOT_FOLDER + FOLDER_SUFFIX + fullPath;
		}
		fullPath = fullPath.replace("\\//", "/");

		return fullPath;
	}

	public static String getS3ObjectKey(String objectKey)
	{
		objectKey = objectKey.replace("\\", "/");

		final String appendRoot = "root/";

		if (objectKey.contains("root") && !objectKey.contains("root/"))
			objectKey = objectKey + FOLDER_SUFFIX;

		if (!objectKey.contains("root/"))
			objectKey = appendRoot + objectKey;

		return objectKey.replace("\\//", "/");
	}

	public static StorageObject convertTStorageObject(S3DataObject object)
	{

		if (object.getType().equals("folder"))
		{
			return new StorageS3Folder(object);
		} else
		{
			return new StorageS3Document(object);
		}

	}

}
