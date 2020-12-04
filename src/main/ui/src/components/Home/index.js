import React from 'react'
import './style.css';
import HomeButtons from '../HomeButtons';
import { userContext } from '../../context/userContext';

export default function Home() {

    const actions = <userContext.Consumer>
                      {({user}) => (<HomeButtons user={user}/>)}
                    </userContext.Consumer>;

    return (
        <div className="wrapper">
			<div className="app-description__row">
				<div className="text-description__column">
					<div className="app-name app-name_home">GreetTeam</div>
					<div className="app-description">Send a greeting card from all friends</div>
				</div>
				<div className="app-example-picture"></div>
			</div>
			{actions}
		</div>
    )
}
