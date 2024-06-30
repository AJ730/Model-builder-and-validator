import { protectedResources } from "../authConfig";
import { useState,useEffect } from 'react'
import UserList from "../components/admin_components/UserList";
import useFetch from "../util/useFetch";
import CircularProgress from '@material-ui/core/CircularProgress';
import TextField from '@material-ui/core/TextField';

/**
 * Create a component to display all the users by calling the protected endpoint.
 * @returns Component to display all the users
 */
const AllUsersPage = () => {
    const scopes = protectedResources.scopes;
    const endpoint = protectedResources.apiGetAllUsers.endpoint;
    const [filterText, setFilterText] = useState('')
    const [users, setUsers] = useState(null)

    const userData = useFetch(scopes, endpoint, "json", "POST")

    useEffect(() => {
        if(userData) setUsers(userData)
    },[userData])

    return (
        <div className="all-projects">
            <header className="project-header">
                <h1>Users</h1>
                <TextField value={filterText} onChange={(str) => setFilterText(str.target.value)} style={{ margin: 'auto', width: '60%' }} label="Search" variant="filled" />
            </header>
            <div className="all-projects-content">
                {users ? (users.length > 0 ? <UserList users={users} setUsers={setUsers} filterText={filterText} /> : <h3 className="no-projects">No Users Found</h3>)
                    : <CircularProgress style={{ position: 'fixed', top: '50%' }} />}
            </div>
        </div>
    );
}

export default AllUsersPage
