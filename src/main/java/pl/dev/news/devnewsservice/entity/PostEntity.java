package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "update_date")
    private Instant updateDate;

    @Column(name = "publish_date")
    private Instant publishDate;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private UserEntity publisher;

    @Column(name = "publisher_id", insertable = false, updatable = false)
    private UUID publisherId;

    @ManyToMany(mappedBy = "bookmarks")
    private Set<UserEntity> usersSaved;

    @ManyToMany
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagEntity> tags;

    @ManyToMany
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<CategoryEntity> categories;

    @OneToMany(mappedBy = "user")
    private Set<UploadEntity> uploads;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity group;

}
