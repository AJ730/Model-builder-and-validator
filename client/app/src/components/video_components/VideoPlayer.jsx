import React, { useRef, useState, useEffect } from "react";

//videojs essentials
import videojs from "video.js";
import 'video.js/dist/video-js.css';
import '@videojs/themes/dist/forest/index.css';

//videojs plugins
import overlay from 'videojs-overlay'
import seekButtons from 'videojs-seek-buttons'


import InteractiveDisplay from "./InteractiveDisplay";
import PredictionDisplay from "./PredictionDisplay";
import hotkeys from "videojs-hotkeys";

export const VideoPlayer = ({ source, modifiableCsv, persistentCsv, classData, token, account, fps }) => {

  // reference <video/>
  const videoPlayerRef = useRef(null);

  // fps from Recycleye video, taken from ffprobe (should be fetched from the back-end!!!)

  const videoWidth = 960;
  const videoHeight = 540;

  const [resolutionInfo, setResolutionInfo] = useState()

  useEffect(() => {

    //explicitly put in useEffect() because all players' options should be the same through out
    const videoJSOptions = {
      controlBar: {
        fullscreenToggle: false,
        volumePanel: false
      },
      controls: true,
      preload: 'auto',
      //userActions: { hotkeys: true },
      width: videoWidth,
      height: videoHeight,
      playbackRates: [0.25, 0.5, 0.75, 1],
      sources: [{
        src: source, //blob source
        type: 'video/mp4'
      }],
      plugins: {
        hotkeys: {
          seekStep: 1/fps
        }
      }
    };

    //register videojs plugins
    videojs.registerPlugin('overlay', overlay) //for later use in VideoCanvas component
    videojs.registerPlugin('seekButtons', seekButtons)

    //creating videojs player
    const player = videojs(videoPlayerRef.current, videoJSOptions, function playerReady() {

      this.playbackRate(0.5);
      //when video is loaded, set intrinsic width and height
      player.on('loadedmetadata', function () {
        setResolutionInfo({
          intrinsicVideoWidth: this.videoWidth(),
          intrinsicVideoHeight: this.videoHeight(),
          videoWidth: videoWidth,
          videoHeight: videoHeight
        })
      })

    });

    
    player.seekButtons({
      forward: 1 / fps,
      back: 1 / fps
    });

    //hide the video and display the canvas only
    videoPlayerRef.current.style.display = 'none'

    return () => { if (player) player.dispose() };
    // eslint-disable-next-line
  }, []);

  return (
    <div>
      <div style={{ display: 'flex', width: '90%', height: '540px', left: '7%', position: 'absolute' }}>
        <div data-vjs-player>
          <video
            ref={videoPlayerRef}
            className="video-js vjs-theme-forest"
          />
        </div>
        {resolutionInfo &&
          <InteractiveDisplay
            videoPlayerRef={videoPlayerRef}
            modifiableCsv={modifiableCsv}
            fps={fps}
            resolutionInfo={resolutionInfo}
            classData={classData}
            persistentCsv={persistentCsv}
            token={token}
            account={account}
          />
        }

      </div>
      
      <div style={{ position: 'relative', top: '600px', left: '0.01%', margin: 'auto' }}>
        <PredictionDisplay
          data={persistentCsv}
          videoPlayerRef={videoPlayerRef}
          fps={fps}
          tableName='Model Predictions'
        />
      </div>

    </div>
  );
};