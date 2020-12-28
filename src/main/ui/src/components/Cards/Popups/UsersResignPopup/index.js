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
        title="resignUsers"
        red_message="warningResignPopup"
        question="questionResignPopup"
        captionActionButton="resign"
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
