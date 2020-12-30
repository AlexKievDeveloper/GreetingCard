import React, { useState, useEffect } from "react";
import "./style.css";
import { Text } from "../../components/Language/Text";
import LoginFilterPages from "../../components/LoginFilterPages";
import { userService } from "../../services/userService";

export default function VerifiedEmail(props) {
  const [verified, setVerified] = useState(false);
  useEffect(() => {
    userService.verifyEmail(props.match.params.hash).then((response) => {
      console.log(response);
      if (response.ok) {
        setVerified(true);
      }
    });
  });

  if (verified) {
    return (
      <div className="main-functions">
        <LoginFilterPages page="login" />
        <main className="container">
          <div className="verification-card">
            <div className="verification-card-row">
              <div className="postcard-column">
                <div className="thank-you">
                  <Text tid="thankYouVerification" />
                </div>
                <div className="greetteam-sign">GreetTeam</div>
              </div>
              <div className="postcard-column"></div>
            </div>
          </div>
        </main>
      </div>
    );
  } else {
    return null;
  }
}
