import React, {useContext} from "react";
import CommandButton from "../../../components/UI/CommandButton";
import { languageContext } from "../../../context/languageContext";

export default function CommandRowDeleteLinks(props) {
  const { dictionary } = useContext(languageContext);

  return (
    <div className="command__row">
      <div className="filter__blocks"></div>
      <div className="actions__row">
        <CommandButton
          className="command-button--yellow"
          caption={dictionary.deleteSelected}
          action={props.deleteLinksFunction}
        />
      </div>
    </div>
  );
}
