import React from "react";
import "./style.css";

import editImg from "../../../../assets/images/edit.jpg";
import userImg from "../../../../assets/images/user.png";

class ProfilePicture extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      file: null
    };
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    if (!event.target.files || event.target.files.length === 0) {
      this.setState({file:null});
      return;
    } 
    
    this.setState({
        file: URL.createObjectURL(event.target.files[0]),
      });
    
  }

  showPicture() { 
    console.log(this.props);
    if (this.state.file != null) {
      return <img src={this.state.file} alt="" />;
    } else if (this.props.imageProfile && this.props.imageProfile.length > 0) {
      return <img src={this.props.imageProfile} alt="" />;
    } else {
      return <img src={userImg} alt="" className="no-picture" />;
    }
  }

  render() {   
    return (
      <div className="profile-picture__row">
        <div className="profile-picture-change__column">
          <label htmlFor="profile-picture-add" name="images">
            <img src={editImg} alt="" className="profile-type-icon" />
            <input
              type="file"
              id="profile-picture-add"
              name="profileFile"
              accept="image/*"
              className="files-input"
              onChange={this.handleChange}
            />
          </label>
          <button></button>
        </div>
        <div className="profile-picture__picture none">
        {this.showPicture()}
        </div>
      </div>
    );
  }
}

export default ProfilePicture;
