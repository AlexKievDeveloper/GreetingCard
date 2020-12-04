import React from 'react'
import './style.css';
import { Link } from 'react-router-dom'
import config from '../../services/config';

export default function HomeButtons(props) {

    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(config.greetingUrl);
          } catch (err) {
            console.log('Failed to copy!');
          }
    }
    
    console.log(props.user);
    if (props.user !== '' && props.user != null) {
    return (
        <div className="call-to-action__row">
           <Link to="/home" className="call-to-action yellow-button" onClick={() => handleCopy()}>Share our link</Link>
           <Link to="/cards/my" className="call-to-action white">Go to My Cards</Link>
        </div>
    )} else {
    return (
        <div className="call-to-action__row">
             <Link to="/signup" className="call-to-action yellow-button">Sign Up</Link>
             <Link to="/login" className="call-to-action white">Log In</Link>
        </div>
    )
    }
}
