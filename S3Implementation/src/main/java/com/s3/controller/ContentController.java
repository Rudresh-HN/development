package com.s3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.s3.service.ContentService;
import com.s3.storageobjects.StorageFolderObject;

@RestController
@RequestMapping("/s3")
public class ContentController
{

	@Autowired
	ContentService contentService;

	@PostMapping("/root")
	public String getRootFolder()
	{
		StorageFolderObject storageFolderObject = contentService.getRootFolder();
		return storageFolderObject.getChildern().toString();
	}

	@PostMapping(value = "/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void addFileToS3(@RequestParam("key") String key, @RequestParam("file[]") MultipartFile[] documents)
	{
		contentService.addFileToS3(key, documents);
	}

	@PostMapping(
			value = "/folder", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
	)
	public StorageFolderObject createFolder(@RequestParam("folderName") String folderName)
	{
		return contentService.checkAndCreateFolder(folderName);
	}

}
