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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(
        callSuper = false,
        exclude = {"tags", "parent", "children"}
)
@ToString(exclude = {"tags", "parent", "children"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity extends AuditableEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @OneToMany(mappedBy = "category")
    private Set<TagEntity> tags;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private CategoryEntity parent;

    @OneToMany(mappedBy = "parent")
    private Set<CategoryEntity> children;

}
