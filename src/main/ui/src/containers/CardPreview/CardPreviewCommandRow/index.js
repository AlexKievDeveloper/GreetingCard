import React from "react";
import FinishAndDeleteLeaveButtons from "../../../components/Cards/FinishAndDeleteLeaveButtons";
import CommandButtonLink from "../../../components/UI/CommandButton/CommandButtonLink";

export default function CardPreviewCommandRow(props) {
  return (
    <div className="command__row hide-with-scroll">
      <div className="filter__blocks"></div>
      <div className="actions__row padding-right_75">
        <CommandButtonLink
          to={"/edit_card/" + props.cardId + "/my_blocks"}
          caption="Back to Edit"
          className="command-button--yellow"
        />
        <FinishAndDeleteLeaveButtons
          idCard={props.cardId}
          isMyCard={props.isMyCard}
        />
      </div>
    </div>
  );
}
