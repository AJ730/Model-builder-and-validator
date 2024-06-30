import Modal from 'react-modal'

const ConfirmAction = ({ modalIsOpen, setModalIsOpen, onConfirm }) => {
    return (
        <Modal style={{
            overlay: {
                position: 'absolute', top: '40%', left: '40%', width: '470px', height: '200px',
                backgroundColor: 'rgba(255, 255, 255, 0)'
            }
        }} isOpen={modalIsOpen}>
            <h6>Are you sure you want to perform this action?</h6>
            {/* The two buttons used for creating and canceling the form.*/}
            <button style={{position:'absolute', left:'2%', bottom:'0%', width:'125px', backgroundColor:'green'}} type='button' className='popbtn crtbtn' onClick={() => { setModalIsOpen(false); onConfirm() }} >
                Confirm
            </button>
            <button style={{position:'absolute', right:'2%', bottom:'0%', width:'125px', backgroundColor:'firebrick'}} type='button' className='popbtn cnclbtn' onClick={() => setModalIsOpen(false)}>
                Cancel
            </button>
        </Modal>
    )
}

export default ConfirmAction
