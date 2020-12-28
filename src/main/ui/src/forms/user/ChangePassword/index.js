import React from "react";
import InputTextWithLabel from "../../../components/UI/InputTextWithLabel";
import { userService } from "../../../services/userService";
import { formValidator } from "../formValidator";
import ChangePasswordCommandRow from "./ChangePasswordCommandRow";

class ChangePassword extends React.Component {
  constructor(props) {
    super(props);
    this.save_password = this.save_password.bind(this);
  }

  save_password(event) {
    event.preventDefault();
    const data = new FormData(this.form);
    const new_password = data.get("new_password");
    const check_new_password = data.get("check_new_password");
    const old_password = data.get("old_password");
    if (new_password !== check_new_password || check_new_password === "" || old_password === "" ||
        !formValidator.isValid("password", new_password)) {
      alert("data is not correct!");
    } else {
      data.delete("check_new_password");
      console.log(data);
      userService.updatePassword(old_password, new_password).then(() => {
        this.props.history.push("/profile");
      });
    }
  }

  render() {
    return (
      <div className="main-functions">
        <ChangePasswordCommandRow onSubmit={this.save_password} />
        <main className="container">
          <form
            id="password-changeable"
            className="profile-details-changeable"
            ref={(fm) => {
              this.form = fm;
            }}
          >
            <div className="profile-text-change__column no-margin-left no-border">
              <InputTextWithLabel
                columnName="old_password"
                labelText="Old Password:"
              />
              <InputTextWithLabel
                columnName="new_password"
                labelText="New Password:"
              />
              <InputTextWithLabel
                columnName="check_new_password"
                labelText="Check New Password:"
              />
            </div>
          </form>
        </main>
      </div>
    );
  }
}

export default ChangePassword;
