import React, { Component } from "react";
import arrayMove from "array-move";
import BlockByUser from "../../components/Blocks/BlockByUser";
import { Text } from "../../components/Language/Text";
import { userContext } from "../../context/userContext";
import { cardService } from "../../services/cardService";
import CardPreviewCommandRow from "./CardPreviewCommandRow";
import FromUsers from "./FromUsers";
import "./style.css";
import CommandButton from "../../components/UI/CommandButton";

export default class CardPreview extends Component {
  constructor(props) {
    super(props);
    this.state = {
      usersWithBlocks: [],
      name: "",
      userIdCardAdmin: 0,
      cardId: 0,
      cardStatus: "",
      cardLink: "",
    };
  }

  componentDidMount() {
    const id = this.props.match.params.idCard;
    const path = this.props.location.pathname;
    if (path.startsWith("/card/")) {
      const hash = this.props.match.params.hash;
      cardService
        .getFinishedCard(id, hash)
        .then((cardData) => this.changeState(cardData));
    } else {
      cardService.getCard(id).then((cardData) => this.changeState(cardData));
    }
  }

  onSortEnd = ({ oldIndex, newIndex }) => {
    this.setState(({ usersWithBlocks }) => ({
      usersWithBlocks: arrayMove(usersWithBlocks, oldIndex, newIndex),
    }));
  };

  changeState = (cardData) => {
    this.setState({
      usersWithBlocks: this.getBlocksGropedByUser(cardData.congratulationList),
      userIdCardAdmin: cardData.user.id,
      name: cardData.name,
      cardId: cardData.id,
      cardStatus: cardData.status,
      cardLink: cardData.cardLink,
      backgroundBlocks: cardData.backgroundCongratulations,
      backgroundCardLink: cardData.backgroundImage,
    });
  };

  saveUserOrder = () => {
    const users = this.state.usersWithBlocks.map((user, index) => ({id:user.id, order:index}));
    cardService.saveUserOrder(this.state.cardId, users);
  }

  getBlocksGropedByUser = (congratulationList) => {
    let userList = [];
    let userIdList = [];
    for (let congratulation of congratulationList) {
      if (!userIdList.includes(congratulation.user.id)) {
        userList.push(congratulation.user);
        userIdList.push(congratulation.user.id);
      }
    }

    for (let user of userList) {
      let blocksByUser = congratulationList.filter(
        (block) => user.id === block.user.id
      );
      user["blocks"] = blocksByUser;
    }
    return userList;
  };

  getBlocksByUser = () => {
    return this.state.usersWithBlocks.map((user) => (
      <BlockByUser
        key={user.id}
        blocks={user.blocks}
        backgroundColor={this.state.backgroundBlocks}
      />
    ));
  };

  render() {
    const path = this.props.location.pathname;
    let cardStyle;
    if (this.state.backgroundCardLink) {
      cardStyle = {
        backgroundImage: "url(" + this.state.backgroundCardLink + ")",
      };
    } else {
      cardStyle = {
        backgroundColor: "#C1CF7A",
      };
    }
    return (
      <div className="main-functions">
        {!path.startsWith("/card/") && (
          <userContext.Consumer>
            {({ userId }) => (
              <CardPreviewCommandRow
                {...this.props}
                cardId={this.state.cardId}
                isMyCard={userId === this.state.userIdCardAdmin}
                cardStatus={this.state.cardStatus}
                cardLink={this.state.cardLink}
              />
            )}
          </userContext.Consumer>
        )}
        <main className="container" style={cardStyle}>
          <div className="card__title with-background">
            {this.state.name}
          </div>
                  
          <div className="with-background" id="card__navigation">
            <userContext.Consumer>
              {({ userId }) => (
                <FromUsers
                  users={this.state.usersWithBlocks}
                  isSort={(userId === this.state.userIdCardAdmin) && (this.state.usersWithBlocks && (this.state.usersWithBlocks.length > 1))}
                  onSortEnd={this.onSortEnd}
                  onSaveOrder={this.saveUserOrder}
                />
              )}
            </userContext.Consumer>
          </div>
          {this.getBlocksByUser()}
          <a
            className="pointer-to-navigation"
            href="#card__navigation"
          >
            <Text tid="toNavigationLabel" />
          </a>
        </main>
      </div>
    );
  }
}
