package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = {
        "user", "post"
})
@ToString(exclude = {
        "user", "post"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "uploads")
public class UploadEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "url")
    private String url;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "filename")
    private String filename;

    @Column(name = "generation")
    private Long generation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "post_id", insertable = false, updatable = false)
    private UUID postId;

}
