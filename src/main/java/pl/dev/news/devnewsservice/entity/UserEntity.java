package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.EnumType.STRING;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {
        "posts", "groups", "comments", "uploads", "followingGroups",
        "followingTags", "bookmarks", "followingUsers", "followers",
        "image", "bg"
})
@ToString(exclude = {
        "posts", "groups", "comments", "uploads", "followingGroups",
        "followingTags", "bookmarks", "followingUsers", "followers",
        "image", "bg"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity implements UserDetails  {

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_upload_id")
    private UploadEntity image;

    @Column(name = "bg_url")
    private String bgUrl;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bg_upload_id")
    private UploadEntity bg;

    @Column(name = "first_name", columnDefinition = "citext")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "citext")
    private String lastName;

    @Column(name = "full_name", columnDefinition = "citext")
    private String fullName;

    @Column(name = "birthday")
    private Instant birthday;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "activation_key")
    private UUID activationKey;

    @Column(name = "reset_key")
    private UUID resetKey;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @OneToMany(mappedBy = "publisher")
    private Set<PostEntity> posts;

    @OneToMany(mappedBy = "owner")
    private Set<GroupEntity> groups;

    @OneToMany(mappedBy = "user")
    private Set<CommentEntity> comments;

    @OneToMany(mappedBy = "user")
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


    public void addFolower(final UserEntity user) {
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

    public void addBookmark(final PostEntity post) {
        if (this.bookmarks == null) {
            this.bookmarks = new HashSet<>();
        }
        this.bookmarks.add(post);
    }

    public void removeBookmark(final PostEntity post) {
        if (this.bookmarks != null) {
            this.bookmarks.remove(post);
        }
    }

    // UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
