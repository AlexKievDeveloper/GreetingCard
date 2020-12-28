import React from "react";
import ColorButton from "../../../../components/UI/ColorButton";
import "./style.css";

export default function ChangeBlocksBackground(props) {
  return (
    <div className="block-change__column">
      <p>Blocks</p>
      <div>
        <div className="choose__row">
          <ColorButton
            color="#4f4fb5"
            isChosen={"#4f4fb5" === props.color}
            onClick={props.onChange}
          />
          <ColorButton
            color="#B40EB9"
            isChosen={"#B40EB9" === props.color}
            onClick={props.onChange}
          />
          <ColorButton
            color="#fff"
            isChosen={"#fff" === props.color}
            onClick={props.onChange}
          />
        </div>
      </div>
    </div>
  );
}
