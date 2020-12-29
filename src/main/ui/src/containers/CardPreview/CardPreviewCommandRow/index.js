import React, {useContext} from "react";
import FinishAndDeleteLeaveButtons from "../../../components/Cards/FinishAndDeleteLeaveButtons";
import CommandButton from "../../../components/UI/CommandButton";
import CommandButtonLink from "../../../components/UI/CommandButton/CommandButtonLink";
import { languageContext } from "../../../context/languageContext";

export default function CardPreviewCommandRow(props) {
  const { dictionary } = useContext(languageContext);
  const isFinished = props.cardStatus === "ISOVER";

  const copyLink = () => { navigator.clipboard.writeText(props.cardLink);}
  
  return (
    <div className="command__row hide-with-scroll">
      <div className="filter__blocks"></div>
      <div className="actions__row padding-right_75">
      {(props.isMyCard && isFinished) && <CommandButton
          action={copyLink()}
          caption={dictionary.getCardLinkButton}
          className="command-button--yellow"
        />}
        {(props.isMyCard || !isFinished) && <CommandButtonLink
          to={"/edit_card/" + props.cardId + "/my_blocks"}
          caption={dictionary.backToEditButton}
          className="command-button--yellow"
        />}
        <FinishAndDeleteLeaveButtons
          {...props}
          idCard={props.cardId}
          isMyCard={props.isMyCard}
          isFinished={isFinished}
        />
      </div>
    </div>
  );
}
