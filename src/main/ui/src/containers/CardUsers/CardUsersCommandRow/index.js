import React, { useState, useContext } from "react";
import UsersResignPopup from "../../../components/Cards/Popups/UsersResignPopup";
import CommandButton from "../../../components/UI/CommandButton";
import { languageContext } from "../../../context/languageContext";
import FormAdd from "../../../forms/common/FormAdd";

export default function CardUsersCommandRow(props) {
  const [isShowPopup, setIsShowPopup] = useState(false);
  const { dictionary } = useContext(languageContext);


  const showPopup = () => {
    console.log('showPopup');  
    setIsShowPopup(true);
  };

  const hidePopup = () => {
    console.log('hidePopup')    
    setIsShowPopup(false);
  };

  const deleteUsers = (event) => {
      event.preventDefault();
      hidePopup();
      props.deleteUsersFunction();
  }

  return (
    <div className="command__row">
      <div className="filter"></div>
      <div className="actions__row">
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