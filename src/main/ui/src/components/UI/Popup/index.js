import React from "react";
import "./style.css";
import CommandButton from "../CommandButton";
import PropTypes from 'prop-types'

export default function Popup(props) {
  return (
    <div className="pop-up">
      <span
        onClick={props.onCloseFunction}
        className="pop-up__close"
        title="Close pop-up"
      >
        ×
      </span>
      <div className="pop-up__box">
        <div className="pop-up__content">
          <p className="pop-up__title">{props.title}</p>
          {props.red_message && <p className="pop-up__red">{props.red_message}</p>}
          <p>{props.question}</p>

          <div className="command__row pop-up__command">
            <CommandButton
              action={props.onCloseFunction}
              caption="Cancel"
              className="command-button--yellow"
            />
            <CommandButton
              action={props.onActionFunction}
              caption={props.captionActionButton}
              className="command-button--white"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

Popup.propTypes = {
  isShow:PropTypes.bool,
  onCloseFunction: PropTypes.func,
  title: PropTypes.string,
  red_message: PropTypes.string,
  question: PropTypes.string,
  captionActionButton: PropTypes.string,
  onActionFunction: PropTypes.func,
};
