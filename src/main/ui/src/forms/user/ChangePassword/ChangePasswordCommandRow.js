import React from "react";
import CommandButton from "../../../components/UI/CommandButton";
import CommandButtonLink from "../../../components/UI/CommandButton/CommandButtonLink";

export default function ChangePasswordCommandRow(props) {
  
  return (
    <div className="command__row">
      <div className="filter"></div>
      <div className="actions__row">
        <CommandButton
          className="command-button--yellow"
          caption="savePassword"
          form="password-changeable"
          action={props.onSubmit}
        />
        <CommandButtonLink
          to="/profile"
          className="command-button--white"
          caption="cancel"
        />
      </div>
    </div>
  );
}
