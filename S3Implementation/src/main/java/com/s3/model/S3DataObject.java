package com.s3.model;

import java.util.GregorianCalendar;
import java.util.List;

public class S3DataObject
{

	private String name;
	private String path;	
	private String id;
	private String type;
	private String contentUrl;
	private String contentMimeType;
	private GregorianCalendar lastModificationDate;
	private List<String> paths;
	
	public S3DataObject()
	{
		super();
	}

	public S3DataObject(
			String name, String path, String id, String type, String contentUrl, String contentMimeType,
			GregorianCalendar lastModificationDate, List<String> paths
	)
	{
		super();
		this.name = name;
		this.path = path;
		this.id = id;
		this.type = type;
		this.contentUrl = contentUrl;
		this.contentMimeType = contentMimeType;
		this.lastModificationDate = lastModificationDate;
		this.paths = paths;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getContentUrl()
	{
		return contentUrl;
	}

	public void setContentUrl(String contentUrl)
	{
		this.contentUrl = contentUrl;
	}

	public String getContentMimeType()
	{
		return contentMimeType;
	}

	public void setContentMimeType(String contentMimeType)
	{
		this.contentMimeType = contentMimeType;
	}

	public GregorianCalendar getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate(GregorianCalendar lastModificationDate)
	{
		this.lastModificationDate = lastModificationDate;
	}

	public List<String> getPaths()
	{
		return paths;
	}

	public void setPaths(List<String> paths)
	{
		this.paths = paths;
	}
	
	
}
