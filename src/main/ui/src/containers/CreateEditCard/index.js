import React, { Component } from "react";
import "./style.css";
import CardCommandRow from "../../components/Cards/CardCommandRow";
import Block from "../../components/Blocks/Block";
import { cardService } from "../../services/cardService";
import { userContext } from "../../context/userContext";
import RenameCard from "../../forms/card/RenameCard";
import ChangeBackground from "../../forms/card/ChangeBackground";

export class CreateEditCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      blocks: [],
      userIdCardAdmin: 0,
      name: "",
      backgroundBlocks: "#fff",
      backgroundCardLink: "",
      backgroundCardFile: null,
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
        backgroundBlocks: "#fff",
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

  getCongratulations = (typeBlocks, idUser, idCard, background) => {
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
        isEdit={true}
        background={background}
        onDeleteBlock={this.deleteBlock}
      />
    ));
    return congratulations;
  };

  changeBackgroundBlock = (newBackground) => {
    this.setState({ backgroundBlocks: newBackground });
  };

  resetBackgroundCard = () => {
    this.setState({ backgroundCardLink: "", backgroundCardFile: null });
  };

  handleFileImageChange = (event) => {
    if (!event.target.files || event.target.files.length === 0) {
      this.setState({ file: null });
      return;
    }

    this.setState({
      backgroundCardLink: URL.createObjectURL(event.target.files[0]),
      backgroundCardFile: event.target.files[0],
    });
  };

  handleSaveBackground = (event) => {
    event.preventDefault();
    const cardBackgroundLink =
      this.state.backgroundCardFile == null
        ? this.state.backgroundCardLink
        : "";
    const cardId = this.getIdFromPath();
    cardService.changeBackground(
      cardId,
      this.state.backgroundBlocks,
      cardBackgroundLink,
      this.state.backgroundCardFile
    );
  };

  render() {
    const cardId = this.getIdFromPath();
    const typeBlocks = this.props.match.params.typeBlocks;

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
      <userContext.Consumer>
        {({ userId }) => (
          <div className="main-functions" style={cardStyle}>
            <CardCommandRow
              {...this.props}
              idCard={cardId}
              page={typeBlocks}
              isMyCard={userId === this.state.userIdCardAdmin}
            />
            <main className="card-container" style={cardStyle}>
              {(userId === this.state.userIdCardAdmin) && 
                <React.Fragment>
                  <ChangeBackground
                    blocksColor={this.state.backgroundBlocks}
                    onFileImageChange={this.handleFileImageChange}
                    onResetCardBackground={this.resetBackgroundCard}
                    onChangeBlocksColor={this.changeBackgroundBlock}
                    onSave={this.handleSaveBackground}
                  />

                  <RenameCard
                    cardName={this.state.name}
                    saveNameFunction={this.saveName}
                  />
                </React.Fragment>
               }

              {this.getCongratulations(
                typeBlocks,
                userId,
                cardId,
                this.state.backgroundBlocks
              )}
            </main>
          </div>
        )}
      </userContext.Consumer>
    );
  }
}

export default CreateEditCard;
