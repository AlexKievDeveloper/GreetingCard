import React, { Component } from "react";
import "./style.css";
import PropTypes from "prop-types";
import CommandButton from "../../../components/UI/CommandButton";

export default class FormAdd extends Component {
  constructor(props) {
    super(props);
    this.state = {
      valueToAdd: "",
    };
  }

  handleChange = (event) => {
    let nameInput = event.target.name;
    let valueInput = event.target.value;
    this.setState({ [nameInput]: valueInput });
  };

  doAction = (event) => {
    event.preventDefault();
    this.props.onSubmit(this.state.valueToAdd, this.props.history);
  };

  render() {
    return (
      <form id="add-form" action="#">
        <input
          type="text"
          name="valueToAdd"
          placeholder={this.props.inputPlaceholder}
          onChange={this.handleChange}
        />
        <CommandButton
          action={this.doAction}
          caption={this.props.buttonCaption}
          className="add-form-button"
        />
      </form>
    );
  }
}

FormAdd.propTypes = {
  onSubmit: PropTypes.func,
  inputPlaceholder: PropTypes.string,
  buttonCaption: PropTypes.string,
};
