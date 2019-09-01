package pl.dev.news.devnewsservice.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = {
        "parent", "children", "posts"
})
@ToString(exclude = {
        "parent", "children", "posts"
})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", columnDefinition = "citext")
    private String name;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private UUID parentId;

    @OneToMany(mappedBy = "parent", cascade = {
            CascadeType.PERSIST,
            CascadeType.DETACH
    })
    private Set<CategoryEntity> children;

    @ManyToMany(mappedBy = "categories")
    private Set<PostEntity> posts;

}
