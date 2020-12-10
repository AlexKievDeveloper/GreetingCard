import React from "react";
import Popup from "../../../UI/Popup";
import PropTypes from "prop-types";

export default function DeleteCardPopup(props) {
  if (props.isShow) {
    <Popup
      onCloseFunction={props.onCloseFunction}
      onActionFunction={props.onDeleteFunction}
      title="Delete Card"
      question="Are you sure you want to delete this card?"
      captionActionButton="Delete"
    />;
  } else {
    return null;
  }
}

DeleteCardPopup.propTypes = {
  isShow: PropTypes.bool,
  onCloseFunction: PropTypes.func,
  onDeleteFunction: PropTypes.func,
};
