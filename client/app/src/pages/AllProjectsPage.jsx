import { protectedResources } from "../authConfig";
import ProjectList from "../components/core/ProjectList";
import useFetch from "../util/useFetch";
import APButton from "../components/core/APButton"
import AddProjectForm from "../components/admin_components/AddProjectForm"
import { useState, useEffect } from 'react'
import CircularProgress from '@material-ui/core/CircularProgress';
import MuiAlert from '@material-ui/lab/Alert';
import Snackbar from '@material-ui/core/Snackbar';
import TextField from '@material-ui/core/TextField';

function Alert(props) {
    return <MuiAlert elevation={6} variant="filled" {...props} />;
}

/**
 * Create a component to display all the projects by obtaining a token and calling the protected endpoint.
 * @returns Component to display all the projects
 */
export const AllProjectsPage = () => {
    const [modalIsOpen, setModalIsOpen] = useState(false)
    const [errorOpen, setErrorOpen] = useState(false)
    const [successOpen, setSuccessOpen] = useState(false)
    const [projectData, setProjectData] = useState(false)
    const [userData, setUserData] = useState(false)
    const [blobList, setBlobList] = useState(false)
    const [filterText, setFilterText] = useState('')

    const scopes = protectedResources.scopes
    const endpointProject = protectedResources.apiGetProjects.endpoint
    const endpointUser = protectedResources.apiGetAllUsers.endpoint
    const endpointBlobList = protectedResources.apiListBlobs.endpoint

    const pData = useFetch(scopes, endpointProject, "json", "POST")
    const uData = useFetch(scopes, endpointUser, "json", "POST")
    const blobsData = useFetch(scopes, endpointBlobList, "json", "POST")

    useEffect(() => {
        if (pData) setProjectData(pData)
        if (uData) setUserData(uData)
        if (blobsData) setBlobList(blobsData.blobs.map(blobName => ({ value:blobName, name: blobName })))
    }, [pData, uData, blobsData])


    return (
        <div className="all-projects">
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
            <header className="project-header">
                <h1>All Projects</h1>
                <TextField value={filterText} onChange={(str) => setFilterText(str.target.value)} style={{ margin: 'auto', width: '60%' }} label="Search" variant="filled" />
                {/*Creates the button to access the pop-up*/}
                {<APButton clr="green" str="Add New Project" onClick={() => setModalIsOpen(true)} />}
            </header>
            <div className="all-projects-content">
                {/*Creates the popup for creating projects*/}
                {(userData && blobList) ? <AddProjectForm modalIsOpen={modalIsOpen} setModalIsOpen={setModalIsOpen} setErrorOpen={setErrorOpen}
                    setSuccessOpen={setSuccessOpen} projects={projectData} setProjects={setProjectData} userData={userData} blobList = {blobList}/> : null}

                {(projectData && userData && blobList) ? (projectData.length > 0 ? <ProjectList projects={projectData} setProjects={setProjectData} filterText={filterText}
                    setErrorOpen={setErrorOpen} setSuccessOpen={setSuccessOpen} isAdmin={true} userData={userData} blobList = {blobList}/>
                    : <h3 className="no-projects">No Projects Found</h3>) : <CircularProgress style={{ position: 'fixed', top: '50%' }} />}
            </div>
        </div>
    );
};


