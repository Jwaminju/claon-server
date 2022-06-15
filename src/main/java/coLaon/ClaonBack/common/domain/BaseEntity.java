package coLaon.ClaonBack.common.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public class BaseEntity{
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;
    @CreatedDate
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    protected BaseEntity(String id) {
        this.id = id;
    }
}