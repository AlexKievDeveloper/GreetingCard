import React, { Component } from "react";
import "./style.css";

import CardsFilterPages from "../../components/Cards/CardsFilterPages";
import { cardService } from "../../services/cardService";
import { userContext } from "../../context/userContext";
import CardItems from "./CardItems";

export class Cards extends Component {
  constructor(props) {
    super(props);
    this.state = {
      cards: [],
    };
    console.log('constructor Cards')

    this.deleteCard = this.deleteCard.bind(this);
  }

  componentDidMount() {
    console.log('component did mount Cards')
    cardService.getCards().then((cardsData) => {
      this.setState({ cards: cardsData });
    });
  }

  deleteCard(cardId) {
    const newCards = this.state.cards.filter((card) => card.id !== cardId);
    this.setState({ cards: newCards });
  }

  createCard(nameCard, history) {
    cardService.createCard(nameCard)
            .then((response) => response.json())
            .then((data) => history.push('/edit_card/' + data.id));
  }

  render() {
    const page = this.props.location.pathname.replace("/cards/", "") + "_cards";

    return (
      <div className="main-functions">
        <CardsFilterPages {...this.props} page={page} createCardFunction={this.createCard} />
        <main className="container-cards">
          <div className="list-of-cards">
            <userContext.Consumer>
              {({ userId }) => (
                <CardItems
                  userId={userId}
                  page={page}
                  cards={this.state.cards}
                  {...this.props}
                  onDeleteCard={this.deleteCard}
                />
              )}
            </userContext.Consumer>
          </div>
        </main>
      </div>
    );
  }
}

export default Cards;
