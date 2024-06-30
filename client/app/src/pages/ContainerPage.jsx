import { protectedResources } from "../authConfig";
import { VideoPlayer } from "../components/video_components/VideoPlayer"
import { useEffect, useState } from 'react'
import { useParams } from "react-router-dom"
import useGetToken from '../util/useGetToken'
import { callApiWithToken } from '../util/useFetch'
import CircularProgress from '@material-ui/core/CircularProgress';


/**
 * 
 * @returns Create a simple homepage
 */
const ContainerPage = () => {
    const scopes = protectedResources.scopes
    const endpointCsv = protectedResources.apiGetCsv.endpoint
    const endpointPersistentCsv = protectedResources.apiGetPersistentCsv.endpoint
    const endpointVideo = protectedResources.apiGetVideo.endpoint
    const endpointClasses = protectedResources.apiGetClasses.endpoint
    const endpointContainer = protectedResources.apiGetContainer.endpoint

    const [videoData, setVideoData] = useState(null)
    const [csvData, setCsvData] = useState(null)
    const [persistentCsvData, setPersistentCsvData] = useState(null)
    const [classData, setClassData] = useState(null)
    const [fps, setFps] = useState(null)

    const { token, account } = useGetToken(scopes)

    const compareObjectId = ((firstEl, secondEl) => {
        return firstEl.objectId - secondEl.objectId;
    })

    //params.id to get id
    let params = useParams()

    //When the token is not null start making calls to the api
    useEffect(() => {
        let videoData = null
        let videoLink = null
        let csvData = null
        let classData = null
        let fps = null
        let persistentCsvData = null
        async function fetchData() {
            //Get the projects using the userId
            if (token) {

                await callApiWithToken(token, endpointCsv, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { csvData = response });

                await callApiWithToken(token, endpointPersistentCsv, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { persistentCsvData = response });

                // Get sasLink from the server, then fetch and save the video as a blob to the browser
                await callApiWithToken(token, endpointVideo, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { videoLink = response.sasLink; });
                await fetch(videoLink, {
                        headers: new Headers({
                            "pragma": "no-cache",
                            "cache-control": "no-store"
                        })
                    })
                    .then(response => response.blob())
                    .then(blob => URL.createObjectURL(blob))
                    .then(url => {videoData = url})
                

                await callApiWithToken(token, endpointClasses, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { classData = response; });

                await callApiWithToken(token, endpointContainer, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { fps = response.frameRate; });

                setVideoData(videoData)
                setCsvData(csvData.sort(compareObjectId))
                setPersistentCsvData(persistentCsvData.sort(compareObjectId))
                setClassData(classData.concat("false_detection").concat("unlabelled"))
                setFps(fps)
            }
        }
        fetchData();
    }, [token]);
    
    return (
        <div className="home">
            { (videoData && csvData && persistentCsvData && classData && fps) ? 
                <VideoPlayer source={videoData} modifiableCsv={csvData} persistentCsv={persistentCsvData} 
                    classData={classData} token={token} 
                    account={account} fps={fps} 
                /> 
            : <CircularProgress style={{ position: 'fixed', top: '50%' }} />}
        </div>
    );
}

export default ContainerPage;