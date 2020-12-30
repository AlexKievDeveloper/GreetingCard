import React from "react";
import { Text } from "../Language/Text";
import "./style.css";

export default function Notification(props) {
  const className = "overlay" + (props.isShow ? " visible" : " hidden");
  return (
    <div className={className}>
      <div className="popup">
        <div className="upper-row">
          <h2>
            <Text tid="newNotification" />
          </h2>
          <span className="close" onClick={props.onClose}>
            &times;
          </span>
        </div>
        <br></br>
        <div className="content">{props.message}</div>
      </div>
    </div>
  );
}
