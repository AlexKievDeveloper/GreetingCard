import React, {useContext} from 'react'
import { languageContext } from '../../../context/languageContext';
import FormAdd from '../../../forms/common/FormAdd';
import FilterButton from '../../UI/FilterButton'
import './style.css';

export default function CardsFilterPages(props) {
    const { dictionary } = useContext(languageContext);
    const filter = <div className="filter__cards">
       <FilterButton linkTo="/cards/all" caption="allCardsFilter" isActive={props.page !=='all_cards' || props.page == null} />
       <FilterButton linkTo="/cards/my"  caption="myCardsFilter" isActive={props.page !=='my_cards'} />
       <FilterButton linkTo="/cards/other"  caption="otherCardsFilter" isActive={props.page !=='other_cards'} />
   </div>;

    return (
        <div className="command__row">
            {filter}
            <FormAdd {...props} onSubmit={props.createCardFunction}
                     inputPlaceholder = {dictionary.nameCardPlaceholder}
                     buttonCaption = "createCardButton"/>
        </div>
    )
}
