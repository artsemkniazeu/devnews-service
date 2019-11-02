package pl.dev.news.devnewsservice.service.impl;

import com.google.cloud.storage.BlobInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.mapper.UploadMapper;
import pl.dev.news.devnewsservice.repository.UploadRepository;
import pl.dev.news.devnewsservice.service.GoogleFileService;
import pl.dev.news.devnewsservice.service.UploadService;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UploadRepository uploadRepository;

    private final GoogleFileService fileService;

    private final UploadMapper uploadMapper = UploadMapper.INSTANCE;


    @Override
    @Transactional
    public UploadEntity upload(final UserEntity entity, final MultipartFile file, final String bucket) {
        final BlobInfo blobInfo = fileService
                .bucketUpload(file, bucket);
        final UploadEntity uploadEntity = uploadMapper.toEntity(entity, blobInfo);
        return uploadRepository.saveAndFlush(uploadEntity);
    }

    @Override
    public void delete(final UploadEntity entity) {
        fileService.delete(entity.getBucket(), entity.getFilename());
    }
}
