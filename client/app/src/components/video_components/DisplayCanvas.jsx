import { useRef, useEffect } from "react";

const DisplayCanvas = ({ videoPlayerRef, resolutionInfo }) => {

    const CANVAS_WIDTH = resolutionInfo.videoWidth;
    const CANVAS_HEIGHT = resolutionInfo.videoHeight;

    const videoCanvasRef = useRef(null);    

    useEffect(() => {
        videoCanvasRef.current.width = CANVAS_WIDTH;
        videoCanvasRef.current.height = CANVAS_HEIGHT;
    }, [])

    // start drawing video frame onto the canvas when DOM is loaded
    useEffect(() => {
        const video = videoPlayerRef.current;
        const videoCanvas = videoCanvasRef.current;
        const ctx = videoCanvas.getContext("2d");

        const updateVideoCanvas = (now, metadata) => {
            // draw current frame to canvas
            ctx.drawImage(video, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);     
            video.requestVideoFrameCallback(updateVideoCanvas);
        };

        video.requestVideoFrameCallback(updateVideoCanvas);
    }, []);

    return (
        <canvas style={{ position: 'absolute', zIndex: '1', left: '0px', top: '0px' }} ref={videoCanvasRef}></canvas>
    );
}

export default DisplayCanvas;