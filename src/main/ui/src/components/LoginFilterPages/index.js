import React from 'react'
import FilterButton from '../UI/FilterButton'
import './style.css';
import PropTypes from 'prop-types'

export default function LoginFilterPages(props) {
    return (
        <div className="command__row">
            <div className="filter__pages">
                <FilterButton linkTo="/signup" caption="signUpButton" isActive={props.page === 'login'}/>
                <FilterButton linkTo="/login" caption="loginButton" isActive={props.page === 'signup'}/>
            </div>
        </div>
    )
}

LoginFilterPages.propTypes = {
    page: PropTypes.string
}