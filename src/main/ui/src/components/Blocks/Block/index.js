import React from 'react'
import BlockActions from '../BlockActions';
import BlockLink from '../BlockLink';
import parse from 'html-react-parser'
import './style.css';

export default function Block(props) {
    const blockLinks = props.block.linkList.map((link) => (
        <BlockLink key={link.id} link={link.link} type={link.type}/>));
    const backgroundColor = {
        backgroundColor: props.background
    }    
    return (
        <div className="congratulation-block">
            <div className="signature">
                {props.block.user.firstName}&#160;{props.block.user.lastName}
            </div>
            <div className="congratulation-body" style={backgroundColor}>
                { props.isEdit && <BlockActions {...props} id={props.block.id} onDeleteBlock={props.onDeleteBlock}/>}
                <div className="text-element">{parse(props.block.message)}</div>
                {blockLinks}
            </div>
        </div>
    )
}
