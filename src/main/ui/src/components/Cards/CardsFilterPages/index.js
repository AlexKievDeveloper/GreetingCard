import React from 'react'
import FormAdd from '../../../forms/common/FormAdd';
import FilterButton from '../../UI/FilterButton'
import './style.css';

export default function CardsFilterPages(props) {
    const filter = <div className="filter__cards">
       <FilterButton linkTo="/cards/all" caption="All Cards" isActive={props.page !=='all_cards' || props.page == null} />
       <FilterButton linkTo="/cards/my"  caption="My Cards" isActive={props.page !=='my_cards'} />
       <FilterButton linkTo="/cards/other"  caption="Other Cards" isActive={props.page !=='other_cards'} />
   </div>;

    return (
        <div className="command__row">
            {filter}
            <FormAdd {...props} onSubmit={props.createCardFunction}
                     inputPlaceholder = "Name new card"
                     buttonCaption = "Create card"/>
        </div>
    )
}
