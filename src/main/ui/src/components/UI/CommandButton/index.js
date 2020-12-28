import React from 'react'
import './style.css'

export default function CommandButton(props) {
    const className = props.className + " command-button";
    return (
        <input type="submit" className={className}
                onClick={props.action}
                value={props.caption}
                form={props.form ? props.form : ''}/>
    )
}
