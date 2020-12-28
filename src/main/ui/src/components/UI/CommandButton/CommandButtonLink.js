import React from 'react'
import './style.css'
import {Link} from 'react-router-dom'
import { Text } from '../../Language/Text';

export default function CommandButtonLink(props) {
    const className = props.className + " command-button";
    return (
        <Link to={props.to} className = {className} > <Text tid={props.caption}/> </Link> 
    )
}
