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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {
        "owner", "followers", "posts"
})
@ToString(exclude = {
        "owner", "followers", "posts"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class GroupEntity extends BaseEntity {

    @Column(name = "name", columnDefinition = "citext")
    private String name;

    @Column(name = "value", columnDefinition = "citext")
    private String value;

    @Column(name = "about")
    private String about;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_upload_id")
    private UploadEntity image;

    @Column(name = "bg_url")
    private String bgUrl;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bg_upload_id")
    private UploadEntity bg;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private UUID ownerId;

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> followers;

    @OneToMany(mappedBy = "group")
    private Set<PostEntity> posts;

    public void addFollower(final UserEntity user) {
        if (this.followers == null) {
            this.followers = new HashSet<>();
        }
        this.followers.add(user);
    }

    public void removeFollower(final UserEntity user) {
        if (this.followers != null) {
            this.followers.remove(user);
        }
    }
}
