import React from "react";
import Popup from "../../../UI/Popup";
import PropTypes from "prop-types";

export default function LeaveCardPopup(props) {
  if (props.isShow) {
    return (
      <Popup
        isShow={props.isShow}
        onCloseFunction={props.onCloseFunction}
        onActionFunction={props.onLeaveFunction}
        title="leaveCardButton"
        red_message="warningLeavePopup"
        question="questionLeavePopup"
        captionActionButton="leave"
      />
    );
  } else {
    return null;
  }
}

LeaveCardPopup.propTypes = {
  isShow: PropTypes.bool,
  onCloseFunction: PropTypes.func,
  onLeaveFunction: PropTypes.func,
};
