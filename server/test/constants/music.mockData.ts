import { MusicCreateDto } from 'src/dto/musicCreate.dto';
import { Music } from 'src/entity/music.entity';
import { Genres } from 'src/constants';
import { User } from 'src/entity/user.entity';

export const user: User = {
  user_id: '2d4b574c-b370-4e2e-ac4f-e1eca8eea090',
  nickname: 'master2',
  photo: null,
  user_email: 'sugamypapa@gmail.com',
  created_at: new Date('2023-12-04 23:57:50.693000'),
} as User;

export const realMusicCreateInfo: MusicCreateDto = {
  music_id: '6bcd415d-4a22-43f0-9daa-64199442ff2d',
  title: 'mysongone',
  cover:
    'https://kr.object.ncloudstorage.com/catchy-tape-bucket2/image/cover/6bcd415d-4a22-43f0-9daa-64199442ff2d/image.png',
  file: 'https://kr.object.ncloudstorage.com/catchy-tape-bucket2/music/6bcd415d-4a22-43f0-9daa-64199442ff2d/music.mp3',
  genre: 'acoustic' as Genres,
};

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
  recent_played: [],
} as Music;
