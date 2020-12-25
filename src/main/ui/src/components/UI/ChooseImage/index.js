import React from "react";
import "./style.css";
import PropTypes from "prop-types";

import addImg from "../../../assets/images/add.png";
import pictureImg from "../../../assets/images/picture.png";
import config from "../../../services/config";

export default function ChooseImage(props) {
  const classForLabel = "adder adder__" + props.place;
  const classForPictureImg =
    "element-type-icon element-type-icon__" + props.size;
  const classForAddImg = "element-adder-icon element-adder-icon__" + props.size;

  return (
    <label htmlFor="image-files" className={classForLabel} name="images">
      <img src={pictureImg} alt="" className={classForPictureImg} />
      <img src={addImg} alt="" className={classForAddImg} />

      <input
        type="file"
        id="image-files"
        name="image-files"
        accept={config.acceptedFileImage}
        className="files-input"
        onChange={props.handleFileImagesChange}
        multiple={props.isMultiple}
      />
    </label>
  );
}

ChooseImage.propTypes = {
  place: PropTypes.string,
  size: PropTypes.string,
  handleFileImagesChange: PropTypes.func,
  isMultiple: PropTypes.bool,
};
