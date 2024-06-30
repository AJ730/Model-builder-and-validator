import { fabric } from "fabric";
import cloneDeep from "lodash.clonedeep";
import { useRef, useEffect } from "react";

const InteractiveCanvas = ({ resolutionInfo, modifiableCsvMap, setModifiableCsvMap, 
    deletedObjects, setDeletedObjects, persistentCsv, fps, videoPlayerRef, modifiableCsv }) => {

    const CANVAS_WIDTH = resolutionInfo.videoWidth;
    const CANVAS_HEIGHT = resolutionInfo.videoHeight;
    const MIN_BOX_WIDTH = 50;
    const MIN_BOX_HEIGHT = 50;

    //info of last entry in model output csv
    let lastObjectId = modifiableCsv[modifiableCsv.length - 1].objectId;
    const csvId = modifiableCsv[modifiableCsv.length - 1].csvId;
    const lastPersistentCsvId = persistentCsv[persistentCsv.length - 1].objectId;

    const drawCanvasRef = useRef(null);
    const fabricCanvas = useRef()
    const modifiableCsvMapRef = useRef(modifiableCsvMap)

    const deletedObjectsRef = useRef(deletedObjects)

    let frame = useRef(0);

    useEffect(() => {
        deletedObjectsRef.current = cloneDeep(deletedObjects)
    }, [deletedObjects])

    const isBetweenVideoResolution = (x, y) => {
        const clipped_x = Math.max(Math.min(x, CANVAS_WIDTH), 0);
        const clipped_y = Math.max(Math.min(y, CANVAS_HEIGHT), 0);
        return [clipped_x, clipped_y];
    }
    
    const getCoordsInfo = (coords) => {
        const tl = coords[0];
        const br = coords[2];

        const [tl_x, tl_y] = isBetweenVideoResolution(tl.x, tl.y);
        const [br_x, br_y] = isBetweenVideoResolution(br.x,br.y);

        const width = Math.abs(br_x - tl_x);
        const height = Math.abs(br_y - tl_y);

        return [tl_x, tl_y, width, height];
    }

    /**
     * Takes a box and extract necessary info.
     * @param {Object} group Reactangle object + Text object
     * @returns a corresponding model output entry
     */
    const createModelEntry = group => {
        const rect = group.getObjects()[0]
        const matrix = group.calcTransformMatrix()

        //calculating top left coordinate of rectangle in the group
        const rect_tl = fabric.util.transformPoint(rect.aCoords.tl, matrix);

        return {
            id: rect.id ? rect.id : undefined,
            frameNum: rect.frameNum,
            objectId: rect.objectId,
            label: rect.label,
            trackerL: rect_tl.x,
            trackerT: rect_tl.y,
            trackerW: rect.width * group.scaleX,
            trackerH:  rect.height * group.scaleY,
            modelConfidence: rect.modelConfidence,
            trackerConfidence: rect.trackerConfidence,
            csvId: rect.csvId,
            modified: true
        }
    }


    /**
     * Takes an entry created from createModelEntry and mutate the model output csv.
     * @param {*} entry CSV entry
     */
    const updateModelEntry = entry => {
        const currentBoxes = modifiableCsvMapRef.current.get(frame.current)

        if (currentBoxes) {
            const index = currentBoxes.findIndex(x => x.objectId === entry.objectId)

            if (index !== -1) currentBoxes[index] = entry
            else currentBoxes.push(entry)

            modifiableCsvMapRef.current.set(frame.current, currentBoxes)
        }
        else modifiableCsvMapRef.current.set(frame.current, [entry])

        setModifiableCsvMap(modifiableCsvMapRef.current)
    }


    var deleteIcon = "data:image/svg+xml,%3C%3Fxml version='1.0' encoding='utf-8'%3F%3E%3C!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' 'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'%3E%3Csvg version='1.1' id='Ebene_1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' x='0px' y='0px' width='595.275px' height='595.275px' viewBox='200 215 230 470' xml:space='preserve'%3E%3Ccircle style='fill:%23F44336;' cx='299.76' cy='439.067' r='218.516'/%3E%3Cg%3E%3Crect x='267.162' y='307.978' transform='matrix(0.7071 -0.7071 0.7071 0.7071 -222.6202 340.6915)' style='fill:white;' width='65.545' height='262.18'/%3E%3Crect x='266.988' y='308.153' transform='matrix(0.7071 0.7071 -0.7071 0.7071 398.3889 -83.3116)' style='fill:white;' width='65.544' height='262.179'/%3E%3C/g%3E%3C/svg%3E";
    var img = document.createElement('img');
    img.src = deleteIcon;

    function deleteObject(eventData, transform) {
        const target = transform.target;
        const objectId_to_remove = target.getObjects()[0].objectId;
        const deleteRecord = createModelEntry(target)
        setDeletedObjects(deletedObjectsRef.current.concat(deleteRecord))

        //delete box
        const currentBoxes = modifiableCsvMapRef.current.get(frame.current)
        const arr = currentBoxes.filter(x => x.objectId !== objectId_to_remove);
        modifiableCsvMapRef.current.set(frame.current, arr)
        setModifiableCsvMap(modifiableCsvMapRef.current)

        const canvas = target.canvas;
        canvas.remove(target);
        canvas.requestRenderAll();
    }

    function renderIcon(ctx, left, top, styleOverride, fabricObject) {
        var size = this.cornerSize;
        ctx.save();
        ctx.translate(left, top);
        ctx.rotate(fabric.util.degreesToRadians(fabricObject.angle));
        ctx.drawImage(img, -size / 2, -size / 2, size, size);
        ctx.restore();
    }

    const deleteControl = new fabric.Control({
        x: 0.5,
        y: -0.5,
        offsetX: 20,
        offsetY: -20,
        cursorStyle: 'pointer',
        mouseUpHandler: deleteObject,
        render: renderIcon,
        cornerSize: 15
    });

    const draw = (canvas, currentFrame) => {
        canvas.clear();
        const currentLabels = modifiableCsvMapRef.current.get(currentFrame);
        const frameText = new fabric.Text(`Frame: ${currentFrame}`, {
            left: 5,
            top: 5,
            fill: 'white',
            fontSize: 15,
            fontFamily: 'Quicksand',
            selectable: false,
            evented: false
        });
        canvas.add(frameText);
        //scanning each frame to draw boxes
        if (currentLabels) {
            currentLabels.forEach(x => {
                var rect = new fabric.Rect({
                    ...x,
                    left: x.trackerL,
                    top: x.trackerT,
                    width: x.trackerW,
                    height: x.trackerH,
                    fill: 'transparent',
                    stroke: x.objectId > lastPersistentCsvId ? 'mediumspringgreen' : x.modified ? 'blue' : 'red',
                    strokeWidth: 2,
                    strokeUniform: true,
                    hasBorders: false,
                    controls: {
                        ...fabric.Rect.prototype.controls,
                        deleteControl: new fabric.Control({ visible: false }) //disable rotation control
                    }
                });
                const text = new fabric.Text(`ID: ${x.objectId} Label: ${x.label}`, {
                    left: rect.left,
                    top: rect.top - 20,
                    fill: 'white',
                    fontSize: 15,
                    fontFamily: 'Quicksand'
                });
                const group = new fabric.Group([rect, text], {
                    controls: x.objectId > lastPersistentCsvId ? {
                        ...fabric.Rect.prototype.controls,
                        deleteControl: deleteControl //disable rotation control
                    } : fabric.Rect.prototype.controls
                }
                );
                canvas.add(group);
                group.addWithUpdate();
            })
            canvas.renderAll();
        }
    }

    useEffect(() => {
        drawCanvasRef.current.width = CANVAS_WIDTH;
        drawCanvasRef.current.height = CANVAS_HEIGHT;

        //initialize fabricJS
        fabricCanvas.current = new fabric.Canvas(drawCanvasRef.current);
        //disable rotation control
        fabric.Object.prototype.controls.mtr = new fabric.Control({ visible: false });
        
    }, [])

    useEffect(() => {
        modifiableCsvMapRef.current = cloneDeep(modifiableCsvMap)
        draw(fabricCanvas.current, frame.current)
    }, [modifiableCsvMap])

    useEffect(() => {
        let id;

        const canvas = fabricCanvas.current

        let rectangle;
        let isMouseDown, isBoxCreated, isBoxModified;
        let origX, origY; //storing original pointer position (i.e. top left coordinate of bounding box)

        canvas.on('mouse:down', function (event) {
            const pointer = canvas.getPointer(event.e);

            isMouseDown = true;
            isBoxCreated = false; //box is not created -> do box creation below

            origX = pointer.x;
            origY = pointer.y;

            rectangle = new fabric.Rect({
                left: origX,
                top: origY,
                fill: 'transparent',
                stroke: 'mediumspringgreen',
                strokeWidth: 2,
                strokeUniform: true,
                hasBorders: false
            });
        });

        canvas.on('mouse:move', function (event) {
            //if mouse is not pressed, do not draw!
            if (!isMouseDown) return;

            const pointer = canvas.getPointer(event.e);

            //if box is drawn outside of canvas -> limit coordinate
            if (CANVAS_WIDTH < pointer.x || pointer.x < 0 || CANVAS_HEIGHT < pointer.y || pointer.y < 0) return;
            if (origX > pointer.x) rectangle.set({ left: Math.abs(pointer.x) });
            if (origY > pointer.y) rectangle.set({ top: Math.abs(pointer.y) });

            rectangle.set({ width: Math.abs(origX - pointer.x) });
            rectangle.set({ height: Math.abs(origY - pointer.y) });
        });

        canvas.on('mouse:up', function (event) {
            if (!isBoxCreated) {
                const [tl_x, tl_y, width, height] = getCoordsInfo(rectangle.getCoords())

                //important if-check: remove all unimportant boxes (from mouse clicks, too small boxes,...)
                if (width >= MIN_BOX_WIDTH && height >= MIN_BOX_HEIGHT) {
                    //inputing necessary fields for creating an entry 
                    rectangle.set({
                        // id: ++last_id,
                        frameNum: frame.current,
                        objectId: ++lastObjectId,
                        csvId: csvId,
                        label: "unlabelled",
                        modified: true,
                        modelConfidence: 0,
                        trackerConfidence: 0
                    })
                    const text = new fabric.Text(`ID: ${rectangle.objectId}`, {
                        left: rectangle.left,
                        top: rectangle.top,
                        //backgroundColor: 'red',
                        fill: 'white',
                        fontSize: 15
                    });
                    var group = new fabric.Group([rectangle, text]);
                    //canvas.remove(rect);
                    canvas.add(group);
                    group.addWithUpdate();
                    //canvas.add(rectangle)
                    canvas.renderAll();

                    updateModelEntry(createModelEntry(group));
                }
            }
            else if (isBoxModified && event.target) {
                updateModelEntry(createModelEntry(event.target));
            }

            isMouseDown = false;
            isBoxCreated = true;
        });

        //modifying an object -> it's already created and being modified
        canvas.on({
            'object:moving': () => { isBoxCreated = true; isBoxModified = true; },
            'object:scaling': () => { isBoxCreated = true; isBoxModified = true; }
        });

        //NOT SURE ABOUT THIS, MAY CHANGE
        // canvas.on('selection:created', (e) => {
        //     if (e.target.type === 'activeSelection') {
        //         canvas.discardActiveObject();
        //     }
        // })

        const video = videoPlayerRef.current;

        const updateDrawCanvas = (now, metadata) => {

            frame.current = Math.round(metadata.mediaTime * fps);
            draw(canvas, frame.current)

            id = video.requestVideoFrameCallback(updateDrawCanvas);
        };

        id = video.requestVideoFrameCallback(updateDrawCanvas);
        return () => video.cancelVideoFrameCallback(id);
    }, []);

    return (
        <canvas style={{ position: 'absolute', zIndex: '2', left: '0px', top: '0px' }} id='canvas' ref={drawCanvasRef}></canvas>
    );
}

export default InteractiveCanvas;