const AWS = require('aws-sdk');
const ffmpeg = require('fluent-ffmpeg');
const { path } = require('@ffmpeg-installer/ffmpeg');
const fs = require('fs');
const axios = require('axios');
const Path = require('path');

function setObjectStorage(accessKey, secretKey) {
  return new AWS.S3({
    endpoint: 'https://kr.object.ncloudstorage.com',
    region: 'kr-standard',
    credentials: {
      accessKeyId: accessKey,
      secretAccessKey: secretKey,
    },
    signatureVersion: 'v4',
  });
}

function separateMusicName(musicPath) {
  const parsedPath = new URL(musicPath);
  const pathNames = parsedPath.pathname.split('/');
  const musicName = pathNames[pathNames.length - 1];

  return musicName;
}

function getPath(option) {
  return Path.resolve(`musics${option}`);
}

function setEncodingPaths(musicPath) {
  const musicName = separateMusicName(musicPath);

  return {
    outputMusicPath: getPath('/output'),
    entireMusicPath: getPath(''),
    outputPath: getPath(`/output/${musicName.replace('.mp3', '')}.m3u8`),
    tempFilePath: getPath(`/${musicName}`),
  };
}

async function uploadToObjectStorage(
  filePath,
  musicId,
  fileName,
  objectStorage,
) {
  const result = await objectStorage
    .upload({
      Bucket: 'catchy-tape-bucket2',
      Key: `music/${musicId}/${fileName}`,
      Body: fs.createReadStream(filePath),
      ACL: 'public-read',
    })
    .promise();

  return { url: result.Location };
}

async function uploadEncodedFile(filePath, musicId, fileName, objectStorage) {
  try {
    const { url } = await uploadToObjectStorage(
      filePath,
      musicId,
      fileName,
      objectStorage,
    );
    return url;
  } catch (err) {
    console.log(err);
  }
}

async function encodeMusic(musicId, musicPath, objectStorage) {
  try {
    ffmpeg.setFfmpegPath(path);

    const { outputMusicPath, outputPath, tempFilePath } =
      setEncodingPaths(musicPath);

    fs.mkdirSync(outputMusicPath, { recursive: true });

    const musicFileResponse = await axios.get(musicPath, {
      responseType: 'arraybuffer',
    });

    const musicBuffer = Buffer.from(musicFileResponse.data);

    fs.writeFile(tempFilePath, musicBuffer, (err) => {
      if (err) throw new Error();
    });

    const encodedFileURL = await executeEncoding(
      tempFilePath,
      outputPath,
      outputMusicPath,
      musicId,
      objectStorage,
    );

    return encodedFileURL;
  } catch (err) {
    console.log(err);
  }
}

async function executeEncoding(
  tempFilePath,
  outputPath,
  outputMusicPath,
  musicId,
  objectStorage,
) {
  let m3u8FileName = '';
  let m3u8Path = '';
  const watcher = fs.watch(outputMusicPath, async (eventType, fileName) => {
    if (fileName.match(/.m3u8$/)) {
      m3u8FileName = fileName;
    } else if (!fileName.match(/\.tmp$/)) {
      await uploadEncodedFile(
        outputMusicPath + `/${fileName}`,
        musicId,
        fileName,
        objectStorage,
      );
    }
  });

  return await new Promise((resolve, reject) => {
    ffmpeg(tempFilePath)
      .addOption([
        '-map 0:a',
        '-c:a aac',
        '-b:a 192k',
        '-hls_time 30',
        '-hls_list_size 0',
        '-f hls',
      ])
      .output(outputPath)
      .on('end', async () => {
        watcher.close();
        m3u8Path = await uploadEncodedFile(
          outputMusicPath + `/${m3u8FileName}`,
          musicId,
          m3u8FileName,
          objectStorage,
        );
        resolve(m3u8Path);
      })
      .on('error', () => {
        reject(new Error());
      })
      .run();
  });
}

async function main(params) {
  const bucket = params.container_name;
  const music_id = params.music_id;

  const musicPath = `https://kr.object.ncloudstorage.com/${bucket}/music/${music_id}/music.mp3`;
  const objectStorage = setObjectStorage(params.accessKey, params.secretKey);

  const url = await encodeMusic(music_id, musicPath, objectStorage);

  return {
    statusCode: 200,
    headers: { 'Content-Type': 'application/json' },
    body: {
      url,
    },
  };
}
