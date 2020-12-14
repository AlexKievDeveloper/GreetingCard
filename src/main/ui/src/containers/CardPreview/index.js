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
      users: [],
      name: "",
      userIdCardAdmin: 0,
      cardId: 0,
    };
  }

  componentDidMount() {
    const id = this.props.match.params.idCard;
    cardService.getCard(id).then((cardsData) => {
      this.setState({
        users: this.getBlocksGropedByUser(cardsData.congratulationList),
        userIdCardAdmin: cardsData.user.id,
        name: cardsData.name,
        cardId: id,
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
      return this.state.users.map((user) => (<BlockByUser blocks={user.blocks}/>))
  }

  render() {
    return (
      <div class="main-functions">
        <userContext.Consumer>
          {({ userId }) => (
            <CardPreviewCommandRow
              cardId={this.state.cardId}
              isMyCard={userId === this.state.userIdCardAdmin}
            />
          )}
        </userContext.Consumer>
        <main className="container">
           <div className="card__title with-background margin-top_65">{this.state.name}</div>
           <div className="with-background" id="card__navigation">
			  <FromUsers users = {this.state.users}/>			
		   </div>
           {this.getBlocksByUser()}
           <a className="pointer-to-navigation with-background" href="#card__navigation">To Navigation</a>					
        </main>
      </div>
    );
  }
}
