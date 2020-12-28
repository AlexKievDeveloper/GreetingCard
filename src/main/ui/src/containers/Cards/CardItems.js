import React from "react";
import Card from "../../components/Cards/Card";

export default function CardItems(props) {
  let cards = props.cards;

  if (props.page === "my_cards") {
    cards = cards.filter((card) => card.user.id === props.userId);
  } else if (props.page === "other_cards") {
    cards = cards.filter((card) => card.user.id !== props.userId);
  }

  return cards.map((card) => (
    <Card
      key={card.id}
      card={card}
      {...props}
      isMyCard={card.user.id === props.userId}
      onDeleteCard={props.onDeleteCard}
    />
  ));
}
