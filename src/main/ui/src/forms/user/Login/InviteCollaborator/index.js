import React, { useContext } from "react";
import { userContext } from "../../../../context/userContext";
import { cardService } from "../../../../services/cardService";
import LoginWithFacebook from "../LoginWithFacebook";
import LoginWithGoogle from "../LoginWithGoogle";

export default function InviteCollaborator(props) {
  const { loginUser } = useContext(userContext);

  const idCard = props.match.params.idCard;
  const editLink = `/edit_card/${idCard}/my_blocks`;
  const handleResult = (result, loginUser) => {
    if (result.hasOwnProperty("message")) {
      console.log(result.message);
    } else {
      loginUser(result);
      cardService
        .joinToCard(idCard, props.match.params.hash)
        .then(() => {
          console.log("go to other cards");
          props.history.push(editLink);
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
