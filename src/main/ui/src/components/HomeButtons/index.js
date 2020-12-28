import React from "react";
import "./style.css";
import { Link } from "react-router-dom";
import config from "../../services/config";
import { Text } from "../Language/Text";

export default function HomeButtons(props) {
  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(config.greetingUrl);
    } catch (err) {
      console.log("Failed to copy!");
    }
  };

  if (props.user !== "" && props.user != null) {
    return (
      <div className="call-to-action__row">
        <Link
          to="/home"
          className="call-to-action yellow-button"
          onClick={() => handleCopy()}
        >
          <Text tid="shareLinkButton" />
        </Link>
        <Link to="/cards/my" className="call-to-action white">
          <Text tid="homeMyCardsButton" />
        </Link>
      </div>
    );
  } else {
    return (
      <div className="call-to-action__row">
        <Link to="/signup" className="call-to-action yellow-button">
          <Text tid="signUpButton" />
        </Link>
        <Link to="/login" className="call-to-action white">
          <Text tid="loginButton" />
        </Link>
      </div>
    );
  }
}
