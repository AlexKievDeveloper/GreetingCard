import React, { Component } from "react";
import CommandButton from "../../../components/UI/CommandButton";
import InputTextWithLabel from "../../../components/UI/InputTextWithLabel";
import { userService } from "../../../services/userService";
import { formValidator } from "../formValidator";
import ProfileCommandRow from "./ProfileCommandRow";
import ProfilePicture from "./ProfilePicture";

class Profile extends Component {
  constructor(props) {
    super(props);
    this.state = {
      id: 0,
      firstName: "",
      lastName: "",
      login: "",
      pathToPhoto: "",
    };
    this.save = this.save.bind(this);
  }

  componentDidMount() {
    userService.getProfile().then((user) =>
      this.setState({
        id: user.id,
        firstName: user.firstName,
        lastName: user.lastName,
        login: user.login,
        pathToPhoto: user.pathToPhoto,
      })
    );
  }

  save(event) {
    event.preventDefault();
    const data = new FormData(this.form);
    data.append("id", this.state.id);
    data.append("pathToPhoto", this.state.pathToPhoto);
    const firstName = data.get("firstName");
    const lastName = data.get("lastName");
    const login = data.get("login");
    if (
      formValidator.isValid("firstName", firstName) &&
      formValidator.isValid("lastName", lastName) &&
      formValidator.isValid("login", login) &&
      login.length > 0
    ) {
      userService.updateProfile(data).then((response) => {
        if (response.ok) {
          alert("Profile was successfully changed");
        } else {
          alert("Error:" + response.statusText);
        }
      });
    } else {
      alert("Could you please check data");
    }
  }

  render() {
    if (this.state.login.length > 0) {
      return (
        <div className="main-functions">
          <ProfileCommandRow />
          <main className="container">
            <form
              className="profile-details-changeable"
              ref={(fm) => {
                this.form = fm;
              }}
            >
              <ProfilePicture imageProfile={this.state.pathToPhoto} />
              <div className="profile-text-change__column">
                <InputTextWithLabel
                  columnName="firstName"
                  labelText="firstName"
                  valueOfColumn={this.state.firstName}
                />
                <InputTextWithLabel
                  columnName="lastName"
                  labelText="lastName"
                  valueOfColumn={this.state.lastName}
                />
                <InputTextWithLabel
                  columnName="login"
                  labelText="loginLabel"
                  valueOfColumn={this.state.login}
                />
                <CommandButton
                  className="command-button--yellow border_black05rad3"
                  caption="saveChanges"
                  action={this.save}
                />
              </div>
            </form>
          </main>
        </div>
      );
    } else {
      return null;
    }
  }
}

export default Profile;
