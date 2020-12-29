import React from "react";
import { Text } from "../../../components/Language/Text";
import User from "../User";
import "./style.css";
import {
  sortableContainer,
  sortableElement,
  sortableHandle,
} from "react-sortable-hoc";

export default function FromUsers(props) {
  const DragHandle = sortableHandle(() => <span>::</span>);
  const SortableItem = sortableElement(({ userWithBlocks }) => (
    <li>
      <User key={userWithBlocks.id} user={userWithBlocks} />
      <DragHandle />
    </li>
  ));

  const SortableContainer = sortableContainer(({ children }) => {
    return <ul>{children}</ul>;
  });

  const getUsers = () => {
    return props.users.map((userWithBlocks) => (
      <li>
        <User key={userWithBlocks.id} user={userWithBlocks} />
      </li>
    ));
  };

  const getUsersWithSwapPosibility = () => {
    return (
      <SortableContainer onSortEnd={props.onSortEnd} useDragHandle>
        {props.users.map((userWithBlocks, index) => (
          <SortableItem
            key={userWithBlocks.id}
            index={index}
            userWithBlocks={userWithBlocks}
          />
        ))}
      </SortableContainer>
    );
  };

  return (
    <div className="card__navigation">
      <Text tid="fromLabel" />
      <div className="card__contributors">
        {!props.isMyCard && <ul>{getUsers()}</ul>}
        {props.isMyCard && getUsersWithSwapPosibility()}
      </div>
    </div>
  );
}
