package pl.dev.news.devnewsservice.service;

import com.google.cloud.storage.BlobInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface GoogleFileService {

    BlobInfo bucketUpload(MultipartFile file, String bucketName);

    String bucketStreamUpload(InputStream stream, String bucketName, String filename);

    boolean delete(String bucket, String filename);
}
