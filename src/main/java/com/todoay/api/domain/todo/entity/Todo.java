package com.todoay.api.domain.todo.entity;

import com.todoay.api.domain.auth.entity.Auth;
import com.todoay.api.domain.hashtag.entity.Hashtag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
public abstract class Todo {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(nullable = false)
    protected String title;

    @Column(columnDefinition = "TEXT")
    protected String description;

    @Column(nullable = false)
    protected boolean isPublic = false;

    @Column(nullable = false)
    protected boolean isFinished = false;

    @ManyToOne
    @JoinColumn(name = "auth_id", nullable = false)
    protected Auth auth;

    @OneToMany(mappedBy = "todo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<TodoHashtag> todoHashtags = new ArrayList<>();

    // 연관관계 메소드
    public void associateWithHashtag(List<Hashtag> hashtags) {
        // hashtag를 받아와서 todohashtag를 만들고 이걸 설정해준다.
        this.todoHashtags.clear();
        for (Hashtag hashtag : hashtags) {
            TodoHashtag todoHashtag = TodoHashtag.builder()
                    .todo(this)
                    .hashTag(hashtag).build();
            this.todoHashtags.add(todoHashtag); // cascade로 인해 생성됨.
        }
    }
    public void switchFinishState() {
        isFinished = !isFinished;
    }
}
