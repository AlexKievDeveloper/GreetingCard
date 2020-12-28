import React from "react";
import "./style.css";
import PropTypes from "prop-types";
import BlockLink from "../../../../components/Blocks/BlockLink";

export default function ElementLink(props) {
  let element = <BlockLink type={props.linkType} link={props.src} />;

  if (!props.isPreview) {
    const idCheckBox = "delete_" + props.linkId;
    element = (
      <React.Fragment>
        <label htmlFor={idCheckBox}>{element}</label>
        <input
          type="checkbox"
          id={idCheckBox}
          name={idCheckBox}
          onChange={props.onClickDeleteCheckBox}
        />
      </React.Fragment>
    );
  }

  return <div className="element-with-checkbox">{element}</div>;
}

ElementLink.propTypes = {
  isPreview: PropTypes.bool,
  src: PropTypes.string,
  linkType: PropTypes.string,
  linkId: PropTypes.number,
  onClickDeleteCheckBox: PropTypes.func,
};
