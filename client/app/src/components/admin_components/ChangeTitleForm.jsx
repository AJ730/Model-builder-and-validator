import Modal from 'react-modal'
import TextField from '@material-ui/core/TextField';
import { useState, useEffect } from 'react'
import { protectedResources } from '../../authConfig'
import { callApiWithToken } from '../../util/useFetch'

/**
 * Create a component to display and edit the title of a project.
 * @param modalIsOpen A boolean used to check if the modal is open
 * @param setModalIsOpen The update function to update the boolean passed to this component
 * @param projects The list of projects 
 * @param setProjects Function to locally show the updated project
 * @param project The project that contains the title to update
 * @param account The account of the current user
 * @param token The bearer token of the user
 * @param index The index of the project in the supplied list
 * @param setErrorOpen Function to make error messsage visible
 * @param setSuccessOpen Function to make success message visible
 * @returns Component that enables the admin to display and edit the title of a project
 */
if (process.env.NODE_ENV !== 'test') Modal.setAppElement('#root')
const ChangeTitleForm = ({ projects, setProjects, modalIsOpen, setModalIsOpen, project, account, token, index, setErrorOpen, setSuccessOpen }) => {
    const [newTitle, setNewTitle] = useState('')
    let data = {}

    //Resources
    const endpoint = protectedResources.apiUpdateProject.endpoint

    //This is the method that submits the form when user clicks the submit button.
    const onSubmit = async (e) => {
        e.preventDefault()
        setModalIsOpen(false)

        //Call the api to update the project with the new title.
        await callApiWithToken(token, endpoint, account, "json", "POST", JSON.stringify({ id: project.id, title: newTitle }), new Headers({ 'content-type': 'application/json' }))
            .then(response => { data = response });
        if (data && data.status !== 500) {
            setSuccessOpen(true)
        } else { setErrorOpen(true); return }

        //Update title
        setNewTitle(newTitle)
        project.title = newTitle
        //Clone array to not mutate
        const newProjects = [...projects]
        //Insert new project at original place
        newProjects.splice(index, 0, project);
        setProjects(newProjects)
    }

    //This is the method that resets the form when user clicks the cancel button.
    const cancelContainer = () => {
        setNewTitle(newTitle)
        setModalIsOpen(false)
    }

    //This is used to update the modal with the correct title.
    useEffect(() => {
        setNewTitle(project.title)
    }, [project])

    return (
        //A modal is a pop-up.
        <Modal style={{
            overlay: {
                position: 'fixed', top: '35%', left: '30%', width: '40%', height: '30%',
                backgroundColor: 'rgba(255, 255, 255, 0)'
            }
        }} isOpen={modalIsOpen} >
            <form className='add-form' onSubmit={onSubmit}>
                <h5>Change Title</h5>
                {/* This input takes a text and if its changed it changes the value.*/}
                <TextField id="standard-basic" value={newTitle} label={'Enter Value'} style={{ position: 'absolute', width: '100%' }}
                    onChange={(str) => setNewTitle(str.target.value)} />
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

export default ChangeTitleForm
