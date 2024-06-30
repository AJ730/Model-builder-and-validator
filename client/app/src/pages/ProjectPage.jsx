import { useParams } from "react-router-dom"
import { Link } from "react-router-dom"
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';
import { useState, useEffect } from 'react'
import CircularProgress from '@material-ui/core/CircularProgress';
import { protectedResources } from '../authConfig';
import useGetToken from '../util/useGetToken'
import { callApiWithToken } from '../util/useFetch'

/**
 * Create a component to display a projects details by obtaining a token and calling the protected endpoint.
 * @returns Component to display a project with its details
 */
const ProjectPage = () => {
    const [projectData, setProjectData] = useState(null)
    const [containerData, setContainerData] = useState(null)

    const endpointGetContainers = protectedResources.apiProjectContainers.endpoint
    const endpointGetProject = protectedResources.apiGetProject.endpoint
    const scopes = protectedResources.scopes

    //Access the id by doing params.id
    let params = useParams()
    let id = 0;

    //Authentication
    const { token, account } = useGetToken(scopes)

    //When the token is not null start making calls to the api
    useEffect(() => {
        let projectData = null
        let containerData = null
        async function fetchData() {
            if (token) {
                await callApiWithToken(token, endpointGetProject, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { projectData = response });

                await callApiWithToken(token, endpointGetContainers, account, "json", "POST", JSON.stringify({ id: params.id }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { containerData = response });
                setProjectData(projectData)
                setContainerData(containerData)
            }
        }
        fetchData()
    }, [token])

    //This creates a custom tooltip with the given styles.
    const CstmTooltip = withStyles((theme) => ({
        tooltip: {
            backgroundColor: '#f5f5f9',
            color: 'rgba(0, 0, 0, 0.87)',
            maxWidth: 500,
            fontSize: theme.typography.pxToRem(12),
            border: '1px solid #dadde9',
        },
    }))(Tooltip);

    return (
        //Show a loading icon if any of the following are null
        (projectData && containerData) ?
            <div>
                <h2>Project Title</h2>
                <div className='show-project'>
                    <h3 className='show-project-content'>{projectData.title}</h3>
                </div>
                <h2>Project Description</h2>
                <div className='show-project' style={{ height: '150px' }}>
                    <h3 className='show-project-content'>{projectData.description}</h3>
                </div>
                <h2>Project Containers</h2>
                <div style={{ overflow: 'auto', height: '180px' }}>
                    {containerData.map((container) => (
                        <div key={id++}>
                            <CstmTooltip
                                title={<div>
                                    <h6>Video: {container.blobName}</h6>
                                    <h6>CSV: {container.csvName}</h6>
                                    <h6>Classes: {container.className}</h6>
                                </div>} arrow placement="top">
                                <h5>
                                    <Link to={"/container/" + container.id}>
                                        {container.name}
                                    </Link>
                                </h5>
                            </CstmTooltip>
                        </div>
                    ))}
                </div>
            </div>
            : <CircularProgress style={{ position: 'fixed', top: '50%' }} />
    )
}

export default ProjectPage
