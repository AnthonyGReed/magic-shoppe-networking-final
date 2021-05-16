import React, { useState } from 'react'
import {Button, Modal, FormControl, Col} from 'react-bootstrap'

const Main = (props) => {
    const [shopID, setShopID] = useState("");
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const checkKey = (event) => {
        if(event.key === 'Enter') {
            props.load(shopID)
            handleClose()
        }
    }

    return (
        <>
            <Col className="buttonholder">
                <Button variant="outline-primary" onClick={props.new}>New Shop</Button>
            </Col>
            <Col className="buttonholder">
                <Button variant="outline-primary" onClick={handleShow}>Load Shop</Button>
            </Col>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header>
                    <Modal.Title>
                        Load Shop
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <FormControl 
                        value={shopID} 
                        onChange={e => setShopID(e.target.value)} 
                        onKeyDown={checkKey}
                        placeholder="Shop ID" />
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={() => {props.load(shopID); handleClose();}}>Submit</Button>
                </Modal.Footer>
            </Modal>
        </>
    )
}
                
                        
export default Main