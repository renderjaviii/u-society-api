package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import usociety.manager.app.util.BaseObject;

@Entity
@Table(name = "reacts", uniqueConstraints = @UniqueConstraint(columnNames = { "post_id", "user_id" }))
public class React extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Post post;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "value", length = 10, nullable = false)
    private String value;

    public React() {
        super();
    }

    private React(Builder builder) {
        id = builder.id;
        post = builder.post;
        userId = builder.userId;
        value = builder.value;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public Long getUserId() {
        return userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof React)) {
            return false;
        }
        return Objects.equals(((React) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private Post post;
        private Long userId;
        private String value;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder post(Post post) {
            this.post = post;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public React build() {
            return new React(this);
        }

    }

}
