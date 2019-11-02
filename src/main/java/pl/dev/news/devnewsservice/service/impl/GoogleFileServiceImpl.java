package pl.dev.news.devnewsservice.service.impl;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.devnewsservice.exception.UnprocessableEntityException;
import pl.dev.news.devnewsservice.service.GoogleFileService;
import pl.dev.news.devnewsservice.utils.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.fileCorruptOrUnreadable;

@Service
@RequiredArgsConstructor
public class GoogleFileServiceImpl implements GoogleFileService {

    private final Storage storage;

    @Override
    public BlobInfo bucketUpload(final MultipartFile file, final String bucketName) {
        try {
            final Bucket bucket = storage.get(bucketName);
            return bucket.create(FileUtil.generateUniqueName(file.getOriginalFilename()), file.getBytes());
        } catch (final IOException e) {
            throw new UnprocessableEntityException(fileCorruptOrUnreadable, e);
        }
    }

    @Override
    public String bucketStreamUpload(
            final InputStream stream,
            final String bucketName,
            final String filename
    ) {
        final BlobInfo blobInfo = createBlobInfo(createBlobId(bucketName, filename));
        try (WriteChannel writer = storage.writer(blobInfo)) {
            final byte[] buffer = new byte[20_000_000];
            int limit = stream.read(buffer);
            while (limit >= 0) {
                writer.write(ByteBuffer.wrap(buffer, 0, limit));
                limit = stream.read(buffer);
            }
        } catch (IOException e) {
            throw new UnprocessableEntityException(fileCorruptOrUnreadable, e);
        }
        final Blob blob = storage.get(blobInfo.getBlobId());
        return blob.getMediaLink();
    }

    @Override
    public boolean delete(final String bucket, final String filename) {
        final BlobId blobId = BlobId.of(bucket, filename);
        return storage.delete(blobId);
    }

    private BlobId createBlobId(final String bucket, final String file) {
        return BlobId.of(bucket, FileUtil.generateUniqueName(file));
    }

    private BlobInfo createBlobInfo(final BlobId blobId) {
        return BlobInfo.newBuilder(blobId)
                .build();
    }

}
