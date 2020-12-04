import React from 'react'
import './style.css';
import {Link} from 'react-router-dom'

import progressCardImg from '../../../assets/images/card-in-progress-icon.png';
import finishedCardImg from '../../../assets/images/finished-card-icon.png';


export default function CardInfo(props) {
    const imgSrc = props.cardStatus === 'STARTUP' ? progressCardImg : finishedCardImg;

    return (
        <div className="card__row">
            <Link to="/preview" className="card-link">
                <img src={imgSrc} alt=""/>
            </Link>
            <div className="card-details__column">
                <div className="card-name card-styled-as-input"> {props.name}</div>
                <div className="card-author card-styled-as-input"> {props.userName}</div>
            </div>
        </div>
    )
}
