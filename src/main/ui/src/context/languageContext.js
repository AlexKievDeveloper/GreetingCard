import React from 'react';
import { dictionaryList } from '../languages';

export const languageContext = React.createContext({
  userLanguage: 'UA',
  dictionary: dictionaryList.ua
});