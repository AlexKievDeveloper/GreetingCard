import React from "react";
import CommandButton from "../../UI/CommandButton";
import CommandButtonLink from "../../UI/CommandButton/CommandButtonLink";
import { cardService } from "../../../services/cardService";
import FormAdd from "../../../forms/common/FormAdd";

export default function CardCommandRow(props) {
  const id = props.idCard;

  const leaveCard = () => {
    cardService.leaveCard(id).then(() => props.history.push("/cards/my"));
  };

  const finishCard = () => {
    if (props.isMyCard) {
      cardService.finishCard(id).then(() => props.history.push("/cards/my"));
    } else {
        props.history.push("/cards/my");  
    }
  };

  const deleteCard = () => {
    cardService.deleteCard(id).then(() => props.history.push("/cards/my"));
  };

  return (
    <div className="command__row">
      <div className="filter__blocks"></div>

      <div className="actions__row">
        <CommandButtonLink
          to={"/add_block/" + id}
          className="command-button--yellow"
          caption="+ Add block"
        />
        {props.isMyCard && (props.cardName.length > 0)  &&(
          <FormAdd onSubmit={props.saveNameFunction}
                   inputPlaceholder=""
                   buttonCaption = "Save name"
                   value = {props.cardName}
          />
        )}
        {props.isMyCard && (
          <CommandButtonLink
            to={"/card_users/" + id}
            className="command-button--no-margin-left command-button--yellow"
            caption="List of collaborators"
          />
        )}
        {props.isMyCard && (
        <CommandButton
          className="command-button--white"
          caption="Finish Card"
          action={finishCard}
        />
        )}
        {props.isMyCard && (
          <CommandButton
            className="command-button--yellow"
            caption="Delete Card"
            action={deleteCard}
          />
        )}
        {!props.isMyCard && (
          <CommandButton
            className="command-button--yellow"
            caption="Leave Card"
            action={leaveCard}
          />
        )}
      </div>
    </div>
  );
}
