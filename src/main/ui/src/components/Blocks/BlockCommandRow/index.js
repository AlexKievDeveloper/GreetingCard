import React from 'react'
import './style.css';
import CommandButton from '../../UI/CommandButton';

export default function BlockCommandRow(props) {

    return (
        <div className="command__row">
            <div className="filter__blocks">
            </div>
            <div className="actions__row">
                <CommandButton className="command-button--yellow" caption="Save Block" action={props.saveFunction}/>
                <CommandButton className="command-button--white"  caption="Delete Block" action={props.deleteFunction}/>
            </div>
        </div>
    )
}
