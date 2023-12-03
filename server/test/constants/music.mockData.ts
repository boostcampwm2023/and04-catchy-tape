import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Genres } from 'src/constants';
import { User } from 'src/entity/user.entity';

export const musicCreateInfo: MusicCreateDto = {
  music_id: 'music_id',
  title: '내가부른노래예시1',
  cover: 'cover이미지.png',
  file: 'https://kr.object.ncloudstorage.com/catchy-tape-bucket2/music/6bcd415d-4a22-43f0-9daa-64199442ff2d/music.mp3',
  genre: 'dance' as Genres,
};

export const faultGenreMusicCreateInfo: MusicCreateDto = {
  music_id: 'music_id',
  title: '내가부른노래예시1',
  cover: 'cover이미지.png',
  file: 'https://kr.object.ncloudstorage.com/catchy-tape-bucket2/music/6bcd415d-4a22-43f0-9daa-64199442ff2d/music.mp3',
  genre: 'pop-pin' as Genres,
};

export const newMusicData: Music = {
  music_id: 'music_id',
  title: '내가부른노래예시1',
  cover: 'cover이미지.png',
  genre: 'dance' as Genres,
  music_file: 'encodedURL',
  created_at: new Date(),
  lyrics: null,
  user: {
    user_id: '아이디',
    nickname: '나',
    photo: '이미지',
    user_email: '이메일',
    created_at: new Date(),
  } as User,
  music_playlist: [],
  hasId: () => true,
  save: async () => newMusicData,
  remove: async () => newMusicData,
  softRemove: async () => newMusicData,
  recover: async () => newMusicData,
  reload: async () => {},
};
