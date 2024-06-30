import { Link } from "react-router-dom"
import useGetToken from '../../util/useGetToken'
import { protectedResources } from '../../authConfig'
import ConfirmAction from '../core/ConfirmAction'
import { callApiWithToken } from '../../util/useFetch'
import { FaTrashAlt } from 'react-icons/fa'
import { useState } from 'react'

/**
 * Create a component which displays the basic users
 * @param users List of users
 * @param filterText Text to filter the list
 * @returns Component which displays the preview of all the projects
 */
const UserList = ({ users, setUsers, filterText }) => {

    let id = 0;
    const endpointDelete = protectedResources.apiDeleteUser.endpoint;
    const scopes = protectedResources.scopes;

    const [confirmModalIsOpen, setConfirmModalIsOpen] = useState(false)
    const [func, setFunc] = useState(false)

    //Authentication
    const { account, token } = useGetToken(scopes)

    async function deleteUser(user) {
        let data = null
        await setUsers(users.filter(x => x !== user))
        await callApiWithToken(token, endpointDelete, account, "json", "POST", JSON.stringify({ id: user.id }),
            new Headers({ 'content-type': 'application/json' })).then(response => { data = response });
    }

    return (
        <div className="project-list">
            <ConfirmAction modalIsOpen={confirmModalIsOpen} setModalIsOpen={setConfirmModalIsOpen} onConfirm={func} />
            {users.filter((user) => user.username.includes(filterText) || user.email.includes(filterText)).map((user) => (
                <div className="project-preview" key={id++}>
                    <div style={{ position: 'relative' }}>
                        {/*Map each user to its title and the description and 
                       link it to the corresponding projectHolder*/}
                        <Link to={"/project-holder/" + btoa(user.id)}>
                            <h5 style={{ overflow: 'hidden' }}>
                                {user.username}
                            </h5>
                            <h6 style={{ overflow: 'hidden' }}>
                                {user.email}
                            </h6>
                        </Link>
                        <FaTrashAlt style={{ position: 'absolute', right: '3%', top: '50%', width: 25, height: 25, cursor: 'pointer' }}
                                onClick={() => {
                                    setFunc(() => () => deleteUser(user));
                                    setConfirmModalIsOpen(true);}} 
                        
                        />
                    </div>
                    <br />
                </div>
            ))
            }
        </div>
    )
}

export default UserList
