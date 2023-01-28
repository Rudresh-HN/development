package com.s3.storageobjects;

import java.util.GregorianCalendar;
import java.util.List;

public interface StorageObject
{
	
	void delete();
	
	String getName();
	
	GregorianCalendar getLastModificationDate();
	
	List<StorageFolderObject> getParent();
	
	List<String> getPaths();
	
	String getId();

}
