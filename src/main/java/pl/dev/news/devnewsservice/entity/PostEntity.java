package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = {
        "publisher", "usersSaved", "tags", "categories", "uploads", "group"
})
@ToString(exclude = {
        "publisher", "usersSaved", "tags", "categories", "uploads", "group"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class PostEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", columnDefinition = "citext")
    private String title;

    @Column(name = "text", columnDefinition = "citext")
    private String text;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "update_date")
    private Instant updateDate;

    @Column(name = "publish_date")
    private Instant publishDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private UserEntity publisher;

    @Column(name = "publisher_id", insertable = false, updatable = false)
    private UUID publisherId;

    @ManyToMany(mappedBy = "bookmarks")
    private Set<UserEntity> usersSaved;

    @BatchSize(size = 10)
    @Fetch(FetchMode.JOIN)
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagEntity> tags;

    @BatchSize(size = 10)
    @Fetch(FetchMode.JOIN)
    @ManyToMany(cascade = {
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories;

    @BatchSize(size = 10)
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "user", cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH
    })
    private Set<UploadEntity> uploads;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupEntity group;

    @Column(name = "group_id", insertable = false, updatable = false)
    private UUID groupId;

    public void addTag(final TagEntity tag) {
        this.tags.add(tag);
    }

    public void addCategory(final CategoryEntity category) {
        this.categories.add(category);
    }

    public void addUpload(final UploadEntity upload) {
        this.uploads.add(upload);
    }
}
