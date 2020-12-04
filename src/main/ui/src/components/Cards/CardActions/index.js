import React from 'react'
import './style.css';

import {Link} from 'react-router-dom'
import editImg from '../../../assets/images/edit.jpg';
import deleteImg from '../../../assets/images/delete.png';
import leaveImg from '../../../assets/images/leave.png';
import {cardService} from '../../../services/cardService';

export default function CardActions(props) {

    const deleteCard = () => {
        cardService.deleteCard(props.id)
                   .then(() => props.onDeleteCard(props.id))
                   .then(() => props.history.push('/cards/my'));
    }


    const leaveCard = () => {
        cardService.leaveCard(props.id)
                   .then(() => props.onDeleteCard(props.id))
                   .then(() => props.history.push('/cards/other'));
    }


    const editLink = '/edit_card/' + props.id;

    return (
        <div className="actions__column">
            <Link to={editLink}>
                <img className="card-action edit" src={editImg} alt=""/>
            </Link>
            {props.isMyCard && <Link to="/cards/my" onClick={deleteCard}>
                <img src={deleteImg}
                   className="card-action"
                   alt=""
            />    
            </Link>}
            {!props.isMyCard && <Link to="/cards/other" onClick={leaveCard}>
                <img src={leaveImg}
                   className="card-action"
                   alt=""
            />    
            </Link>}
        </div>
    )
}
