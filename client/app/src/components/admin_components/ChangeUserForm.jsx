import Modal from 'react-modal'
import TextField from '@material-ui/core/TextField';
import { useState, useEffect, useRef } from 'react'
import { protectedResources } from '../../authConfig';
import { callApiWithToken } from '../../util/useFetch'
import SelectSearch from 'react-select-search/dist/cjs';
import 'react-select-search/style.css';
import fuzzySearch from 'react-select-search/dist/cjs/fuzzySearch'

/**
 * Create a component to display the user and edit the assigned projectHolder of a project.
 * @param modalIsOpen A boolean used to check if the modal is open
 * @param setModalIsOpen The update function to update the boolean passed to this component
 * @param id The id of the project to update
 * @param projectHolderId The id of the projectHolder that contains the project
 * @param account The account of the current user
 * @param token The bearer token of the user
 * @param setErrorOpen Function to make error messsage visible
 * @param setSuccessOpen Function to make success message visible
 * @returns Component that enables the admin to display and edit the projectHolderId of a project
 */
if (process.env.NODE_ENV !== 'test') Modal.setAppElement('#root')
const ChangeUserForm = ({ modalIsOpen, setModalIsOpen, id, projectHolderId, account, token, setErrorOpen, setSuccessOpen, userData }) => {
    const [newUser, setNewUser] = useState('')
    const didMountRef = useRef(false);

    const users = userData.map(user => ({ value: user.email, name: user.email }))

    //Resources
    const endpointProject = protectedResources.apiUpdateProjectUser.endpoint
    const endpointUser = protectedResources.apiGetUserWithId.endpoint
    const endpointProjectHolder = protectedResources.apiGetProjectHolder.endpoint
    const endpointProjectHolderUser = protectedResources.apiUserProjectHolder.endpoint

    let data = null

    useEffect(() => {
        async function fetchData() {
            //The projectHolder is acquired
            await callApiWithToken(token, endpointProjectHolder, account, "json", "POST", JSON.stringify({ id: projectHolderId }), new Headers({ 'content-type': 'application/json' }))
                .then(response => { data = response });
            //The user is acquired
            await callApiWithToken(token, endpointUser, account, "json", "POST", JSON.stringify({ id: data.userId }), new Headers({ 'content-type': 'application/json' }))
                .then(response => { data = response });
            //Set the newUser to the email
            setNewUser(data.email);
        }
        if (didMountRef.current)
            fetchData();
        else
            didMountRef.current = true;
    }, [id]);

    //This is the method that submits the form when user clicks the submit button.
    const onSubmit = async (e) => {
        e.preventDefault()
        setModalIsOpen(false)

        //Get the projectHolder using a users id
        await callApiWithToken(token, endpointProjectHolderUser, account, "json", "POST", JSON.stringify({ id: userData.filter(x => x.email === newUser)[0].id }), new Headers({ 'content-type': 'application/json' }))
            .then(response => { data = response });

        //Call the api to update the project with the new projectHolderId.
        callApiWithToken(token, endpointProject, account, "json", "POST", JSON.stringify({ id: id, projectHolderId: data.id }), new Headers({ 'content-type': 'application/json' }));

        if (data && data.status !== 500) {
            setSuccessOpen(true)
        } else { setErrorOpen(true); return }

        setNewUser(newUser)
    }

    //This is the method that resets the form when user clicks the cancel button.
    const cancelContainer = () => {
        setNewUser(newUser)
        setModalIsOpen(false)
    }

    return (
        //A modal is a pop-up.
        <Modal style={{
            overlay: {
                position: 'fixed', top: '35%', left: '30%', width: '40%', height: '30%',
                backgroundColor: 'rgba(255, 255, 255, 0)'
            }
        }} isOpen={modalIsOpen} >
            <form className='add-form' onSubmit={onSubmit}>
                <h5>Change Users</h5>
                <div style={{position:'relative', right:'3%'}}>
                    <SelectSearch
                        className='select-search'
                        options={users}
                        value={newUser}
                        search
                        filterOptions={fuzzySearch}
                        placeholder='Please select a client'
                        onChange={(selectedOption) => {setNewUser(selectedOption)}}
                    />
                </div>
                {/* The two buttons used for creating and canceling the form.*/}
                <div style={{ position: 'absolute', top: '170px', right: '0%', display: 'box', width: '90%' }}>
                    <input type='submit' style={{ width: '40%' }} className='popbtn crtbtn' value='Apply'></input>
                    <button type='button' style={{ width: '40%' }} className='popbtn cnclbtn' onClick={cancelContainer}>
                        Cancel
                    </button>
                </div>
            </form>
        </Modal>
    )
}

export default ChangeUserForm
