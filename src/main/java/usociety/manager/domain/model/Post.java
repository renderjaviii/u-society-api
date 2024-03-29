package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import usociety.manager.app.util.BaseObject;

@Entity
@Table(name = "posts")
public class Post extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Group group;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    public Post() {
        super();
    }

    private Post(Builder builder) {
        id = builder.id;
        creationDate = builder.creationDate;
        expirationDate = builder.expirationDate;
        isPublic = builder.isPublic;
        content = builder.content;
        description = builder.description;
        group = builder.group;
        userId = builder.userId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Post)) {
            return false;
        }
        return Objects.equals(((Post) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private LocalDateTime creationDate;
        private LocalDateTime expirationDate;
        private Boolean isPublic;
        private String content;
        private String description;
        private Group group;
        private Long userId;

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

        public Builder isPublic(Boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Post build() {
            return new Post(this);
        }

    }

}
