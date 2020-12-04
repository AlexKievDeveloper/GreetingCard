import React, { Component } from 'react'
import LoginFilterPages from '../../../components/LoginFilterPages';
import InputTextWithLabel from '../../../components/UI/InputTextWithLabel';
import { userService } from '../../../services/userService';
import { formValidator } from '../formValidator';
import '../style.css';

export default class RecoverPassword extends Component {
    constructor(props) {
        super(props);
        this.save_password = this.save_password.bind(this);
      }
    
      save_password(event) {
        event.preventDefault();
        const data = new FormData(this.form);
        const new_password = data.get("new_password");
        const check_new_password = data.get("check_new_password");
        if (new_password !== check_new_password || check_new_password === "" || 
            !formValidator.isValid("password", new_password)) {
          alert("data is not correct!");
        } else {
          data.delete("check_new_password");
          console.log(data);
          const path = this.props.location.pathname;
          const hash = path.replace("/recover_password", "");
          console.log("hash = " + hash)
          userService.recoverPassword(new_password, hash).then(() => {
            this.props.history.push("/login");
          });
        }
      }
    
    render() {
        return (
            <div className="main-functions">
            <LoginFilterPages page="login"/>
            <main className="container">
              <form
                id="password-changeable"
                className="profile-details-changeable"
                ref={(fm) => {
                  this.form = fm;
                }}
              >
                <div className="profile-password-change__column">
                  <InputTextWithLabel
                    columnName="new_password"
                    labelText="New Password:"
                  />
                  <InputTextWithLabel
                    columnName="check_new_password"
                    labelText="Check New Password:"
                  />
                  <div></div>
                  <input type="submit" className="command-button" value="Change password" onClick={this.save_password}></input>
                </div>
              </form>
            </main>
          </div>
        )
    }
}
