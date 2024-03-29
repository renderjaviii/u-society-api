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
@Table(name = "surveys", uniqueConstraints = @UniqueConstraint(columnNames = { "post_id", "user_id" }))
public class Survey extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "vote", nullable = false)
    private Integer vote;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Post post;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Survey() {
        super();
    }

    private Survey(Builder builder) {
        id = builder.id;
        vote = builder.vote;
        post = builder.post;
        userId = builder.userId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Integer getVote() {
        return vote;
    }

    public Post getPost() {
        return post;
    }

    public Long getUserId() {
        return userId;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Survey)) {
            return false;
        }
        return Objects.equals(((Survey) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private Integer vote;
        private Post post;
        private Long userId;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder vote(Integer vote) {
            this.vote = vote;
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

        public Survey build() {
            return new Survey(this);
        }

    }

}
