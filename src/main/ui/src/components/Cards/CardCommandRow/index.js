import React, { useState } from "react";
import CommandButton from "../../UI/CommandButton";
import CommandButtonLink from "../../UI/CommandButton/CommandButtonLink";
import { cardService } from "../../../services/cardService";
import FormAdd from "../../../forms/common/FormAdd";
import LeaveCardPopup from "../Popups/LeaveCardPopup";
import DeleteCardPopup from "../Popups/DeleteCardPopup";
import FilterButton from "../../UI/FilterButton";

export default function CardCommandRow(props) {
  const [isShowPopup, setIsShowPopup] = useState(false);

  const showPopup = () => {
    setIsShowPopup(true);
  };

  const hidePopup = () => {
    setIsShowPopup(false);
  };

  const id = props.idCard;

  const leaveCard = (event) => {
    event.preventDefault();
    hidePopup();
    cardService.leaveCard(id).then(() => props.history.push("/cards/other"));
  };

  const finishCard = () => {
    if (props.isMyCard) {
      cardService.finishCard(id).then(() => props.history.push("/cards/my"));
    } else {
      props.history.push("/cards/my");
    }
  };

  const deleteCard = (event) => {
    event.preventDefault();
    hidePopup();
    cardService.deleteCard(id).then(() => props.history.push("/cards/my"));
  };

  return (
    <div className="command__row">
      <div className="filter__blocks">
        <FilterButton
          linkTo={"/edit_card/" + id + "/all_blocks"}
          caption="All Blocks"
          isActive={props.page !== "all_blocks" || props.page == null}
        />
        <FilterButton
          linkTo={"/edit_card/" + id + "/my_blocks"}
          caption="My Blocks"
          isActive={props.page !== "my_blocks"}
        />
      </div>

      <div className="actions__row">
        <CommandButtonLink
          to={"/add_block/" + id}
          className="command-button--yellow"
          caption="+ Add block"
        />
        {props.isMyCard && props.cardName.length > 0 && (
          <FormAdd
            onSubmit={props.saveNameFunction}
            inputPlaceholder=""
            buttonCaption="Save name"
            value={props.cardName}
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
          <React.Fragment>
            <CommandButton
              className="command-button--yellow"
              caption="Delete Card"
              action={showPopup}
            />
            <DeleteCardPopup
              isShow={isShowPopup}
              onCloseFunction={hidePopup}
              onDeleteFunction={deleteCard}
            />
          </React.Fragment>
        )}
        {!props.isMyCard && (
          <React.Fragment>
            <CommandButton
              className="command-button--yellow"
              caption="Leave Card"
              action={showPopup}
            />
            <LeaveCardPopup
              isShow={isShowPopup}
              onCloseFunction={hidePopup}
              onLeaveFunction={leaveCard}
            />
          </React.Fragment>
        )}
      </div>
    </div>
  );
}
