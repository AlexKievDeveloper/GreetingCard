import React from 'react'
import './style.css';
import {Link} from 'react-router-dom'
import markImg from '../../../assets/images/mark.png';
import {blockService} from "../../../services/blockService";
import { Text } from '../../Language/Text';

export default function BlockActions(props) {
    const deleteBlock = () => {
       const id = props.id;
       blockService.deleteBlock(id)
                   .then(() => props.onDeleteBlock(id))
                   .then(() => props.history.push('/edit_card/' + props.idCard + '/my_blocks'));
    }

    return (
        <div className="block-actions__container">
            <div className="block-actions__dropdown">
                <img src={markImg} alt=""/>
                <div className="block-actions">
                    <Link to={"/edit_block/" + props.id} className="dropdown-link"><Text tid="editBlock"/></Link>
                    <Link to={'/edit_card/'+ props.idCard + '/my_blocks'} className="dropdown-link" onClick={deleteBlock}><Text tid="deleteBlock"/></Link>
                </div>
            </div>
        </div>

    )
}
