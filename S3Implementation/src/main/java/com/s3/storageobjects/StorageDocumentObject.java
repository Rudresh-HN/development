package com.s3.storageobjects;

import java.io.InputStream;

public interface StorageDocumentObject extends StorageObject
{
	void deleteDocument();
	
	InputStream getContentStream();
	
	String getFileName();
	
	String getContentMimeType();
	
	String getContnetUrl();
	
	StorageDocumentObject rename(String fileName);
	
}
