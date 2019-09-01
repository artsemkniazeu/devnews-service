package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Set;

import static javax.persistence.EnumType.STRING;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {
        "posts", "groups", "comments", "uploads", "followingGroups",
        "followingTags", "bookmarks", "followingUsers", "followers"
})
@ToString(exclude = {
        "posts", "groups", "comments", "uploads", "followingGroups",
        "followingTags", "bookmarks", "followingUsers", "followers"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "username", columnDefinition = "citext")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password")
    private String password;

    @Enumerated(STRING)
    @Column(name = "role")
    private UserRoleEntity role;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "bg_url")
    private String bgUrl;

    @Column(name = "first_name", columnDefinition = "citext")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "citext")
    private String lastName;

    @Column(name = "birthday")
    private Instant birthday;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @OneToMany(mappedBy = "publisher")
    private Set<PostEntity> posts;

    @OneToMany(mappedBy = "owner")
    private Set<GroupEntity> groups;

    @OneToMany(mappedBy = "user")
    private Set<CommentEntity> comments;

    @OneToMany(mappedBy = "post")
    private Set<UploadEntity> uploads;

    @ManyToMany(mappedBy = "followers")
    private Set<GroupEntity> followingGroups;

    @ManyToMany
    @JoinTable(
            name = "user_tag",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagEntity> followingTags;

    @ManyToMany
    @JoinTable(
            name = "user_bookmark",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private Set<PostEntity> bookmarks;

    @ManyToMany(mappedBy = "followers")
    private Set<UserEntity> followingUsers;

    @ManyToMany
    @JoinTable(
            name = "user_follower",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<UserEntity> followers;



}
