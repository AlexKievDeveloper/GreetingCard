import React from "react";
import "./style.css";

export default function ColorButton(props) {
  const handleClick = (event) => {
    event.preventDefault();
    props.onClick(props.color);
  };

  const classForChosen = props.isChosen ? "chosen " : "";

  return (
    <button
      className={classForChosen + "change-button"}
      style={{ backgroundColor: props.color }}
      onClick={handleClick}
    ></button>
  );
}
