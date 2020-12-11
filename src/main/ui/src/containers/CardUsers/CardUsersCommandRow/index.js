import React, { useState } from "react";
import UsersResignPopup from "../../../components/Cards/Popups/UsersResignPopup";
import CommandButton from "../../../components/UI/CommandButton";
import FormAdd from "../../../forms/common/FormAdd";

export default function CardUsersCommandRow(props) {
  const [isShowPopup, setIsShowPopup] = useState(false);

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
          caption="Clear choice"
          action={props.clearChoiceFunction}
          className="command-button--yellow"
        />
        <CommandButton
          caption="Delete chosen"
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
          inputPlaceholder="input login"
          buttonCaption="Add user"
        />
      </div>
    </div>
  );
}
