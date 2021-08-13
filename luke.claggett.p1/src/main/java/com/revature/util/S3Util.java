package com.revature.util;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Util {
	public static final Region REGION = Region.US_EAST_2;
	public static final String BUCKET_NAME = "mtm-project1";

	private static Logger log = LogManager.getLogger(S3Util.class);

	private static S3Util instance = null;

	private S3Client client = null;

	private S3Util() {
		client = S3Client.builder().region(REGION).build();
	}

	public static synchronized S3Util getInstance() {
		if (instance == null) {
			instance = new S3Util();
		}
		return instance;
	}

	public void uploadToBucket(String key, byte[] file) {
		log.trace("Uploading the file: " + key);
		client.putObject(PutObjectRequest.builder().bucket(BUCKET_NAME).key(key).build(), RequestBody.fromBytes(file));
		log.trace("The upload is complete");
	}

	public InputStream getObject(String key) {
		log.trace("Retriving file: " + key);
		InputStream object = client.getObject(GetObjectRequest.builder().bucket(BUCKET_NAME).key(key).build());
		log.trace("Retrieval Complete");
		return object;
	}
}
