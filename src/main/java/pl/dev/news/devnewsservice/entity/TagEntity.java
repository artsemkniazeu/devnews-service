package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

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
public class TagEntity extends BaseEntity {


    @Column(name = "name", columnDefinition = "citext")
    private String name;

    @ManyToMany(mappedBy = "followingTags")
    private Set<UserEntity> followers;

    @ManyToMany(mappedBy = "tags")
    private Set<PostEntity> posts;

}
