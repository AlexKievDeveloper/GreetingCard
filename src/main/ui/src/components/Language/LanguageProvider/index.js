import React, {useState} from 'react'
import { languageContext } from '../../../context/languageContext';
import {dictionaryList, languageOptions} from '../../../languages'
import { userService } from '../../../services/userService';

export function LanguageProvider({ children }) {
    const [userLanguage, setUserLanguage] = useState('EN');
  
    const provider = {
      userLanguage,
      dictionary: dictionaryList[userLanguage],
      userLanguageChange: selected => {
        const newLanguage = languageOptions[selected] ? selected : 'EN'
        setUserLanguage(newLanguage);
        userService.setLanguage(newLanguage);
      }
    };
  
    return (
      <languageContext.Provider value={provider}>
        {children}
      </languageContext.Provider>
    );
  };