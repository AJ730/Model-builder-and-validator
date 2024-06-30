FROM node:16-alpine3.11

WORKDIR /app

ENV PATH /app/node_modules/.bin:$PATH

COPY app/package.json .
COPY app/package-lock.json .
RUN npm install
RUN npm install react-scripts@3.4.1 -g --silent

COPY app .

ENV GENERATE_SOURCEMAP false
RUN npm run build

EXPOSE 3000

CMD ["npm","run","production"]
