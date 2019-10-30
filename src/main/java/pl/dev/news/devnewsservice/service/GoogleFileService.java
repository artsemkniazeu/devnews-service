package pl.dev.news.devnewsservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface GoogleFileService {

    String bucketUpload(MultipartFile file, String bucketName);

    String bucketStreamUpload(InputStream stream, String bucketName, String filename);

}
