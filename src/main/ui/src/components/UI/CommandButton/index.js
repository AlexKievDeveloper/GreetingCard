import React, {useContext} from 'react'
import { languageContext } from '../../../context/languageContext';
import './style.css'

export default function CommandButton(props) {
    const { dictionary } = useContext(languageContext);
    const className = props.className + " command-button";
    return (
        <input type="submit" className={className}
                onClick={props.action}
                value={dictionary[props.caption]}
                form={props.form ? props.form : ''}/>
    )
}
