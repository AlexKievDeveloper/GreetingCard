import React from 'react'
import '../style.css';
import LoginFilterPages from '../../../components/LoginFilterPages';
import { formValidator } from '../formValidator';
import { userService } from '../../../services/userService';

class ForgotPassword extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            email: "",
            errorMessage:""
        }

        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();
        const email = this.state.email;
        if (email === "") {
            this.setState({errorMessage:"Email is not correct"});
            return;
        }
        if (formValidator.isValid("email", email)) {
            userService.forgotPassword(email).then(alert("Link was sent. Please check your mailbox"));
        } else {
            this.setState({errorMessage:"Email is not correct"});
        }
    }

    render() {
        return (
            <div className="main-functions">
                <LoginFilterPages page="login"/>
                <main className="container">
                    <form id="profile-text">
                        <input type="text" className="styled-as-input"
                               onChange={(event) => this.setState({email: event.target.value, errorMessage:""})}
                               placeholder="input email"/>
                        <br></br>
                        <br></br>      
                        <input type="submit" className="command-button" value="Send Link" 
                               onClick={(event) => {this.handleSubmit(event)}}/>
                        <br></br>
                        <span className="error">{this.state.errorMessage}</span>
                    </form>
                </main>
            </div>
        )
    }
}

export default ForgotPassword
