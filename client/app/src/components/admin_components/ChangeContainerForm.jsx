import Modal from 'react-modal'
import APButton from '../core/APButton'
import AddContainerForm from './AddContainerForm'
import { useState, useEffect, useRef } from 'react'
import { FaTrashAlt } from 'react-icons/fa'
import { protectedResources } from '../../authConfig'
import { callApiWithToken } from '../../util/useFetch'
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';

/**
 * Create a component to display and edit containers by obtaining a token and calling the protected endpoint.
 * @param modalIsOpen A boolean used to check if the modal is open
 * @param setModalIsOpen The update function to update the boolean passed to this component
 * @param id The id of the project that contains the containers to update
 * @param account The account of the current user
 * @param token The bearer token of the user
 * @param setErrorOpen Function to make error messsage visible
 * @param setSuccessOpen Function to make success message visible
 * @returns Component that enables the admin to display and change containers of a project
 */
if (process.env.NODE_ENV !== 'test') Modal.setAppElement('#root')
const ChangeContainerForm = ({ modalIsOpen, setModalIsOpen, id, account, token, setErrorOpen, setSuccessOpen, blobList }) => {
    const [newContainers, setNewContainers] = useState([])
    const [containerModalIsOpen, setContainerModalIsOpen] = useState(false)
    const [removeContainer, setRemoveContainer] = useState([])
    const [data, setData] = useState([])
    const didMountRef = useRef(false);


    //Resources
    const endpointGet = protectedResources.apiProjectContainers.endpoint
    const endpointCreate = protectedResources.apiCreateContainer.endpoint
    const endpointRemove = protectedResources.apiDeleteContainer.endpoint

    useEffect(() => {
        let data = []
        async function fetchData() {
            //Here the containers are acquired from the database.
            await callApiWithToken(token, endpointGet, account, "json", "POST", JSON.stringify({ id: id }), new Headers({ 'content-type': 'application/json' }))
                .then(response => { data = response });
            setNewContainers(data);
            setData(data)
        }
        if (didMountRef.current)
            fetchData();
        else
            didMountRef.current = true;
    }, [id]);

    //This is the method that submits the form when user clicks the submit button.
    const onSubmit = (e) => {
        e.preventDefault()
        setModalIsOpen(false)

        //The containers are checked for the attribute containerId because if it doesnt exist, that means it does not exist in the db.
        //The new ones without id are created by using the endpoint.
        newContainers.forEach(container => {
            if (container.hasOwnProperty('id')) { }
            else {
                const formData = new FormData()
                formData.append("projectId", id)
                formData.append("blobName", container.blobName)
                formData.append("csv", container.csv)
                formData.append("classes", container.text)
                formData.append("description", container.description)
                formData.append("name", container.name)
                callApiWithToken(token, endpointCreate, account, "json", "POST", formData);
            }
        });

        //The containers that are marked for removal are removed by calling the endpoint.
        removeContainer.forEach(id => {
            callApiWithToken(token, endpointRemove, account, "json", "POST", JSON.stringify({ id: id }), new Headers({ 'content-type': 'application/json' }));
        })

        setNewContainers(newContainers)
        setRemoveContainer([])

        if (data && data.status !== 500) {
            setSuccessOpen(true)
        } else { setErrorOpen(true); return }
    }

    let containerId = 0

    //This is the method that resets the form when user clicks the cancel button.
    const cancelContainer = () => {
        setModalIsOpen(false)
        setNewContainers(data)
        setRemoveContainer([])
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
        <Modal style={{
            overlay: {
                position: 'fixed', top: '25%', left: '30%', width: '40%', height: '50%',
                backgroundColor: 'rgba(255, 255, 255, 0)'
            }
        }} isOpen={modalIsOpen} >
            <form className='add-form' onSubmit={onSubmit}>
                <header className='modal-header' style={{ display: 'flex', width: '100%' }}>
                    <h3>Containers</h3>
                    {/* This section is used to create containers*/}
                    <APButton clr="green" str="Add Container" onClick={() => setContainerModalIsOpen(true)}></APButton>
                    <AddContainerForm containers={newContainers} setContainers={setNewContainers}
                        containerModalIsOpen={containerModalIsOpen} setContainerModalIsOpen={setContainerModalIsOpen}
                        blobList={blobList}    
                    />
                </header>

                <div className='container-wrapper' style={{ position: 'absolute', width: '100%' }} >
                    {/* Maps each container to the following things*/}
                    {newContainers.map((container) => (
                        <div key={containerId++} style={{ whiteSpace: 'nowrap' }}>
                            {/* A tooltip that displays the video and csv name.*/}
                            <CstmTooltip
                                title={<div>
                                    <h6>Video: {container.blobName}</h6>
                                    <h6>CSV: {container.csvName ? container.csvName : container.csv.name}</h6>
                                    <h6>Classes: {container.className ? container.className : container.text.name}</h6>
                                </div>} arrow placement="top-start">
                                <h5 style={{ backgroundColor: 'whitesmoke', height: '25px', overflow: 'hidden' }}>
                                    {/* This is a trash icon used to delete containers*/}
                                    <FaTrashAlt style={{ cursor: 'pointer', width: 50, height: 20 }}
                                        onClick={() => {
                                            if (container.hasOwnProperty('id')) { setRemoveContainer(removeContainer.concat(container.id)) };
                                            setNewContainers(newContainers.filter(x => x !== container))
                                        }} />
                                    {container.name}
                                </h5>
                            </CstmTooltip>
                        </div>
                    ))}
                </div>

                {/* The two buttons used for creating and canceling the form.*/}
                <div style={{ position: 'absolute', top: '350px', right: '0%', display: 'box', width: '90%' }}>
                    <input type='submit' style={{ width: '40%' }} className='popbtn crtbtn' value='Apply'></input>
                    <button type='button' style={{ width: '40%' }} className='popbtn cnclbtn' onClick={cancelContainer}>
                        Cancel
                    </button>
                </div>

            </form>
        </Modal>
    )
}

export default ChangeContainerForm
