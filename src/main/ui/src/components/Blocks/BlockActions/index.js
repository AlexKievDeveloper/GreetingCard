import React from 'react'
import './style.css';
import {Link} from 'react-router-dom'
import markImg from '../../../assets/images/mark.png';
import {blockService} from "../../../services/blockService";

export default function BlockActions(props) {
    const deleteBlock = () => {
       const id = props.id;
       blockService.deleteBlock(id)
                   .then(() => props.onDeleteBlock(id))
                   .then(() => props.history.push('/edit_card/' + props.idCard));
    }

    return (
        <div className="block-actions__container">
            <div className="block-actions__dropdown">
                <img src={markImg} alt=""/>
                <div className="block-actions">
                    <Link to={"/edit_block/" + props.id} className="dropdown-link">Edit block</Link>
                    <Link to={'/edit_card/'+ props.idCard} className="dropdown-link" onClick={deleteBlock}>Delete block</Link>
                </div>
            </div>
        </div>

    )
}
