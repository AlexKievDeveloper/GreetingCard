import React from "react";
import "./style.css";
import {
  sortableContainer,
  sortableElement,
  sortableHandle,
} from "react-sortable-hoc";
import { Text } from "../../../components/Language/Text";
import grabberImg from "../../../assets/images/grabber-icon.png";
import CommandButton from "../../../components/UI/CommandButton";

export default function FromUsers(props) {
  const DragHandle = sortableHandle(() => (
    <img src={grabberImg} alt="" className="grabber" />
  ));

  const getUser = (user, isSorted) => {
    return (
      <a href={"#blocks__column_Author" + user.id} className="collaborator-row">
        {isSorted && <DragHandle />}
        {user.firstName + " " + user.lastName}
        {user.pathToPhoto && (
          <div className="profile-picture">
            <img src={user.pathToPhoto} alt="" />
          </div>
        )}
      </a>
    );
  };

  const SortableItem = sortableElement(({ userWithBlocks }) => (
    <li key={userWithBlocks.id}>{getUser(userWithBlocks, true)}</li>
  ));

  const SortableContainer = sortableContainer(({ children }) => {
    return <ul>{children}</ul>;
  });

  const getUsers = () => {
    return props.users.map((userWithBlocks) => (
      <li key={userWithBlocks.id}>{getUser(userWithBlocks, false)}</li>
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
        {!props.isSort && <ul>{getUsers()}</ul>}
        {props.isSort && getUsersWithSwapPosibility()}
      </div>
      {props.isSort && (
        <CommandButton
          className="save-order-button command-button--yellow command-button"
          caption="SaveOrder"
          action={props.onSaveOrder}
        />
      )}
    </div>
  );
}
