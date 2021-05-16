import React, {useState} from 'react'
import Axios from 'axios'
import Badge from 'react-bootstrap/Badge'
import Modal from 'react-bootstrap/Modal'
import Button from 'react-bootstrap/Button'

function Potions(props) {
    const [itemData, setItemData] = useState("");
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const getPotionData = (id) => {
        setItemData("Loading...")
        Axios.get("/api/itemInfo?type=potion&id=" + id).then(result => {
          setItemData(result.data)
          handleShow()
        })
    }

    const potions = props.data.map((item, index) => {
        if(item.type === "Potion") {
          return(<tr key={index} onClick={() => getPotionData(item.id)} className={item.onSale ? "sale" : ""}>
            <td><span className={`dot ${item.rarity}`}></span> {item.name} {item.onSale && <Badge pill>Sale!</Badge>}</td>
            <td>{item.type}</td>
            <td>{item.goldCost}</td>
            <td>{item.doses}</td>
            <td>{item.page}</td>
          </tr>)
        }
    }) 

    return (
      <>
        {potions}
        <Modal show={show} onHide={handleClose}>
          <Modal.Header>
            <Modal.Title>{itemData.name}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {itemData.attunement && <p><em>Requires Attunement</em></p>}
            {itemData.limits && <p><strong>Limits: </strong>{itemData.limits}</p>}
            <p><strong>Description: </strong>{itemData.description}</p>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="primary" onClick={handleClose}>Ok</Button>
          </Modal.Footer>
        </Modal>
      </>
    )

}

export default Potions;