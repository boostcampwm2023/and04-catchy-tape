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

export const keyFlags = ['user', 'cover'];

export const contentTypeHandler: Record<string, string> = {
  image: 'image/png',
  music: 'audio/mpeg',
};

export const getTimeStamp = (): number => {
  return new Date().getTime();
};

export const SLICE_COUNT: number =
  'https://catchy-tape-bucket2.kr.object.ncloudstorage.com/'.length;

export const ONE_WEEK_TO_SECONDS: number = 60 * 60 * 24 * 7;

export const ONE_HOUR_TO_SECONDS: number = 60 * 60;
