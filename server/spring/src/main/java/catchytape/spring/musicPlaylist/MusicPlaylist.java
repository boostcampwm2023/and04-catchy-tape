package catchytape.spring.musicPlaylist;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import catchytape.spring.music.Music;
import catchytape.spring.playlist.Playlist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name="music_playlist", indexes = {@Index(name="idx_playlist", columnList = "playlist_id"), @Index(name="idx_created_at", columnList = "created_at")})
@Getter
public class MusicPlaylist {
    @Id
    @Column(name = "music_playlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long music_playlist_id;
  
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Music music;
  
    @ManyToOne
    @JoinColumn(name="playlist_id")
    private Playlist playlist;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreatedDate
    private Date created_at;
}
