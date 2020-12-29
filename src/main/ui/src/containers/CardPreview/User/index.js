import React from "react";
import "./style.css";

export default function User(props) {
  return (
    <a href={"#blocks__column_Author" + props.user.id}>
      {props.user.firstName + " " + props.user.lastName}
      {props.user.pathToPhoto && (
        <div className="profile-picture">
          <img src={props.user.pathToPhoto} alt="" />
        </div>
      )}
    </a>
  );
}
