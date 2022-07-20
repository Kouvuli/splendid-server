package com.example.server.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String content;

    @Column(name = "create_at")
    private Timestamp createAt;

    @OneToMany(mappedBy = "comment")
    @JsonIgnore
    private Set<Reaction> reactions=new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "post_id",referencedColumnName = "id")
    private Post post;


    @ManyToOne
    @JoinColumn(name = "author_id",referencedColumnName = "id")
    private User author;

    public Comment(int id, String content, Timestamp createAt, Post post, User author) {
        this.id = id;
        this.content = content;
        this.createAt = createAt;
        this.post = post;
        this.author = author;
    }

    public Comment() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(Set<Reaction> reactions) {
        this.reactions = reactions;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createAt=" + createAt +
                ", reactions=" + reactions +
                ", post=" + post +
                ", author=" + author +
                '}';
    }
}
