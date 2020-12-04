import React from 'react'
import CommandButton from '../../../components/UI/CommandButton'
import FormAdd from '../../../forms/common/FormAdd'

export default function CardUsersCommandRow(props) {
    return (
        <div className="command__row">
            <div className="filter"></div>
            <div className="actions__row">
                <CommandButton caption="Clear choice" action={props.clearChoiceFunction}
                               className="command-button--yellow"/>
                <CommandButton caption="Delete chosen" action={props.deleteUsersFunction}
                               className="command-button--white"
                               form="list-of-collaborators"/>
                
                <FormAdd {...props} onSubmit={props.addUserFunction}
                     inputPlaceholder = "input login"
                     buttonCaption = "Add user"/>
            </div>
        </div>
    )
}
