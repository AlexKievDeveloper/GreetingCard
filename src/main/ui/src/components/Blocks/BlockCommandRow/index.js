import React from 'react'
import './style.css';
import CommandButton from '../../UI/CommandButton';

export default function BlockCommandRow(props) {
    /* function deleteBlock() {
         blockService.deleteBlock(props.id);
         props.history.push('/all_cards');
     }
    */

    return (
        <div className="command__row">
            <div className="filter__blocks">
            </div>
            <div className="actions__row">
                <CommandButton className="command-button--yellow" caption="Save Block" action={props.save}/>
            </div>
        </div>
    )
}
