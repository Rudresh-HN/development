package com.s3.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.s3.exception.S3RuntimeException;
import com.s3.model.S3DocumentServiceImpl;
import com.s3.service.DocumentService;
import com.s3.util.NullUtill;

import jakarta.annotation.PostConstruct;

@Configuration
public class BaseConfig
{
	@Autowired
	ConfigurableEnvironment configurableEnvironment;
	
	Logger logger = LoggerFactory.getLogger(BaseConfig.class);
	
	@PostConstruct
	public void initialize() {
		
		try
		{
		
			String bucketName =  configurableEnvironment.getProperty("S3_BUCKETNAME");
			
			if(NullUtill.isNullorEmpty(bucketName))
				logger.error("S3 bucket name can't be empty");
					System.exit(0);
			
			final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().build();
			final TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
			
			DocumentService documentService =  new S3DocumentServiceImpl(amazonS3,transferManager,bucketName);
			
			logger.debug("DocumentService gets initialized "+ documentService.toString());
			
		} catch (Exception e)
		{
			throw new S3RuntimeException("Failed to initalize s3 object "+ e.getMessage());
		}
		
	}
	
}
