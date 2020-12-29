import React from 'react'
import './style.css';
import {Link} from 'react-router-dom'
import PropTypes from 'prop-types'
import { Text } from '../../Language/Text';

export default function FilterButton(props) {
    if (props.isActive) {
        return (
            <Link to={props.linkTo} className="filter-button"> <Text tid={props.caption}/> </Link>
        )
    } else {
        return (
            <button disabled className="filter-button chosen"> <Text tid={props.caption}/> </button>
        )
    }

}

FilterButton.propTypes = {
    isActive: PropTypes.bool,
    linkTo: PropTypes.string,
    caption: PropTypes.string
}