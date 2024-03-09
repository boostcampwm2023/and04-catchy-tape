package catchytape.spring.recentPlayed;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import catchytape.spring.music.Music;
import catchytape.spring.user.User;

@Table(name="recent_played", indexes = @Index(name="idx_recent_played_user", columnList = "user_id"))
@Entity(name="recent_played")
public class RecentPlayed {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long recent_played_id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="music_id")
    private Music music;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column
    private Date played_at;
}
