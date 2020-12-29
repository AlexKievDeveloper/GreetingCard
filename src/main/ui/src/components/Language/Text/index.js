import { useContext } from "react";
import { languageContext } from "../../../context/languageContext";

export function Text({ tid }) {
  const languageApplicationContext = useContext(languageContext);

  return languageApplicationContext.dictionary[tid] || tid;
}
