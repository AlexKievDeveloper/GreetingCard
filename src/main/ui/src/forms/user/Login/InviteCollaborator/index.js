import React, { useContext } from "react";
import { userContext } from "../../../../context/userContext";
import { cardService } from "../../../../services/cardService";
import LoginWithFacebook from "../LoginWithFacebook";
import LoginWithGoogle from "../LoginWithGoogle";

export default function InviteCollaborator(props) {
  const { loginUser } = useContext(userContext);

  const handleResult = (result, loginUser) => {
    if (result.hasOwnProperty("message")) {
      console.log(result.message);
    } else {
      loginUser(result);
      cardService
        .joinToCard(props.match.params.idCard, props.match.params.hash)
        .then(() => {
          console.log("go to other cards");
          props.history.push("/cards/other");
        });
    }
  };

  return (
    <main className="container">
      <div id="profile-text">
        <LoginWithFacebook
          handleResult={handleResult}
          appLoginUser={loginUser}
        />
        <LoginWithGoogle handleResult={handleResult} appLoginUser={loginUser} />
      </div>
    </main>
  );
}
