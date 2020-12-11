import React, { useState } from "react";
import "./style.css";

import { Link } from "react-router-dom";
import editImg from "../../../assets/images/edit.jpg";
import deleteImg from "../../../assets/images/delete.png";
import leaveImg from "../../../assets/images/leave.png";
import { cardService } from "../../../services/cardService";
import DeleteCardPopup from "../Popups/DeleteCardPopup";
import LeaveCardPopup from "../Popups/LeaveCardPopup";

export default function CardActions(props) {
  const [isShowPopup, setIsShowPopup] = useState(false);

  const showPopup = () => {
    setIsShowPopup(true);
  };

  const hidePopup = () => {
    setIsShowPopup(false);
  };

  const deleteCard = (event) => {
    event.preventDefault();  
    hidePopup();  
    cardService
      .deleteCard(props.id)
      .then(() => props.onDeleteCard(props.id))
      .then(() => props.history.push("/cards/my"));
  };

  const leaveCard = (event) => {
    event.preventDefault();    
    hidePopup();  
    cardService
      .leaveCard(props.id)
      .then(() => props.onDeleteCard(props.id))
      .then(() => props.history.push("/cards/other"));
  };

  const editLink = "/edit_card/" + props.id;

  return (
    <div className="actions__column">
      <Link to={editLink}>
        <img className="card-action edit" src={editImg} alt="" />
      </Link>
      {props.isMyCard && (
        <React.Fragment>
          <Link to="/cards/my" onClick={showPopup}>
            <img src={deleteImg} className="card-action" alt="" />
          </Link>
          <DeleteCardPopup
            isShow={isShowPopup}
            onCloseFunction={hidePopup}
            onDeleteFunction={deleteCard}
          />
        </React.Fragment>
      )}
      {!props.isMyCard && (
        <React.Fragment>
          <Link to="/cards/other" onClick={showPopup}>
            <img src={leaveImg} className="card-action" alt="" />
          </Link>
          <LeaveCardPopup
            isShow={isShowPopup}
            onCloseFunction={hidePopup}
            onLeaveFunction={leaveCard}
          />
        </React.Fragment>
      )}
    </div>
  );
}
