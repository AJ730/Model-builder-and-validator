import Modal from 'react-modal'
import { useState } from 'react'
import SelectSearch from 'react-select-search/dist/cjs/SelectSearch'
import fuzzySearch from 'react-select-search/dist/cjs/fuzzySearch'

/**
 * Create a component to add new containers to a given project.
 * @param containers The list of containers that a given project has
 * @param setContainers The update function to update the list of containers passed to this component
 * @param containerModalIsOpen A boolean used to check if the modal is open
 * @param setContainerModalIsOpen The update function to update the boolean passed to this component
 * @returns Component that enables the admin to add new containers
 */
if (process.env.NODE_ENV !== 'test') Modal.setAppElement('#root')
const AddContainerForm = ({ containers, setContainers, containerModalIsOpen, setContainerModalIsOpen, blobList }) => {
    const [video, setVideo] = useState('')
    const [csv, setCsv] = useState([])
    const [text, setText] = useState([])
    const [title, setTitle] = useState('')
    const [desc, setDesc] = useState('')
    const [selected, setSelected] = useState(true)
    const [selectedBoth, setSelectedBoth] = useState(true)


    //This is the method that resets the form when user clicks the cancel button.
    const addContainer = () => {
        setSelectedBoth(true)
        setVideo('')
        setCsv([])
        setText([])
        setTitle('')
        setDesc('')
        setContainerModalIsOpen(false)
    }

    //This is the method that submits the containers  when user clicks the submit button.
    const submitContainer = () => {
        //If there is no video or csv set the selection boolean to false.
        if(video === '') {setSelected(false); return}

        if (video.length === 0 || csv.length === 0 || text.length === 0) {
            setSelectedBoth(false)
        } else {
            setSelectedBoth(true)
            setContainerModalIsOpen(false)
            var container = {
                name: title,
                description: desc,
                blobName: video,
                csv: csv,
                text: text
            }
            if (container.name.length === 0) container.name = 'Default Container'
            setVideo('')
            setCsv([])
            setText([])
            setTitle('')
            setDesc('')
            setContainers(containers.concat(container))
        }
    }

    return (
        //A modal is a popup
        <Modal style={{
            overlay: {
                position: 'fixed', width: '50%', height: '90%', left: '20%', top: '5%',
                backgroundColor: 'rgba(255, 255, 255, 0)'
            }
        }} isOpen={containerModalIsOpen}>
            <header className='modal-header'>
                <h1>Add Container</h1>
            </header>

            <div className='add-div'>
                <label>Title</label>
                {/* This input takes a text and if its changed it changes the value.*/}
                <input type='text' placeholder='Add Title.'
                    value={title} onChange={(str) => setTitle(str.target.value)}>
                </input>
            </div>

            <div className='add-div'>
                <label>Description</label>
                {/* This input takes a text and if its changed it changes the value.*/}
                <textarea rows='5' placeholder='Add Description.' style={{ resize: 'none' }}
                    value={desc} onChange={(str) => setDesc(str.target.value)}>
                </textarea>
            </div>

            <div className='add-project-div'>
                <label>Select a video:</label>
                {/* This input takes a file and if its changed it changes the value.*/}
                <div style={{display:'flex', position:'relative', right:'3%'}}>
                        <SelectSearch
                            className='select-search'
                            options={blobList}
                            value={video}
                            search
                            filterOptions={fuzzySearch}
                            placeholder='Please select a video'
                            onChange={(selectedOption) => {setVideo(selectedOption); setSelected(true)}}
                        />
                        {selected ? null : <h6 style={{position:'relative', color:'red', left:'6%', top:'12px'}}>Please select a video</h6>}
                    </div>
            </div>

            <br></br>

            <div className='add-project-div'>
                <label>Select a CSV:</label>
                {/* This input takes a file and if its changed it changes the value.*/}
                <input type='file' accept='.csv, text/csv' style={{display:'flex', position:'relative'}}
                    onChange={(file) => setCsv(file.target.files[0])}>
                </input>
            </div>

            <br></br>

            <div className='add-project-div'>
                <label>Select Classes:</label>
                {/* This input takes a file and if its changed it changes the value.*/}
                <input type='file' accept='.txt, text/plain' style={{display:'flex', position:'relative'}}
                    onChange={(file) => setText(file.target.files[0])}>
                </input>
            </div>
            {/* This warning is displayed if user submits the form before a video and csv are selected.*/}
            <h5 style={{ color: 'red' }}>
                {selectedBoth ? null : 'Please input all three files.'}
            </h5>
            {/* The two buttons used for creating and canceling the form.*/}
            <div style={{position: 'relative', bottom: '-8%', left: '55%', display: 'box', width: 300 }}>
                <button type='button' className='popbtn crtbtn' style={{ width: 100 }} onClick={submitContainer}>
                    Create
                </button>
                <button type='button' className='popbtn cnclbtn' style={{ width: 100 }} onClick={addContainer}>
                    Cancel
                </button>
            </div>

        </Modal>
    )
}

export default AddContainerForm
