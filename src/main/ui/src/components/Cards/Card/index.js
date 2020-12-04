import React from 'react'
import CardActions from '../CardActions';
import CardInfo from '../CardInfo';
import './style.css';

export default function Card(props) {
    return (
        <div className="card-and-actions-block">
            <CardInfo name={props.card.name}
                      userName={props.card.user.login}
                      cardStatus={props.card.status}
            />
            <CardActions {...props} id={props.card.id} onDeleteCard={props.onDeleteCard}/>
        </div>
    )
}
