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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = {
        "followers", "posts"
})
@ToString(exclude = {
        "followers", "posts"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class TagEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", columnDefinition = "citext")
    private String name;

    @ManyToMany(mappedBy = "followingTags")
    private Set<UserEntity> followers;

    @ManyToMany(mappedBy = "tags")
    private Set<PostEntity> posts;

}
