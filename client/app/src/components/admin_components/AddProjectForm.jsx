import Modal from 'react-modal'
import { useState } from 'react'
import APButton from '../core/APButton'
import AddContainerForm from './AddContainerForm'
import { FaTrashAlt } from 'react-icons/fa'
import { protectedResources } from '../../authConfig'
import useGetToken from '../../util/useGetToken'
import { callApiWithToken } from '../../util/useFetch'
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';
import SelectSearch from 'react-select-search/dist/cjs';
import 'react-select-search/style.css';
import fuzzySearch from 'react-select-search/dist/cjs/fuzzySearch'

/**
 * Create a component to add new projects to a specified projectHolder.
 * @param modalIsOpen A boolean used to check if the modal is open
 * @param setModalIsOpen The update function to update the boolean passed to this component
 * @param projects The list of projects 
 * @param setProjects Function to locally show the updated project
 * @param setErrorOpen Function to make error messsage visible
 * @param setSuccessOpen Function to make success message visible
 * @returns Component that enables the admin to add new projects with containers and assign them to a user
 */
if (process.env.NODE_ENV !== 'test') Modal.setAppElement('#root')
const AddProjectForm = ({ modalIsOpen, setModalIsOpen, projects, setProjects, setErrorOpen, setSuccessOpen, userData, blobList }) => {
    const [title, setTitle] = useState('')
    const [desc, setDesc] = useState('')
    const [client, setClient] = useState('')
    const [containers, setContainers] = useState([])
    const [selected, setSelected] = useState(true)
    const [containerModalIsOpen, setContainerModalIsOpen] = useState(false)

    const users = userData.map(user => ({ value: user.email, name: user.email }))

    //Resources
    const endpointProjectHolder = protectedResources.apiUserProjectHolder.endpoint
    const endpointContainer = protectedResources.apiCreateContainer.endpoint
    const endpointProject = protectedResources.apiCreateProject.endpoint
    const scopes = protectedResources.scopes

    //Authentication
    const { token, account } = useGetToken(scopes)

    //let projectId = 0;
    let id = 0;

    //This is the method that submits the project when user clicks the submit button.
    const onSubmit = async (e) => {
        
        //Data is an object used to hold the response data of the endpoints.
        //It is reusable since creating a project follows an order.
        let data = null
        
        e.preventDefault()
        if(client === '') {setSelected(false);return}
        setModalIsOpen(false)

        //Get the projectHolder using a users id
        await callApiWithToken(token, endpointProjectHolder, account, "json", "POST", JSON.stringify({ id: userData.filter(x => x.email === client)[0].id }), new Headers({ 'content-type': 'application/json' }))
            .then(response => { data = response });

        //Create JSON for creating project.
        var project = {
            title: title,
            description: desc,
            projectHolderId: data.id,
            adminId: account.idTokenClaims.oid
        }

        //Send POST request to create project and save the response.
        await callApiWithToken(token, endpointProject, account, "json", "POST", JSON.stringify(project), new Headers({ 'content-type': 'application/json' }))
            .then(response => { data = response });

        //Loop through each container and send a POST request to create a container.
        containers.forEach(container => {
            const formData = new FormData()
            formData.append("projectId", data.id)
            formData.append("blobName", container.blobName)
            formData.append("csv", container.csv)
            formData.append("description", container.description)
            formData.append("classes", container.text)
            formData.append("name", container.name)
            callApiWithToken(token, endpointContainer, account, "json", "POST", formData);
        })

        if (data && data.status !== 500) {
            setSuccessOpen(true)
        } else { setErrorOpen(true); return }

        //Add id to project object.
        var pair = { id: data.id }
        project = { ...project, ...pair }
        //Edit projects list to add new project
        setProjects(projects.concat(project))
        setTitle('')
        setDesc('')
        setClient('')
        setContainers([])
        id = 0
    }

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
        //A modal is a pop-up.
        <Modal isOpen={modalIsOpen}>
            <header className='modal-header'>
                <h1>Project</h1>
                <h5 style={{ right: '2%', color: 'grey' }}>
                    TIP: You can edit/add to all of the fields below even after creation!
                </h5>
            </header>

            <form className='add-form' onSubmit={onSubmit}>

                <div className='add-div'>
                    <label style={{display:'flex'}}>Project Title <p style={{color:'red'}}>*</p> </label>
                    {/* This input takes a text and if its changed it changes the value.*/}
                    <input type='text' required placeholder='Add Title.'
                        value={title} onChange={(str) => setTitle(str.target.value)}>
                    </input>
                </div>

                <div className='add-div'>
                    <label>Project Description</label>
                    {/* This input takes a text and if its changed it changes the value.*/}
                    <textarea rows='5' placeholder='Add Description.' style={{ resize: 'none' }}
                        value={desc} onChange={(str) => setDesc(str.target.value)}>
                    </textarea>
                </div>

                <div className='add-div'>
                    <label style={{display:'flex', height:'30px'}}>Client <p style={{color:'red'}}>*</p> </label>
                    <div style={{display:'flex', position:'relative', right:'3%'}}>
                        <SelectSearch
                            className='select-search'
                            options={users}
                            value={client}
                            search
                            filterOptions={fuzzySearch}
                            placeholder='Please select a client'
                            onChange={(selectedOption) => {setClient(selectedOption); setSelected(true)}}
                        />
                        {selected ? null : <h6 style={{position:'relative', color:'red', left:'6%', top:'15px'}}>Please select a client.</h6>}
                    </div>
                </div>

                {/* This section is used to create containers*/}
                <header className='modal-header' style={{ display: 'flex', width: 500 }}>
                    <h3>Containers</h3>
                    <APButton clr="green" str="Add Container" onClick={() => setContainerModalIsOpen(true)}></APButton>
                    <AddContainerForm containers={containers} setContainers={setContainers}
                        containerModalIsOpen={containerModalIsOpen} setContainerModalIsOpen={setContainerModalIsOpen} 
                        blobList={blobList}
                    />
                </header>

                <div className='container-wrapper'>
                    {/* Maps each container to the following things*/}
                    {containers.map((container) => (
                        <div key={id++} style={{ whiteSpace: 'nowrap' }}>
                            {/* A tooltip that displays the video and csv name.*/}
                            <CstmTooltip
                                title={<div>
                                    <h6>Video: {container.blobName}</h6>
                                    <h6>CSV: {container.csv.name}</h6>
                                    <h6>Classes: {container.text.name}</h6>
                                </div>} arrow placement="top-start">
                                <h5 style={{ backgroundColor: 'whitesmoke', height: '25px', overflow: 'hidden' }}>
                                    {/* This is a trash icon used to delete containers*/}
                                    <FaTrashAlt style={{ cursor: 'pointer', width: 50, height: 20 }}
                                        onClick={() => setContainers(containers.filter(x => x !== container))} />
                                    {container.name}
                                </h5>
                            </CstmTooltip>
                        </div>
                    ))}
                </div>
                {/* The two buttons used for creating and canceling the form.*/}
                <div style={{ position: 'absolute', bottom: '0%', right: '0', display: 'box', width: 450 }}>
                    <input type='submit' className='popbtn crtbtn' value='Create Project'></input>
                    <button type='button' className='popbtn cnclbtn' onClick={() => setModalIsOpen(false)}>
                        Cancel
                    </button>
                </div>

            </form>
        </Modal>
    )
}

export default AddProjectForm
