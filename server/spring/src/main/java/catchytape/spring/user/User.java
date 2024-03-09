package catchytape.spring.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;

import catchytape.spring.music.Music;
import catchytape.spring.playlist.Playlist;
import catchytape.spring.recentPlayed.RecentPlayed;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="user")
@Getter
@Entity
public class User implements Persistable<String> {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "photo")
    private String photo;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "user")
    private List<Music> musics = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<RecentPlayed> recent_played = new ArrayList<>();

    public User(String userId, String email) {
        this.userId = userId;
        this.userEmail = email;
        this.photo = null;
        this.isDeleted = false;
    }

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return getCreatedAt() == null; // 객체가 만들어진 시간[BaseEntity에 존재함]
    }
}
