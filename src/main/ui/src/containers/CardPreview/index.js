import React, { Component } from "react";
import BlockByUser from "../../components/Blocks/BlockByUser";
import { userContext } from "../../context/userContext";
import { cardService } from "../../services/cardService";
import CardPreviewCommandRow from "./CardPreviewCommandRow";
import FromUsers from "./FromUsers";
import "./style.css";

export default class CardPreview extends Component {
  constructor(props) {
    super(props);
    this.state = {
      usersWithBlocks: [],
      name: "",
      userIdCardAdmin: 0,
      cardId: 0,
      cardStatus:"",
      cardLink:""
    };
  }

  componentDidMount() {
    const id = this.props.match.params.idCard;
    cardService.getCard(id).then((cardData) => {
      this.setState({
        usersWithBlocks: this.getBlocksGropedByUser(cardData.congratulationList),
        userIdCardAdmin: cardData.user.id,
        name: cardData.name,
        cardId: id,
        cardStatus:cardData.status,
        cardLink:cardData.cardLink
      });
    });
  }

  getBlocksGropedByUser = (congratulationList) => {
    let userList = [];
    let userIdList =[];
    for (let congratulation of congratulationList) {
      if (!userIdList.includes(congratulation.user.id)) {
        userList.push(congratulation.user);
        userIdList.push(congratulation.user.id);
      }
    }

    console.log(userList);

    for (let user of userList) {
      let blocksByUser = congratulationList.filter(
        (block) => user.id === block.user.id
      );
      user["blocks"] = blocksByUser;
    }
    return userList;
  };

  getBlocksByUser = () => {
      return this.state.usersWithBlocks.map((user) => (<BlockByUser blocks={user.blocks}/>))
  }

  render() {
    return (
      <div class="main-functions">
        <userContext.Consumer>
          {({ userId }) => (
            <CardPreviewCommandRow
              cardId={this.state.cardId}
              isMyCard={userId === this.state.userIdCardAdmin}
              cardStatus={this.state.cardStatus}
              cardLink={this.state.cardLink}
            />
          )}
        </userContext.Consumer>
        <main className="container">
           <div className="card__title with-background margin-top_65">{this.state.name}</div>
           <div className="with-background" id="card__navigation">
			  <FromUsers users = {this.state.usersWithBlocks}/>			
		   </div>
           {this.getBlocksByUser()}
           <a className="pointer-to-navigation with-background" href="#card__navigation">To Navigation</a>					
        </main>
      </div>
    );
  }
}
