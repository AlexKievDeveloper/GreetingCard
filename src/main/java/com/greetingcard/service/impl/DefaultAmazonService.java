package com.greetingcard.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.greetingcard.service.AmazonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class DefaultAmazonService implements AmazonService {

    private AmazonS3 s3client;

    @Value("${bucketName}")
    private String bucketName;
    @Value("${accessKey}")
    private String accessKey;
    @Value("${secretKey}")
    private String secretKey;
    @Value("${region}")
    private String region;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Override
    public void uploadFile(MultipartFile multipartFile, String fileName) {

        try {
            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (AmazonServiceException ase) {
            log.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
            log.info("Error Message: {}", ase.getMessage());
            log.info("HTTP Status Code: {}", + ase.getStatusCode());
            log.info("AWS Error Code: {}", ase.getErrorCode());
            log.info("Error Type: {}", ase.getErrorType());
            log.info("Request ID: {}", ase.getRequestId());
        } catch (AmazonClientException ace) {
            log.info("Caught an AmazonClientException: ");
            log.info("Error Message: {}", ace.getMessage());
        } catch (IOException ioe) {
            log.info("IOE Error Message: {}", ioe.getMessage());
        }
    }

    @Override
    public void deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.indexOf("/") + 1);
        log.info("Filename in Amazon service before deleting with s3: {}", fileName);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        log.info("Successfully deleted file: {}", fileName);
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}