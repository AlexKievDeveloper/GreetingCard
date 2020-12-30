import React from "react";
import "../style.css";
import "./style.css";
import LoginFilterPages from "../../../components/LoginFilterPages";
import { userContext } from "../../../context/userContext";
import { Link } from "react-router-dom";
import { languageContext } from "../../../context/languageContext";
import { userService } from "../../../services/userService";
import LoginWithFacebook from "./LoginWithFacebook";
import LoginWithGoogle from "./LoginWithGoogle";

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      login: "",
      password: "",
      errorMessage: "",
    };

    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleSubmit(event, loginUser) {
    event.preventDefault();
    userService.login(this.state.login, this.state.password).then((result) => {
      this.handleResult(result, loginUser);
    });
  }

  handleResult = (result, loginUser) => {
    if (result.hasOwnProperty("message")) {
      console.log(result.message);
      this.setState({ errorMessage: result.message });
    } else {
      loginUser(result);
      console.log("go to my cards");
      this.props.history.push("/cards/my");
    }
  };

  render() {
    return (
      <div className="main-functions">
        <LoginFilterPages page="login" />
        <main className="container">
          <languageContext.Consumer>
            {({ dictionary }) => (
              <form id="profile-text">
                <input
                  type="text"
                  className="styled-as-input"
                  onChange={(event) =>
                    this.setState({ login: event.target.value })
                  }
                  placeholder={dictionary.loginPlaceholder}
                  pattern="[a-zA-Zа-яА-Я0-9ёЁІіЇїґЄє]{1,30}"
                />
                <br></br>
                <input
                  type="password"
                  className="password styled-as-input"
                  onChange={(event) =>
                    this.setState({ password: event.target.value })
                  }
                  placeholder={dictionary.passwordPlaceholder}
                />
                <br></br>
                <Link to="/forgot_password"> {dictionary.forgotPassword} </Link>
                <br></br>
                <userContext.Consumer>
                  {({ loginUser }) => (
                    <React.Fragment>
                      <input
                        type="submit"
                        className="command-button"
                        value={dictionary.loginButton}
                        onClick={(event) => {
                          this.handleSubmit(event, loginUser);
                        }}
                      />
                      <LoginWithFacebook
                        handleResult={this.handleResult}
                        appLoginUser={loginUser}
                      />

                      <LoginWithGoogle
                        handleResult={this.handleResult}
                        appLoginUser={loginUser}
                      />
                    </React.Fragment>
                  )}
                </userContext.Consumer>
                <br></br>
                <span className="error">{this.state.errorMessage}</span>
              </form>
            )}
          </languageContext.Consumer>
        </main>
      </div>
    );
  }
}

export default Login;
