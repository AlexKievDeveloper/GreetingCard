import React, { Component } from "react";
import "./style.css";
import CardCommandRow from "../../components/Cards/CardCommandRow";
import Block from "../../components/Blocks/Block";
import { cardService } from "../../services/cardService";
import { userContext } from "../../context/userContext";

export class CreateEditCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      blocks: [],
      userIdCardAdmin: 0,
      name: "",
    };

    this.deleteBlock = this.deleteBlock.bind(this);
  }

  componentDidMount() {
    const id = this.getIdFromPath();
    cardService.getCard(id).then((cardsData) => {
      this.setState({
        blocks: cardsData.congratulationList,
        userIdCardAdmin: cardsData.user.id,
        name: cardsData.name,
      });
    });
  }

  getIdFromPath = () => this.props.match.params.id;

  deleteBlock(idBlock) {
    const newBlocks = this.state.blocks.filter((block) => block.id !== idBlock);
    this.setState({ blocks: newBlocks });
  }

  saveName = (newName) => {
    const idCard = this.getIdFromPath();
    if (newName !== this.state.name) {
      cardService
        .updateName(idCard, newName)
        .then(() => this.setState({ name: newName }));
    } else {
      alert("Enter new name for card");
    }
  };

  getCongratulations = (typeBlocks, idUser, idCard) => {
    let congratulationsToShow = this.state.blocks;

    if (typeBlocks === "my_blocks") {
      congratulationsToShow = congratulationsToShow.filter(
        (block) => block.user.id === idUser
      );
    }

    let congratulations = congratulationsToShow.map((block) => (
      <Block
        {...this.props}
        key={block.id}
        block={block}
        idCard={idCard}
        onDeleteBlock={this.deleteBlock}
      />
    ));
    return congratulations;
  };

  render() {
    const cardId = this.getIdFromPath();
    const typeBlocks = this.props.match.params.typeBlocks;

    return (
      <userContext.Consumer>
        {({ userId }) => (
          <div className="main-functions">
            <CardCommandRow
              {...this.props}
              idCard={cardId}
              page={typeBlocks}
              cardName={this.state.name}
              saveNameFunction={this.saveName}
              isMyCard={userId === this.state.userIdCardAdmin}
            />
            <main className="card-container">
              {this.getCongratulations(typeBlocks, userId, cardId)}
            </main>
          </div>
        )}
      </userContext.Consumer>
    );
  }
}

export default CreateEditCard;
