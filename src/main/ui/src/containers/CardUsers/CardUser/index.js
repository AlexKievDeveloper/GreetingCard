import React from "react";
import "./style.css";

import userImg from "../../../assets/images/user.png";

export default function CardUser(props) {
  function showPicture(pathToPicture) {
    if (pathToPicture && pathToPicture.length > 0) {
      return (
        <div className="user-profile-picture">
          <img src={pathToPicture} alt=""/>
        </div>
      );
    } else {
      return (
        <div className="user-profile-picture none">
          <img src={userImg} alt="" className="no-picture" />
        </div>
      );
    }
  }

  const idCheckBox = 'delete_' + props.user.id;
  const firstName = props.user.firstName ? props.user.firstName : '';
  const lastName = props.user.lastName ? props.user.lastName : '';
  const pathToPhoto = props.user.pathToPhoto ? props.user.pathToPhoto : '';
  return (
    <div className="user-and-checkbox-block">
      <div className="user__row">
        {showPicture(pathToPhoto)}
        <div className="user-text-details__row">
          <div className="user-styled-as-input user-signature">
            {firstName + " " + lastName}
          </div>
          <div className="user-styled-as-input login">{props.user.login}</div>
        </div>
        <div className="number-of-blocks">
          {props.user.countCongratulations}
        </div>
      </div>
      <div className="checkbox-for-collaborator">
        <input type="checkbox" id={idCheckBox} name={idCheckBox} checked = {props.checked} onChange={props.onClickDeleteCheckBox} />
        <label htmlFor={idCheckBox}>Delete</label>
      </div>
    </div>
  );
}
