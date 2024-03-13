package catchytape.spring.music;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import catchytape.spring.common.constant.Genres;
import catchytape.spring.musicPlaylist.MusicPlaylist;
import catchytape.spring.recentPlayed.RecentPlayed;
import catchytape.spring.user.User;

@Table(name="music")
@Entity
public class Music {
    @Id
    @Column(name = "music_id")
    private String music_id;
  
    @Column()
    private String title;
  
    @Column(nullable = true)
    private String lyrics;
  
    @Column()
    private String cover;
  
    @Column(name = "music_file")
    private String music_file;
  
    @Column()
    @Enumerated(EnumType.STRING)
    private Genres genre;
  
    @Column(name = "created_at", updatable = false, nullable = false)
    @CreatedDate
    private Date created_at;
  
    @ManyToOne
    private User user;
  
    @OneToMany(mappedBy = "music")
    private List<MusicPlaylist> music_playlist = new ArrayList<>();
  
    @OneToMany(mappedBy = "music")
    private List<RecentPlayed> recent_played = new ArrayList<>();
}
