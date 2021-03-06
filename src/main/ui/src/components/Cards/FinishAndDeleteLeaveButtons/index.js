import React, { useState } from "react";
import { cardService } from "../../../services/cardService";
import CommandButton from "../../UI/CommandButton";
import DeleteCardPopup from "../Popups/DeleteCardPopup";
import LeaveCardPopup from "../Popups/LeaveCardPopup";

export default function FinishAndDeleteLeaveButtons(props) {
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
      cardService.finishCard(id).then(() => props.history.push(`/preview/${id}`));
    } else {
      props.history.push(`/cards/my`);
    }
  };

  const unfinishCard = () => {
    if (props.isMyCard) {
      cardService.unFinishCard(id).then(() => props.history.push("/cards/my"));
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
    <React.Fragment>
      {props.isMyCard && !props.isFinished && (
        <CommandButton
          className="command-button--yellow"
          caption="finishCardButton"
          action={finishCard}
        />
      )}
      {props.isMyCard && props.isFinished && (
        <CommandButton
          className="command-button--yellow"
          caption="unfinishCardButton"
          action={unfinishCard}
        />
      )}
      {props.isMyCard && (
        <React.Fragment>
          <CommandButton
            className="command-button--white"
            caption="deleteCardButton"
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
            className="command-button--white"
            caption="leaveCardButton"
            action={showPopup}
          />
          <LeaveCardPopup
            isShow={isShowPopup}
            onCloseFunction={hidePopup}
            onLeaveFunction={leaveCard}
          />
        </React.Fragment>
      )}
    </React.Fragment>
  );
}
