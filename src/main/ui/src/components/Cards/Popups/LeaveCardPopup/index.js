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
        title="Leave Card"
        red_message="Your blocks will be deleted"
        question="Are you sure you want to leave this card?"
        captionActionButton="Leave"
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
