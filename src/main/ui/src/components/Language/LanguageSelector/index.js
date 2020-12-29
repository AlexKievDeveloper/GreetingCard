import React, { useContext, useEffect } from "react";
import "./style.css";

import { languageOptions } from "../../../languages";
import { languageContext } from "../../../context/languageContext";
import { userService } from "../../../services/userService";
import LanguageButton from "./LanguageButton";

export default function LanguageSelector() {
  const { userLanguage, userLanguageChange } = useContext(languageContext);

  const changeLanguage = (language) => {
    userService.updateLanguage(language);
    userLanguageChange(language);
  };

  useEffect(() => {
    let defaultLanguage = window.localStorage.getItem("userLanguage");
    if (!defaultLanguage) {
      defaultLanguage = window.navigator.language.substring(0, 2);
      defaultLanguage = defaultLanguage.toUpperCase();
      if (!(defaultLanguage in languageOptions)) {
        defaultLanguage = "EN";
      }
    }
    userLanguageChange(defaultLanguage);
  }, [userLanguageChange]);

  return (
    <div className="languages">
      {Object.entries(languageOptions).map(([id, name]) => (
        <LanguageButton
          key={id}
          language={id}
          isChoosen={id === userLanguage}
          onClick={changeLanguage}
        />
      ))}
    </div>
  );
}
