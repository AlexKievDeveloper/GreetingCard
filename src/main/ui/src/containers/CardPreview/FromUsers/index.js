import React from "react";
import { Text } from "../../../components/Language/Text";
import User from "../User";
import "./style.css";

export default function FromUsers(props) {
  const getUsers = () => {
    return props.users.map((userWithBlocks) => (
      <User key={userWithBlocks.id} user={userWithBlocks} />
    ));
  };

  return (
    <div className="card__navigation">
      <Text tid="fromLabel" />
      <div className="card__contributors">
        <ul>{getUsers()}</ul>
      </div>
    </div>
  );
}
