package pl.dev.news.devnewsservice.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class BaseEntity extends AuditableEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    protected UUID id;
}
