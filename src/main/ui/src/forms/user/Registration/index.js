import React from "react";
import { userService } from "../../../services/userService";
import LoginFilterPages from "../../../components/LoginFilterPages";
import "./style.css";
import "../style.css";
import { formValidator } from "../formValidator";

class Registration extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      email: "",
      firstName: "",
      lastName: "",
      login: "",
      password: "",
      password_check: "",
      errors: {
        email: "",
        firstName: "",
        lastName: "",
        login: "",
        password: "",
        password_check: "",
        formError: "",
      },
      countErrors: 0,
    };
    this.showError = this.showError.bind(this);
  }

  changeHandler = (event) => {
    let nam = event.target.name;
    let val = event.target.value;
    this.setState({ [nam]: val });

    let errors = this.state.errors;
    errors.formError = "";
    if (!formValidator.isValid(nam, val)) {
      errors[nam] = "value is not correct";
      this.setState({ [nam]: val, errors: errors });
    } else {
      errors[nam] = "";
      this.setState({ [nam]: val, errors: errors });
    }
  };

  registration = (event) => {
    event.preventDefault();
    if (this.validated()) {
      const userObject = {
        firstName: this.state.firstName,
        lastName: this.state.lastName,
        email: this.state.email,
        login: this.state.login,
        password: this.state.password,
      };
      userService
        .registerUser(userObject)
        .then(() => this.props.history.push("/login"));
    }
  };

  validated = () => {
    let errors = this.state.errors;
    let countErrors = 0;
    if (this.state.email === "") {
      errors["email"] = "is required";
      countErrors = countErrors + 1;
    }
    if (this.state.login === "") {
      errors["login"] = "is required";
      countErrors = countErrors + 1;
    }

    if (this.state.password !== this.state.password_check) {
      countErrors = countErrors + 1;
      errors["formError"] = "passwords are not equal";
    }

    if (countErrors > 0) {
      this.setState({ errors: errors });
      return false;
    } else {
      for (let key in errors) {
        if (errors[key] !== "") {
          countErrors = countErrors + 1;
        }
      }
      return countErrors === 0;
    }
  };

  showError(columnName, message) {
    let className = "error";
    if (this.state.errors[columnName].length === 0)
      className = className + " hidden";
    return <span className={className}>{message}</span>;
  }

  render() {
    return (
      <div className="main-functions">
        <LoginFilterPages page="signup" />
        <main className="container">
          <form id="profile-text">
            <input
              type="email"
              className="styled-as-input"
              name="email"
              placeholder="input e-mail"
              value={this.state.email}
              onChange={this.changeHandler}
            />
            {this.showError("email", "Email " + this.state.errors.email)}

            <div className="signature-block">
              <input
                type="text"
                className="styled-as-input"
                name="firstName"
                placeholder="input first name"
                value={this.state.firstName}
                onChange={this.changeHandler}
              />
              {this.showError(
                "firstName",
                "First name " + this.state.errors.firstName
              )}

              <input
                type="text"
                className="styled-as-input"
                name="lastName"
                placeholder="input last name"
                value={this.state.lastName}
                onChange={this.changeHandler}
              />
              {this.showError(
                "lastName",
                "Last name " + this.state.errors.lastName
              )}
            </div>

            <input
              type="text"
              className="styled-as-input"
              name="login"
              placeholder="input username"
              value={this.state.login}
              onChange={this.changeHandler}
            />
            {this.showError("login", "Login " + this.state.errors.login)}

            <input
              type="password"
              className="styled-as-input"
              name="password"
              placeholder="input password"
              value={this.state.password}
              onChange={this.changeHandler}
            />
            {this.showError(
              "password",
              "Password " + this.state.errors.password
            )}

            <input
              type="password"
              className="styled-as-input"
              name="password_check"
              placeholder="input password"
              value={this.state.password_check}
              onChange={this.changeHandler}
            />
            {this.showError(
              "password_check",
              "Password check " + this.state.errors.password_check
            )}

            <input
              type="submit"
              className="command-button"
              value="Sign Up"
              onClick={this.registration}
            />
            <br></br>
            {this.showError(
              "formError",
              "Error: " + this.state.errors.formError
            )}
          </form>
        </main>
      </div>
    );
  }
}

export default Registration;
