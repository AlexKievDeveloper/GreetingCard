import React, {useState} from "react";
import "./style.css";
import clockImg from "../../../assets/images/clock.png";

export default function CardTimer(props) {
  const [dateValue, setdateValue] = useState(props.dateOfFinish);  

  const handleClick = (event) => {
    event.preventDefault(); 
    props.onChangeDate(dateValue)
  }

  return (
    <React.Fragment>
      <form>
        <input type="date" value={props.dateOfFinish} onChange={(event)=>{setdateValue(event.target.value)}}/>
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
