import React, { Component } from "react";
import ChangeBlocksBackground from "./ChangeBlocksBackground";
import ChangeCardBackground from "./ChangeCardBackground";
import "./style.css";
import markImg from "../../../assets/images/mark.png";
import { Text } from "../../../components/Language/Text";

export default class ChangeBackground extends Component {
  render() {
    return (
      <div className="collapsible-wrapper">
        <input id="collapsible_bg-and-dec" className="toggle" type="checkbox" />
        <label htmlFor="collapsible_bg-and-dec" className="toggle__label">
          <img src={markImg} alt="" />
        </label>
        <div className="collapsible__content">
          <div className="content_inner">
            <form className="bg-and-decorations-options__column">
              <ChangeCardBackground
                onReset={this.props.onResetCardBackground}
                onFileImageChange={this.props.onFileImageChange}
              />
              <ChangeBlocksBackground
                color={this.props.blocksColor}
                onChange={this.props.onChangeBlocksColor}
              />
              ​
              <button
                id="save-changes"
                className="dropdown-link"
                onClick={this.props.onSave}
              >
                <Text tid="save"/>
              </button>
            </form>
          </div>
        </div>
      </div>
    );
  }
}
