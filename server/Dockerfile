FROM node:20

WORKDIR /catchy-tape

COPY . .

RUN npm install
RUN npm run build

EXPOSE 3000
CMD [ "npm", "run", "start:prod" ]