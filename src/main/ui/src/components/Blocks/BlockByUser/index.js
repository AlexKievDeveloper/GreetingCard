import React from "react";
import Block from "../Block";
import "./style.css";

export default function BlockByUser(props) {
  const user = props.blocks[0].user;
  const blocks = props.blocks.map((block) => (
    <Block key={block.id} block={block} isEdit={false} />
  ));
  return (
    <div
      className="blocks__column with-background paper-like"
      id={"blocks__column_Author" + user.id}
    >
      {blocks}
    </div>
  );
}
