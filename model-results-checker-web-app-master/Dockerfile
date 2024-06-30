FROM gradle:6.7-jdk15

WORKDIR /app

RUN apt-get update && apt-get install -y gnupg
RUN echo 'deb http://ftp.nl.debian.org/debian sid main' >> /etc/apt/sources.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 04EE7237B7D453EC
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 648ACFD622F3D138

RUN apt-get update && apt-get install -y \
    ffmpeg

COPY . .

VOLUME app/data

RUN gradle build -x test

EXPOSE 8080

ENTRYPOINT ["gradle" ]
