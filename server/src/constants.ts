export const fileSize: Record<string, number> = {
  MUSIC_FILE_LIMIT_SIZE: 1024 * 1024 * 50,
  IMAGE_FILE_LIMIT_SIZE: 1024 * 1024 * 5,
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

export const fileExt = ['jpeg', 'jpg', 'png', 'mp3'];

export const pathPattern = [
  /^image\/user\/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/,
  /^image\/cover\/\d+$/,
  /^music\/\d+$/,
];
