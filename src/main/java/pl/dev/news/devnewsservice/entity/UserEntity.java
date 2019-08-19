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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.EnumType.STRING;

@Getter
@Setter
@EqualsAndHashCode(
        callSuper = false,
        exclude = {"posts", "followingTags", "bookmarks", "followingUsers", "followers", "comments", "uploads"}
)
@ToString(exclude = {"posts", "followingTags", "bookmarks", "followingUsers", "followers", "comments", "uploads"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username")
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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birthday")
    private Instant birthday;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @OneToMany(mappedBy = "publisher")
    private Set<PostEntity> posts;

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

    @OneToMany(mappedBy = "user")
    private Set<CommentEntity> comments;

    @OneToMany(mappedBy = "post")
    private Set<UploadEntity> uploads;

    // ============ //
    //  UserDetails //
    // ============ //

    public UserEntity(final UserEntity userEntity) {
        this.id = userEntity.getId();
        this.role = userEntity.getRole();
    }

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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
