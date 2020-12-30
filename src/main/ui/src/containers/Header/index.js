import React from 'react'
import PropTypes from 'prop-types'
import './style.css';
import Menu from '../../components/Menu';
import LanguageSelector from '../../components/Language/LanguageSelector';
import { Text } from '../../components/Language/Text';

export default function Header(props) {

    let isLogin = false;
    if (props.userName) {
        isLogin = props.userName.length > 0;
    }
    let classForHeaderRow = "header-right__row";
    if (!isLogin) {
        classForHeaderRow = "header-right__row0"
    }

    return (
        <header className="header">
            <div className="app-name">GreetTeam</div>
            <div className={classForHeaderRow}>
                <LanguageSelector />
                <Menu isLoggedIn={isLogin} logoutCall={props.logoutCall}/>
                {isLogin && <div className="user-greeting"> <Text tid="greetingUser"/> gubarev.alexandr@gmail.com!</div>}
            </div>
        </header>
    )
}

Header.propTypes = {
    userName: PropTypes.string,
    logoutCall: PropTypes.func
}