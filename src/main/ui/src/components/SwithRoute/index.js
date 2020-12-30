import React from 'react'
import {
    Switch,
    Route
} from 'react-router-dom';
import {Cards} from '../../containers/Cards';
import CreateEditBlock from '../../containers/CreateEditBlock';
import CreateEditCard from '../../containers/CreateEditCard';
import Home from '../Home';
import Login from '../../forms/user/Login';
import Registration from '../../forms/user/Registration';
import Profile from '../../forms/user/Profile';
import ChangePassword from '../../forms/user/ChangePassword';
import CardUsers from '../../containers/CardUsers';
import ForgotPassword from '../../forms/user/ForgotPassword';
import RecoverPassword from '../../forms/user/RecoverPassword';
import CardPreview from '../../containers/CardPreview';
import VerifiedEmail from '../../containers/VerifiedEmail';
import InviteCollaborator from '../../forms/user/Login/InviteCollaborator';


export default function SwitchRoute(props) {

    const isLoggedIn = (typeof props.userName !== 'undefined') && (props.userName != null) && (props.userName !== '');

    if (isLoggedIn)
        return (
            <Switch>
                <Route path="/home" component={Home}/>
                <Route path="/login" component={Login}/>
                <Route path="/signup" component={Registration}/>
                <Route path="/profile" component={Profile}/>
                <Route path="/card_users/:id" component={CardUsers}/>
                <Route path="/change_password" component={ChangePassword}/>
                <Route path="/cards/:type" component={Cards}/>
                <Route path="/edit_card/:id/:typeBlocks" component={CreateEditCard}/>
                <Route path="/add_block/:idCard" component={CreateEditBlock}/>
                <Route path="/edit_block/:idBlock" component={CreateEditBlock}/>
                <Route path="/preview/:idCard" component={CardPreview}/>
                <Route path="/card/:idCard/card_link/:hash" component={CardPreview}/>
                <Route path="/user/verification/:hash" component={VerifiedEmail}/>
                <Route path="/invite_link/:idCard/code/:hash" component={InviteCollaborator}/>
            </Switch>
        )
    else
        return (
            <Switch>
                <Route exact path="/" component={Home}/>
                <Route path="/home" component={Home}/>
                <Route path="/forgot_password" component={ForgotPassword}/>
                <Route path="/recover_password/:hash" component={RecoverPassword}/>
                <Route path="/login" component={Login}/>
                <Route path="/signup" component={Registration}/>
                <Route path="/profile" component={Login}/>
                <Route path="/card_users/:id" component={Login}/>
                <Route path="/change_password" component={Login}/>
                <Route path="/cards/:type" component={Login}/>
                <Route path="/edit_card/:id/:typeBlock" component={Login}/>
                <Route path="/add_block/:idCard" component={Login}/>
                <Route path="/edit_block/:idBlock" component={Login}/>
                <Route path="/preview/:idCard" component={Login}/>
                <Route path="/card/:idCard/card_link/:hash" component={CardPreview}/>
                <Route path="/user/verification/:hash" component={VerifiedEmail}/>
                <Route path="/invite_link/:idCard/code/:hash" component={InviteCollaborator}/>
            </Switch>
        )
}
