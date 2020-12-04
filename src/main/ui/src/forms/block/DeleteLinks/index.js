import React, { Component } from "react";
import CommandRowDeleteLinks from "./CommandRowDeleteLinks";
import ElementLink from "./ElementLink";
import "./style.css";
import PropTypes from "prop-types";
import { blockService } from "../../../services/blockService";

export default class DeleteLinks extends Component {
  constructor(props) {
    super(props);
    this.state = {
      linksToDelete: [],
    };
    this.onClickDeleteCheckBox = this.onClickDeleteCheckBox.bind(this);
    this.showLinks = this.showLinks.bind(this);
  }

  handleDeleteLinks = (event) => {
    event.preventDefault();
    let listToDelete = this.state.linksToDelete;
    if (listToDelete.length === 0) {
      alert("Nothing selected to delete");
    } else {
      blockService.deleteLinks(this.props.blockId, listToDelete)
                  .then(()=> this.props.deleteLinksFunction(listToDelete))
                  .catch(error => alert(error));
    }
  };

  onClickDeleteCheckBox = (event) => {
    const nameCheckBox = event.target.name;
  
    const idStr = nameCheckBox.replace("delete_", "");
    const id = parseInt(idStr);
    let listToDelete = this.state.linksToDelete;
  
    if (event.target.checked) {
      listToDelete.push(id);
      this.setState({ linksToDelete: listToDelete });
    } else {
      const listToDeleteNew = listToDelete.filter((idLink) => idLink !== id);
      this.setState({ linksToDelete: listToDeleteNew });
    }
  };

  showPreview(array, type) {
    return array.map((src) => (
      <ElementLink key={src} isPreview={true} src={src} linkType={type} />
    ));
  }

  showLinks = () => {
      return this.props.links.map((linkItem) => (
        <ElementLink
          key={linkItem.id}
          isPreview={false}
          src={linkItem.link}
          linkType={linkItem.type}
          linkId={linkItem.id}
          onClickDeleteCheckBox={this.onClickDeleteCheckBox}
        />
      ));
  };

  render() {
    return (
      <form action="#" id="optional-elements__box">
        {this.props.links.length > 0 && (
          <CommandRowDeleteLinks deleteLinksFunction={this.handleDeleteLinks} />
        )}
        <div className="container">
          {this.showPreview(this.props.audioToAdd, "AUDIO")}
          {this.showPreview(this.props.imagesToAdd, "PICTURE")}
          {this.showLinks()}
        </div>
      </form>
    );
  }
}

DeleteLinks.propTypes = {
  audioToAdd: PropTypes.array,
  imagesToAdd: PropTypes.array,
  links: PropTypes.array,
  blockId: PropTypes.number,
  deleteLinksFunction:PropTypes.func
};
