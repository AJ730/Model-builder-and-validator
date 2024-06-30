import { protectedResources } from "../authConfig";
import ProjectList from "../components/core/ProjectList";
import { useState, useEffect } from 'react'
import { useParams } from "react-router-dom"
import useGetToken from '../util/useGetToken'
import { callApiWithToken } from '../util/useFetch'
import CircularProgress from '@material-ui/core/CircularProgress';
import TextField from '@material-ui/core/TextField';

/**
 * Create a component to display all the projects contained in a projectHolder by obtaining a token and calling the protected endpoint.
 * @returns Component to display all the projects accessible to the client
 */
const ProjectHolderPage = () => {
    const scopes = protectedResources.scopes;
    const endpoint = protectedResources.apiGetProjectsFromHolderUser.endpoint;
    const [filterText, setFilterText] = useState('')
    const [data, setData] = useState(null)
    const { token, account } = useGetToken(scopes)

    let params = useParams()

    //When the token is not null start making calls to the api
    useEffect(() => {
        let data = null
        async function fetchData() {
            //Decrypt using atob()
            //Get the projects using the userId
            if (token) {
                await callApiWithToken(token, endpoint, account, "json", "POST", JSON.stringify({ id: atob(params.id) }), new Headers({ 'content-type': 'application/json' }))
                    .then(response => { data = response });
            }
            setData(data)
        }
        fetchData();
    }, [token]);


    return (
        <div className="all-projects">
            <header className="project-header">
                <h1>Projects</h1>
                <TextField value={filterText} onChange={(str) => setFilterText(str.target.value)} style={{ margin: 'auto', width: '60%' }} label="Search" variant="filled" />
            </header>
            <div className="all-projects-content">
                {data ? (data.length > 0 ? <ProjectList projects={data} filterText={filterText} isAdmin={false} />
                    : <h3 className="no-projects">No Projects Found</h3>) : <CircularProgress style={{ position: 'fixed', top: '50%' }} />}
            </div>
        </div>
    );
};

export default ProjectHolderPage
