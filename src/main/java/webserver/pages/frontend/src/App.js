import React, {useState} from 'react'
import './App.css';
import Main from './components/Main/Main'
import Shop from './containers/Shop/Shop'
import Axios from 'axios'
import {Container, Row} from 'react-bootstrap';

function App() {
  const [shopData, setShopData] = useState("");
  const [error, setError] = useState("Something has gone wrong.");

  const newShop = () => {
    setShopData("Loading")
    Axios.get("/api/newShop").then(result => {
      console.log(result.data)
      setShopData(result.data)
    })
  }

  const loadShop = (id) => {
    setShopData("Loading")
    let shopId = id.match(/^[a-z0-9]+$/i)
    if(id.length !== 6 || !shopId) {
      setError("Sorry. We could not find a shop with that ID. Please try again.")
      setShopData("Error")
    } else {
      try {
        Axios.get("/api/shopID?id=" + id).then(result => {
          setShopData(result.data)
        })
      } catch(err) {
        if(err.response.status === "404") {
          setError("Sorry. We could not find a shop with that ID. Please try again.")
          setShopData("Error")
        } else {
          console.log(err)
        }
      }
    }
  }

  let table = ""
  if (shopData === "Loading") {
    table = "Loading..."
  }

  if (shopData === "Error") {
    table = error
  }

  if (shopData !== "Loading" && shopData !=="Error" && shopData !== "") {
    table = <Shop data={shopData} />
  }

  return (
    <Container fluid className="App">
      <Row>
        <Main new={newShop} load={loadShop}/>
      </Row>
      <Row>
        {table}
      </Row>
    </Container>
  );
}

export default App;
