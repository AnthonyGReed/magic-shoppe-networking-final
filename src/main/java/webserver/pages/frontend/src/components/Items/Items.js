import React, {useState} from 'react'
import Axios from 'axios'
import Badge from 'react-bootstrap/Badge'
import Button from 'react-bootstrap/Button'
import Modal from 'react-bootstrap/Modal'

function Items(props) {
    const [itemData, setItemData] = useState("");
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const getWonderData = (id) => {
      setItemData("Loading")
      Axios.get("/api/itemInfo?type=wonder&id=" + id).then(result => {
        setItemData(result.data)
        handleShow()
      })
    }

    const items = props.data.map((item, index) => {
      if(item.type !== "Scroll" && item.type !== "Potion") {
        return(<tr key={index} onClick={() => getWonderData(item.id)}>
          <td><span className={`dot ${item.rarity}`}></span> {item.name} {item.onSale && <Badge pill>Sale!</Badge>}</td>
          <td>{item.type}</td>
          <td>{item.goldCost}</td>
          <td>{item.charges}</td>
          <td>{item.stones}</td>
          <td>{item.page}</td>
          </tr>)
      }
    })

    return(
      <>
        {items}
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

export default Items;