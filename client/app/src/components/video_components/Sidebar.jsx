import { useState, useEffect, useRef } from "react";
import Modal from 'react-modal'
import SelectSearch from 'react-select-search/dist/cjs';
import 'react-select-search/style.css';
import cloneDeep from 'lodash.clonedeep'
import fuzzySearch from 'react-select-search/dist/cjs/fuzzySearch'
import displayMetrics from "../../util/metrics";
import { protectedResources } from '../../authConfig'
import { callApiWithToken } from '../../util/useFetch'
import MuiAlert from '@material-ui/lab/Alert';
import Snackbar from '@material-ui/core/Snackbar';
import { useParams } from "react-router-dom"
import ExportMetrics from '../core/ExportMetrics'
import { FaTrashAlt } from 'react-icons/fa'

function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const compareObjectFrame = ((firstEl, secondEl) => {
    return firstEl.frameNum - secondEl.frameNum;
})

const Sidebar = ({ videoPlayerRef, fps, modelInfo, modifiableCsvMap, setModifiableCsvMap,
    currentFrame, token, account, classData, deletedObjects, setDeletedObjects, resolutionInfo }) => {

    const VIDEO_INTRINSIC_WIDTH = resolutionInfo.intrinsicVideoWidth;
    const VIDEO_INTRINSIC_HEIGHT = resolutionInfo.intrinsicVideoHeight;
    const CANVAS_WIDTH = resolutionInfo.videoWidth;
    const CANVAS_HEIGHT = resolutionInfo.videoHeight;

    const classes = classData.map(str => ({ value: str, name: str }))

    const modifiableCsvMapRef = useRef(modifiableCsvMap)
    const deletedObjectsRef = useRef(deletedObjects)

    let params = useParams()
    let key = 0;
    const [errorOpen, setErrorOpen] = useState(false)
    const [saveOpen, setSaveOpen] = useState(false)
    const [autoErrorOpen, setAutoErrorOpen] = useState(false)
    const [successOpen, setSuccessOpen] = useState(false)
    const [tipOpen, setTipOpen] = useState(false)
    const [reminderOpen, setReminderOpen] = useState(false)
    const endpointSubmit = protectedResources.apiSubmit.endpoint
    const endpointDelete = protectedResources.apiDeleteRecords.endpoint

    const [labels, setLabels] = useState([])
    const [correctedModelInfo, setCorrectedModelInfo] = useState(Array.from(modifiableCsvMap.values()).flat());

    const lastPersistentCsvId = modelInfo[modelInfo.length - 1].objectId

    const seekRelevantFrame = () => {
        Array.from(modifiableCsvMap.values()).flat().sort(compareObjectFrame).some((x, index) => {
            // Do your thing, then:
            console.log(index)
            if (x.frameNum > currentFrame) {
                videoPlayerRef.current.currentTime = x.frameNum / fps + 0.0001
                return true
            }
            else return false
        })
    }

    function deleteObject(obj) {
        setDeletedObjects(deletedObjects.concat(obj))
        const mapClone = cloneDeep(modifiableCsvMap)
        mapClone.set(currentFrame, mapClone.get(currentFrame).filter(x => x.objectId !== obj.objectId))
        setModifiableCsvMap(mapClone)
    }

    async function submit(manual) {
        let data = null

        const arr = Array.from(modifiableCsvMapRef.current.values()).flat()
        setCorrectedModelInfo(arr)
        if (arr.filter(x => x.label === 'unlabelled').length > 0) {
            if (manual) {
                setReminderOpen(true);
            }
            else setAutoErrorOpen(true)
        } else {
            setCorrectedModelInfo(Array.from(modifiableCsvMapRef.current.values()).flat())
            await callApiWithToken(token, endpointSubmit, account, "", "POST", JSON.stringify({
                containerId: params.id,
                recordDtos: Array.from(modifiableCsvMapRef.current.values()).flat().filter(x => x.modified === true).map(x => {
                    const record = {
                        ...x,
                        trackerL: Math.round(x.trackerL * VIDEO_INTRINSIC_WIDTH / CANVAS_WIDTH),
                        trackerT: Math.round(x.trackerT * VIDEO_INTRINSIC_HEIGHT / CANVAS_HEIGHT),
                        trackerW: Math.round(x.trackerW * VIDEO_INTRINSIC_WIDTH / CANVAS_WIDTH),
                        trackerH: Math.round(x.trackerH * VIDEO_INTRINSIC_HEIGHT / CANVAS_HEIGHT),
                        modified: undefined
                    };
                    x.modified = false;
                    //const { modified, ...newObj } = x;
                    return record;
                })
            }), new Headers({ 'content-type': 'application/json' }))
                .then(response => { data = response });

            await callApiWithToken(token, endpointDelete, account, "", "POST", JSON.stringify({
                recordDtos: deletedObjectsRef.current.map(x => { const { modified, ...newObj } = x; return newObj })
            }), new Headers({ 'content-type': 'application/json' }))
                .then(response => { data = response });


            if (data && data.status === 500) setErrorOpen(true)
            else {
                const newMap = cloneDeep(modifiableCsvMapRef.current)
                newMap.forEach((value) => {
                    value.forEach(x => x.modified = false)
                })
                setModifiableCsvMap(newMap);
                setSuccessOpen(true);
                setDeletedObjects([]); return
            }

        }

    }

    //Auto-save functionality
    const TIMEOUT_MS = 180000 // 3 minutes timeout
    useEffect(() => {
        const interval = setInterval(() => { submit(false); setSaveOpen(true) }, TIMEOUT_MS)

        return () => clearInterval(interval)
    }, [])
    useEffect(() => {
        modifiableCsvMapRef.current = modifiableCsvMap
        deletedObjectsRef.current = deletedObjects
    }
        , [modifiableCsvMap, deletedObjects])

    useEffect(() => {
        /**
         * Update model info after each change in <select>.
         * @param {*} selectedObjectId objectId of the object being selected.
         * @param {*} selectedLabel label of the object being selected.
         */
        const updateModelOutputLabels = (selectedObjectId, selectedLabel) => {
            const modifiableCsvMapRef = cloneDeep(modifiableCsvMap)
            modifiableCsvMapRef.set(currentFrame, modifiableCsvMapRef.get(currentFrame)
                .map(obj => obj.objectId === selectedObjectId ? { ...obj, label: selectedLabel, modified: true } : obj)
            )
            setModifiableCsvMap(modifiableCsvMapRef)
        }

        const currentLabels = modifiableCsvMap.get(currentFrame)

        let currentLabelsFormat = []
        //if there are labels appearing on the screen
        if (currentLabels) {
            currentLabelsFormat = currentLabels.map(obj => <div key={key++} >Object ID: {obj.objectId} - Label:
                {obj.objectId > lastPersistentCsvId ?
                    <FaTrashAlt style={{ position: "relative", bottom: 2 , left: "13%" , width: 15, height: 15, cursor: 'pointer' }}
                        onClick={() => { deleteObject(obj) }}
                    /> : null
                }
                <SelectSearch
                    className='select-search'
                    options={classes}
                    value={obj.label}
                    search
                    filterOptions={fuzzySearch}
                    placeholder='Please select a label'
                    onChange={selectedOption => updateModelOutputLabels(obj.objectId, selectedOption)}
                />
                
            </div>)
        }

        setLabels(currentLabelsFormat)
    }, [currentFrame, modifiableCsvMap])

    return (
        <div>
            <Modal style={{
                overlay: {
                    position: 'absolute', top: '12%', left: '8.5%', width: '60%', height: '60%', zIndex: 5,
                    backgroundColor: 'rgba(255, 255, 255, 0)'
                }
            }} isOpen={reminderOpen}>
                <h6>You have unlabelled boxes in the following frames. Please label them before saving:</h6>
                <p>{Array.from(modifiableCsvMapRef.current.values()).flat().filter(x => x.label === 'unlabelled').map(x => x.frameNum).join(', ')}</p>
                <button style={{ position: 'absolute', right: '2%', bottom: '0%', width: '125px', backgroundColor: 'firebrick' }} type='button'
                    className='popbtn cnclbtn' onClick={() => setReminderOpen(false)}>
                    OK
                </button>
            </Modal>
            <Modal style={{
                overlay: {
                    position: 'absolute', top: '12%', left: '8.5%', width: '60%', height: '60%', zIndex: 5,
                    backgroundColor: 'rgba(255, 255, 255, 0)'
                }
            }} isOpen={tipOpen}>
                <h4>TIPS:</h4>
                <h6>Basic</h6>
                <ul>
                    <li>To navigate frame by frame, use either the arrow icons on the video player
                    or click anywhere on the video to use the arrow keys
                    </li>
                    <li>To change the labels of boxes, use the dropdown menu on the sidebar.</li>
                    <li>If the model falsely detected an object, it should be reclassified as "false_detection".</li>
                    <li>To calculate and display the metrics click on Calculate Metrics.</li>
                    <li>All items must be labelled before saving.</li>
                    <li>To see the changes when exporting the metrics, make sure to save first.</li>
                    <li>Use the table below the video to navigate quickly by clicking on any row.</li>
                </ul>

                <h6>Advanced</h6>
                <ul>
                    <li>Clicking on the video and dragging creates a box.</li>
                    <li>Clicking on the box and dragging allows for repositioning.</li>
                    <li>Clicking on the box and dragging the corners allows for resizing.</li>
                    <li>Boxes created by the user can be deleted by clicking the X icon.</li>
                    <li>Newly added boxes will have a green color and modified boxes for the current state will have a blue color.</li>
                    <li>Red color is used to show the original boxes drawn by the model.</li>
                </ul>

                <button style={{ position: 'relative', left: '80%', bottom: '0%', width: '125px', backgroundColor: 'firebrick' }} type='button'
                    className='popbtn cnclbtn' onClick={() => setTipOpen(false)}>
                    Close
                </button>
            </Modal>
            <Snackbar style={{ position: 'fixed', width: '500px', bottom: '88%', left: '90%' }} open={errorOpen} autoHideDuration={6000} onClose={() => setErrorOpen(false)}>
                <Alert onClose={() => setErrorOpen(false)} severity="error">
                    Something went wrong!
                </Alert>
            </Snackbar>
            <Snackbar style={{ position: 'fixed', width: '500px', bottom: '88%', left: '90%' }} open={successOpen} autoHideDuration={6000} onClose={() => setSuccessOpen(false)}>
                <Alert onClose={() => setSuccessOpen(false)} severity="success">
                    Success!
                </Alert>
            </Snackbar>
            <Snackbar style={{ position: 'fixed', width: '500px', bottom: '88%', left: '90%' }} open={saveOpen} autoHideDuration={6000} onClose={() => setSaveOpen(false)}>
                <Alert onClose={() => setSaveOpen(false)} severity="success">
                    Auto-Saved.
                </Alert>
            </Snackbar>
            <Snackbar style={{ position: 'fixed', width: '500px', bottom: '88%', left: '90%' }} open={autoErrorOpen} autoHideDuration={6000}
                onClose={() => setAutoErrorOpen(false)}>
                <Alert onClose={() => setAutoErrorOpen(false)} severity="error">
                    Auto-Saved failed because of unlabelled boxes!
                </Alert>
            </Snackbar>
            <div style={{ borderStyle: 'solid', borderWidth: '2px', borderColor: 'black', background: 'whitesmoke' }}>
                <header style={{ display: 'flex', borderBottom: '1px solid black' }}>
                    <h4 style={{ margin: 'auto' }}> Objects </h4>
                    <button className='apbtn' onClick={() => {
                        const correctedModelInfo = Array.from(modifiableCsvMap.values()).flat();
                        displayMetrics(classes.map(x => x.value), modelInfo, correctedModelInfo);
                    }}>
                        Calculate Metrics
                    </button>
                </header>
                <div style={{
                    flex: '1 1 auto', height: '483px', minWidth: '325px', overflowY: 'auto'
                }}>
                    {labels}
                </div>
            </div>
            {/* The two buttons used for creating and canceling the form.*/}
            <div style={{ display: 'flex', position: 'absolute', left: '0%', width: '1290px', height: '50px' }}>
                <button style={{ position: 'absolute', width: '150px', right: '13%' }} type='button' className='apbtn' onClick={() => submit(true)}>
                    Save
                </button>
                <button style={{ width: '150px' }} type='button' className='apbtn' onClick={() => seekRelevantFrame()}>
                    Next Object
                </button>
                <button style={{ position: 'absolute', width: '150px', left: '13%' }} type='button' className='apbtn' onClick={() => setTipOpen(true)}>
                    Help
                </button>
                <ExportMetrics
                    classLabels={classes.map(x => x.value)}
                    modelPredictions={modelInfo}
                    actualLabels={correctedModelInfo}
                ></ExportMetrics>
            </div>
        </div>

    );
}

export default Sidebar;