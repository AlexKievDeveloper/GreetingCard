import React, { Component } from "react";
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
    console.log(this.state);
    return (
      <div className="main-functions">
        <ProfileCommandRow onSubmit={this.save} />
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
                labelText="First Name:"
                valueOfColumn={this.state.firstName}
              />
              <InputTextWithLabel
                columnName="lastName"
                labelText="Last Name:"
                valueOfColumn={this.state.lastName}
              />
              <InputTextWithLabel
                columnName="login"
                labelText="Login:"
                valueOfColumn={this.state.login}
              />
            </div>
          </form>
        </main>
      </div>
    );
  }
}

export default Profile;
