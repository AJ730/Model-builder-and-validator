import { Link } from "react-router-dom"
import { FaEdit, FaTrashAlt } from 'react-icons/fa'
import { useState } from 'react'
import Menu from '@material-ui/core/Menu'
import MenuItem from '@material-ui/core/MenuItem'
import ChangeTitleForm from '../admin_components/ChangeTitleForm'
import ChangeDescForm from '../admin_components/ChangeDescForm'
import ChangeUserForm from '../admin_components/ChangeUserForm'
import ChangeContainerForm from '../admin_components/ChangeContainerForm'
import useGetToken from '../../util/useGetToken'
import { protectedResources } from '../../authConfig'
import ConfirmAction from './ConfirmAction'
import { callApiWithToken } from '../../util/useFetch'

/**
 * Create a component which displays the preview of all the projects
 * @param projects a list of projects 
 * @param setProjects function to locally show the new project
 * @param isAdmin boolean to check if the user is an admin
 * @param filterText Text to filter the list
 * @param setErrorOpen Function to make error messsage visible
 * @param setSuccessOpen Function to make success message visible
 * @returns Component which displays the preview of all the projects
 */
const ProjectList = ({ projects, isAdmin, setProjects, setErrorOpen, setSuccessOpen, filterText, userData, blobList }) => {
    const [titleModalIsOpen, setTitleModalIsOpen] = useState(false)
    const [descModalIsOpen, setDescModalIsOpen] = useState(false)
    const [userModalIsOpen, setUserModalIsOpen] = useState(false)
    const [containerModalIsOpen, setContainerModalIsOpen] = useState(false)
    const [confirmModalIsOpen, setConfirmModalIsOpen] = useState(false)
    const [func, setFunc] = useState(false)
    const [index, setIndex] = useState(-1)
    const [prj, setPrj] = useState({ id: -1, title: '', description: '', projectHolderId: -1, adminId: -1 })
    const [anchorEl, setAnchorEl] = useState(null)

    const scopes = protectedResources.scopes;
    const endpointDelete = protectedResources.apiDeleteProject.endpoint;

    async function deleteProject(project) {
        let data = null
        await setProjects(projects.filter(x => x !== project))
        await callApiWithToken(token, endpointDelete, account, "json", "POST", JSON.stringify({ id: project.id}),
            new Headers({ 'content-type': 'application/json' })).then(response => { data = response });
      }

    //Authentication
    const { account, token } = useGetToken(scopes)

    let id = 0;

    return (
        <div className="project-list">

            {/*These are the pop-ups used for editing the project, the assigned users and the containers.
               These are only created if the user is an admin*/}
            { isAdmin ? <div>
                <ConfirmAction modalIsOpen={confirmModalIsOpen} setModalIsOpen={setConfirmModalIsOpen} onConfirm={func} />
                <ChangeTitleForm modalIsOpen={titleModalIsOpen} setModalIsOpen={setTitleModalIsOpen} projects={projects.filter(x => x !== prj)} index={index} setProjects={setProjects}
                    project={prj} account={account} token={token} setErrorOpen={setErrorOpen} setSuccessOpen={setSuccessOpen} />
                <ChangeDescForm modalIsOpen={descModalIsOpen} setModalIsOpen={setDescModalIsOpen} projects={projects.filter(x => x !== prj)} index={index} setProjects={setProjects}
                    project={prj} account={account} token={token} setErrorOpen={setErrorOpen} setSuccessOpen={setSuccessOpen} />
                <ChangeUserForm modalIsOpen={userModalIsOpen} setModalIsOpen={setUserModalIsOpen} id={prj.id} projectHolderId={prj.projectHolderId} account={account}
                    token={token} setErrorOpen={setErrorOpen} setSuccessOpen={setSuccessOpen} userData={userData} />
                <ChangeContainerForm modalIsOpen={containerModalIsOpen} setModalIsOpen={setContainerModalIsOpen} id={prj.id} account={account} token={token}
                    setErrorOpen={setErrorOpen} setSuccessOpen={setSuccessOpen} blobList={blobList}/>
                {/*This is the menu used for selecting what to edit for a given project
               This is only created if the user is an admin*/}
                <Menu
                    id="simple-menu"
                    anchorEl={anchorEl}
                    open={Boolean(anchorEl)}
                    onClose={() => { setAnchorEl(null) }}
                >
                    {/*The items in the menu that open the corresponding pop-ups*/}
                    <MenuItem onClick={() => { setAnchorEl(null); setTitleModalIsOpen(true) }}>Change title</MenuItem>
                    <MenuItem onClick={() => { setAnchorEl(null); setDescModalIsOpen(true) }}>Change description</MenuItem>
                    <MenuItem onClick={() => { setAnchorEl(null); setContainerModalIsOpen(true) }}>Change containers</MenuItem>
                    <MenuItem onClick={() => { setAnchorEl(null); setUserModalIsOpen(true) }}>Assign user</MenuItem>
                </Menu>
            </div> : null}

            {projects.filter((project) => project.title.includes(filterText) || project.description.includes(filterText)).map((project) => (
                <div className="project-preview" key={id++}>
                    <div style={{ position: 'relative' }}>
                        {/*Map each project to its title and the description and 
                       link it to the corresponding detailed project view page*/}
                        <Link to={"/project/" + project.id}>
                            <h5 style={{ overflow: 'hidden' }}>
                                {project.title}
                            </h5>
                            <h6 style={{ overflow: 'hidden' }}>
                                {project.description}
                            </h6>
                        </Link>

                        {/*This is the icon that is used to bring up the editing menu.
                           It is only created for admins.*/}
                        {isAdmin ?
                            <div>
                                <FaTrashAlt style={{ position: 'absolute', right: '6%', top: '50%', width: 25, height: 25, cursor: 'pointer' }}
                                    onClick={() => {
                                        setFunc(() => () => deleteProject(project));
                                        setConfirmModalIsOpen(true);
                                    }} />
                                <FaEdit style={{ position: 'absolute', right: '3%', top: '50%', width: 25, height: 25, cursor: 'pointer' }}
                                    onClick={(event) => { setPrj(project); setIndex(projects.indexOf(project)); setAnchorEl(event.currentTarget) }} />
                            </div>
                            : null}
                    </div>
                    <br />
                </div>

            ))
            }

        </div>
    );
}

export default ProjectList;