import React, { useState, useContext } from "react";
import UsersResignPopup from "../../../components/Cards/Popups/UsersResignPopup";
import CommandButton from "../../../components/UI/CommandButton";
import { languageContext } from "../../../context/languageContext";
import FormAdd from "../../../forms/common/FormAdd";
import { cardService } from "../../../services/cardService";

export default function CardUsersCommandRow(props) {
  const [isShowPopup, setIsShowPopup] = useState(false);
  const { dictionary } = useContext(languageContext);

  const showPopup = () => {
    setIsShowPopup(true);
  };

  const hidePopup = () => {
    setIsShowPopup(false);
  };

  const deleteUsers = (event) => {
    event.preventDefault();
    hidePopup();
    props.deleteUsersFunction();
  };

  const generateLink = (event) => {
    event.preventDefault();
    cardService
      .getInvitationLink(props.cardId)
      .then((response) => {
        navigator.clipboard.writeText(response.link);
        alert("link is copied");
      });
  };

  return (
    <div className="command__row">
      <div className="filter"></div>
      <div className="actions__row">
        <CommandButton
          caption="generateInviteLink"
          action={generateLink}
          className="command-button--yellow"
        />
        <CommandButton
          caption="clearChoiceButton"
          action={props.clearChoiceFunction}
          className="command-button--yellow"
        />
        <CommandButton
          caption="deleteChosenButton"
          action={showPopup}
          className="command-button--white"
        />
        <UsersResignPopup
          isShow={isShowPopup}
          onCloseFunction={hidePopup}
          onResignFunction={deleteUsers}
        />
        <FormAdd
          {...props}
          onSubmit={props.addUserFunction}
          inputPlaceholder={dictionary.inputLoginPlaceholder}
          buttonCaption="addUserButton"
        />
      </div>
    </div>
  );
}
