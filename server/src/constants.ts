export const fileSize: Record<string, number> = {
  MUSIC_SIZE: 1024 * 1024 * 50,
  IMAGE_SIZE: 1024 * 1024 * 5,
};

export enum Genres {
  'hip-hop' = 'hip-hop',
  'acoustic' = 'acoustic',
  'ballade' = 'ballade',
  'r&b' = 'r&b',
  'jazz' = 'jazz',
  'rock' = 'rock',
  'dance' = 'dance',
  'etc' = 'etc',
}

export const keyFlags = ['user', 'music', 'cover'];

export const keyHandler: {
  [key: string]: (uuid: string) => string;
} = {
  user: (uuid) => `image/user/${uuid}/user.png`,
  music: (uuid) => `music/${uuid}/music.mp3`,
  cover: (uuid) => `image/cover/${uuid}/cover.png`,
};
