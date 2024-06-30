const APButton = ({ clr, str, onClick }) => {
    return (
        <button type="button" style={{ backgroundColor: clr }}
            onClick={onClick}
            className='apbtn'>{str}</button>
    )
}

export default APButton
