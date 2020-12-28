import React from "react";
import CommandButtonLink from "../../UI/CommandButton/CommandButtonLink";
import FilterButton from "../../UI/FilterButton";
import FinishAndDeleteLeaveButtons from "../FinishAndDeleteLeaveButtons";

export default function CardCommandRow(props) {
  const id = props.idCard;
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
        <CommandButtonLink
          to={"/preview/" + id}
          className="command-button--yellow"
          caption="Preview"
        />
        {props.isMyCard && (
          <CommandButtonLink
            to={"/card_users/" + id}
            className="command-button--no-margin-left command-button--yellow"
            caption="List of collaborators"
          />
        )}
        <FinishAndDeleteLeaveButtons
            {...props}
            isMyCard = {props.isMyCard}
            idCard = {props.idCard}/>
      </div>
    </div>
  );
}
