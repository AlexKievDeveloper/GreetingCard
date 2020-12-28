import React from 'react'
import './style.css';

import {Link} from 'react-router-dom'

import home from '../../assets/images/home.png';
import user from '../../assets/images/user.png';
import photos from '../../assets/images/photos.png';
import exit from '../../assets/images/exit.png';

export default function Menu(props) {

    function handleLogout(event) {
        event.preventDefault();
        props.logoutCall();
    }

    let classForRow = 'navigation-row';
    if (!props.isLoggedIn) {
        classForRow = 'navigation-row-before-login';
    }
    return (

        <div className={classForRow}>
            <Link to="/home" className="navigation-link home">
                <img src={home} alt=""/>
            </Link>
            {props.isLoggedIn && <Link to="/profile" className="navigation-link profile">
                <img src={user} alt=""/>
            </Link>}
            {props.isLoggedIn && <Link to="/cards/my" className="navigation-link my-cards">
                <img src={photos} alt=""/>
            </Link>}
            {props.isLoggedIn &&
            <Link to="/login" className="navigation-link log-out" onClick={(e) => handleLogout(e)}>
                <img src={exit} alt="" />
            </Link>}
        </div>
    )
}
