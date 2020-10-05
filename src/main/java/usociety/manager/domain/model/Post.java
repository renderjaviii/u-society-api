package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "creation_date", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime creationDate;

    @Column(name = "expiration_date", columnDefinition = "DATETIME")
    private LocalDateTime expirationDate;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    public Post() {
        super();
    }

    private Post(Builder builder) {
        id = builder.id;
        creationDate = builder.creationDate;
        expirationDate = builder.expirationDate;
        isPublic = builder.isPublic;
        content = builder.content;
        group = builder.group;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getContent() {
        return content;
    }

    public Group getGroup() {
        return group;
    }

    public static final class Builder {

        private Long id;
        private LocalDateTime creationDate;
        private LocalDateTime expirationDate;
        private boolean isPublic;
        private String content;
        private Group group;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder expirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Post build() {
            return new Post(this);
        }

    }

}