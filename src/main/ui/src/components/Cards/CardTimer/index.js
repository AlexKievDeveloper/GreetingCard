import React, { useState, useEffect } from "react";
import "./style.css";
import clockImg from "../../../assets/images/clock.png";

export default function CardTimer(props) {
  const [dateValue, setdateValue] = useState(null);

  const handleClick = (event) => {
    event.preventDefault();
    props.onChangeDate(dateValue);
  };

  useEffect(() => {
    let dateOfFinish = props.dateOfFinish;
    if (dateOfFinish) {
      setdateValue(
        dateOfFinish.year + "-" +
        dateOfFinish.monthValue.toString().padStart(2, "0") +
          "-" +
          dateOfFinish.dayOfMonth.toString().padStart(2, "0")
      );
    }
  }, [props.dateOfFinish]);

  return (
    <React.Fragment>
      <form>
        <input
          type="date"
          value={dateValue}
          onChange={(event) => {
            setdateValue(event.target.value);
          }}
        />
      </form>
      <a
        href="#"
        className="timer-button command-button--yellow command-button"
        onClick={handleClick}
      >
        <img src={clockImg} alt="" />
      </a>
    </React.Fragment>
  );
}
