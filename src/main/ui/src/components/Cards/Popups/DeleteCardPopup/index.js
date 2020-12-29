import React from "react";
import Popup from "../../../UI/Popup";
import PropTypes from "prop-types";

export default function DeleteCardPopup(props) {
  
  if (props.isShow) {
    return (<Popup
      onCloseFunction={props.onCloseFunction}
      onActionFunction={props.onDeleteFunction}
      title="deleteCardButton"
      question="questionDeletePopup"
      captionActionButton="delete"
    />);
  } else {
    return null;
  }
}

DeleteCardPopup.propTypes = {
  isShow: PropTypes.bool,
  onCloseFunction: PropTypes.func,
  onDeleteFunction: PropTypes.func,
};
