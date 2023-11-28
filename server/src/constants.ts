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

export const RECENT_PLAYLIST_NAME = '최근 재생 목록';
export const keyFlags = ['user', 'cover'];

export const contentTypeHandler: Record<string, string> = {
  image: 'image/png',
  music: 'audio/mpeg',
};
