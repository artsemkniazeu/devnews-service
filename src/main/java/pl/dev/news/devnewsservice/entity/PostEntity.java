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
import javax.persistence.Table;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(
        callSuper = false,
        exclude = {"publisher", "usersSaved", "tags"}
)
@ToString(exclude = {"publisher", "usersSaved", "tags"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class PostEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "date", nullable = false)
    private Instant date;

    //List<String> authors
    //List<String> photosAuthor

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private UserEntity publisher;

    @ManyToMany(mappedBy = "bookmarks")
    private Set<UserEntity> usersSaved;

    @ManyToMany
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagEntity> tags;

}
