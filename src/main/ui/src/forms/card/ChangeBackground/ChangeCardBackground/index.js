import React from "react";
import "./style.css";

import resetImg from "../../../../assets/images/reset.png";
import ChooseImage from "../../../../components/UI/ChooseImage";
import { Text } from "../../../../components/Language/Text";

export default function ChangeCardBackground(props) {
  const handleReset = (event) => {
    event.preventDefault();
    props.onReset();
  };

  return (
    <div className="card-change__column">
      <p>
        <Text tid="card" />
      </p>
      <div className="card-change">
        <ChooseImage
          place="background"
          size="small"
          handleFileImagesChange={props.onFileImageChange}
          isMultiple={false}
        />
        <button
          id="reset_card"
          className="button_with_image"
          onClick={handleReset}
        >
          <img
            src={resetImg}
            alt=""
            style={{ height: "14px", marginRight: "8px" }}
          />
          <Text tid="reset" />
        </button>
      </div>
    </div>
  );
}
