package catchytape.spring.playlist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import catchytape.spring.musicPlaylist.MusicPlaylist;
import catchytape.spring.user.User;

@Table(name="playlist", indexes = @Index(name="idx_playlist_user", columnList = "user_id"))
@Entity
public class Playlist {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playlist_id;

    @Column
    private String playlist_title;
  
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
  
    @OneToMany(mappedBy = "playlist")
    private List<MusicPlaylist> music_playlist = new ArrayList<>();

    @Column(updatable = false, nullable = false)
    @CreatedDate
    private Date created_at;

    @Column()
    private Date updated_at;
}
