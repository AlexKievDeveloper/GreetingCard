import React from "react";
import Popup from "../../../UI/Popup";
import PropTypes from "prop-types";

export default function UsersResignPopup(props) {
  if (props.isShow) {
    return (
      <Popup
        isShow={props.isShow}
        onCloseFunction={props.onCloseFunction}
        onActionFunction={props.onResignFunction}
        title="Resign users"
        red_message="When you resign user their blocks are deleted."
        question="Are you sure you want to resign choosen users?"
        captionActionButton="Resign"
      />
    );
  } else {
    return null;
  }
}

UsersResignPopup.propTypes = {
  isShow: PropTypes.bool,
  onCloseFunction: PropTypes.func,
  onResignFunction: PropTypes.func,
};
