import React from 'react'
import Items from "../../components/Items/Items"
import Potions from '../../components/Potions/Potions'
import Scrolls from '../../components/Scrolls/Scrolls'
import Table from 'react-bootstrap/Table'

const Shop = (props) => {
    return(
        <div>
            <p className="shopId">Shop ID: {props.data.id}</p>
            <Table responsive>
                <thead>

                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Gold Cost</th>
                        <th>Charges</th>
                        <th>Stones/Charge</th>
                        <th>Page</th>
                    </tr>
                </thead>
                <tbody>
                    <Items data={props.data.items}/>
                </tbody>
            </Table>
            <Table responsive>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Gold Cost</th>
                        <th>Doses</th>
                        <th>Page</th>
                    </tr>
                </thead>
                <tbody>
                    <Potions data={props.data.items}/>
                </tbody>
            </Table>
            <Table responsive>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Gold Cost</th>
                        <th>Page</th>
                    </tr>
                </thead>
                <tbody>
                    <Scrolls data={props.data.items}/>
                </tbody>
            </Table>
        </div>
    )
}
export default Shop