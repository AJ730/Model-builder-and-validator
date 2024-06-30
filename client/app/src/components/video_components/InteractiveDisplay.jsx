import { useState, useEffect } from "react";
import InteractiveCanvas from "./InteractiveCanvas";
import DisplayCanvas from "./DisplayCanvas";
import Sidebar from "./Sidebar";
import videojs from "video.js";

const InteractiveDisplay = ({ videoPlayerRef, modifiableCsv, persistentCsv, fps, resolutionInfo, token, account, classData }) => {
    const VIDEO_INTRINSIC_WIDTH = resolutionInfo.intrinsicVideoWidth;
    const VIDEO_INTRINSIC_HEIGHT = resolutionInfo.intrinsicVideoHeight;
    const CANVAS_WIDTH = resolutionInfo.videoWidth;
    const CANVAS_HEIGHT = resolutionInfo.videoHeight;

    const modifiableCsvMapping = new Map(modifiableCsv.map(obj => [obj.frameNum, []]));
    modifiableCsv.forEach(x => {
        const scaledEntry = {
            ...x,
            trackerL: x.trackerL * CANVAS_WIDTH / VIDEO_INTRINSIC_WIDTH,
            trackerT: x.trackerT * CANVAS_HEIGHT / VIDEO_INTRINSIC_HEIGHT,
            trackerW: x.trackerW * CANVAS_WIDTH / VIDEO_INTRINSIC_WIDTH,
            trackerH: x.trackerH * CANVAS_HEIGHT / VIDEO_INTRINSIC_HEIGHT
        }
        modifiableCsvMapping.get(x.frameNum).push(scaledEntry); 
    });
    const [modifiableCsvMap, setModifiableCsvMap] = useState(modifiableCsvMapping)
    const [currentFrame, setCurrentFrame] = useState(0);
    const [deletedObjects, setDeletedObjects] = useState([]);
    useEffect(() => {
        let id;

        const player = videojs(videoPlayerRef.current);
        player.overlay({
            content: document.getElementById('canvases') //overlaying the canvas on top of video, taking from VideoCanvas
        })

        const video = videoPlayerRef.current;
        const updateFrame = (now, metadata) => {

            // mediaTime is what you should use if you want to precisely grab frames in a reproducible way
            const currentFrame = Math.round(metadata.mediaTime * fps);
            setCurrentFrame(currentFrame);

            id = video.requestVideoFrameCallback(updateFrame);
        };

        // using requestVideoFrameCallback() as a best effort to get the correct frame in video playback
        // is called when a new frame is painted on the screen
        // https://web.dev/requestvideoframecallback-rvfc/
        id = video.requestVideoFrameCallback(updateFrame);
        return () => video.cancelVideoFrameCallback(id);
    }, [])

    return (
        <div>
            {/* {console.log(deletedObjects)} */}
            <div id='canvases' style={{ position: 'relative' }}>

                <DisplayCanvas
                    videoPlayerRef={videoPlayerRef}
                    resolutionInfo={resolutionInfo}>
                </DisplayCanvas>

                <InteractiveCanvas
                    resolutionInfo={resolutionInfo}
                    modifiableCsvMap={modifiableCsvMap}
                    setModifiableCsvMap={setModifiableCsvMap}
                    persistentCsv={persistentCsv}
                    deletedObjects={deletedObjects}
                    setDeletedObjects={setDeletedObjects}
                    videoPlayerRef={videoPlayerRef}
                    modifiableCsv={modifiableCsv}
                    fps={fps}>
                </InteractiveCanvas>
            </div>

            <Sidebar
                resolutionInfo={resolutionInfo}
                videoPlayerRef={videoPlayerRef}
                fps={fps}
                classData={classData} 
                deletedObjects={deletedObjects}
                setDeletedObjects={setDeletedObjects}
                token={token} 
                account={account}
                modelInfo={persistentCsv.map(x => Object.assign({}, 
                    {
                        ...x,
                        trackerL: x.trackerL * CANVAS_WIDTH / VIDEO_INTRINSIC_WIDTH,
                        trackerT: x.trackerT * CANVAS_HEIGHT / VIDEO_INTRINSIC_HEIGHT,
                        trackerW: x.trackerW * CANVAS_WIDTH / VIDEO_INTRINSIC_WIDTH,
                        trackerH: x.trackerH * CANVAS_HEIGHT / VIDEO_INTRINSIC_HEIGHT
                    }) 
                )}
                modifiableCsvMap={modifiableCsvMap}
                setModifiableCsvMap={setModifiableCsvMap}
                currentFrame={currentFrame}>
            </Sidebar>

        </div>
    );
}

export default InteractiveDisplay;