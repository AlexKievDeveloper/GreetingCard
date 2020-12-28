import React, { useState } from "react";
import "./style.css";

export default function RenameCard(props) {
  const [name, setName] = useState(props.cardName ? props.cardName : "");

  React.useEffect(() => {
    setName(props.cardName);
  }, [props.cardName]);

  const doAction = (event) => {
    event.preventDefault();
    if (name === "") {
      alert("Value is empty!");
    } else {
      props.saveNameFunction(name);
    }
  };

  return (
    <form id="rename-card" action="#">
      <input
        type="text"
        className="card__title"
        value={name}
        onChange={(e) => {
          setName(e.target.value);
        }}
      />
      <input
        type="submit"
        name="Save name"
        className="save-name"
        value="Save name"
        onClick={doAction}
      />
    </form>
  );
}
