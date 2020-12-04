import React from 'react'
import './style.css'
import {Link} from 'react-router-dom'

export default function CommandButtonLink(props) {
    const className = props.className + " command-button";
    return (
        <Link to={props.to} className = {className} > {props.caption} </Link> 
    )
}
