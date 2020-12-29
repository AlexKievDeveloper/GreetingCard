import React from "react";
import "./style.css";

import enImg from "../../../../assets/images/eng.svg";
import uaImg from "../../../../assets/images/ua.svg";

export default function LanguageButton(props) {
  const handleClick = () => {
    if (!props.isChoosen) {
      props.onClick(props.language);
    }
  };

  let classForLink = "language-link";
  if (props.isChoosen) {
    classForLink += " choosen";
  }

  let srcImg;
  if (props.language === "EN") {
    srcImg = enImg;
  } else {
    srcImg = uaImg;
  }

  return (
    <a href="#top" className={classForLink} onClick={handleClick}>
      <img src={srcImg} alt="" />
    </a>
  );
}
