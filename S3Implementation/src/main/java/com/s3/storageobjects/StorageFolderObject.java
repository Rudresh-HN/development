package com.s3.storageobjects;

import java.io.InputStream;
import java.util.Map;

public interface StorageFolderObject extends StorageObject
{
	
	StorageDocumentObject createDocument(Map<String, Object> properties, InputStream inputStream);
	
	StorageFolderObject creatFolder(Map<String, Object> properties);
	
	Iterable<StorageObject> getChildern();
	
	Integer getChildernCount();
	
	String getParentId();
	
	String getPath();
}
