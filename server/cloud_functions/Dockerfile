FROM cloudfunctions.kr.ncr.ntruss.com/cloudfunctions-nodejs-16:latest

RUN npm install fluent-ffmpeg
RUN npm install @ffmpeg-installer/ffmpeg
RUN npm install axios
RUN npm install aws-sdk

WORKDIR /nodejsAction

CMD node --expose-gc app.js