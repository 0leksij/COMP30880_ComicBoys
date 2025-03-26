package com.comicboys.project.data;

import java.util.List;

public class NumberedList{
    private final List<String> items;

    public NumberedList(List<String> items){
        this.items = items;
    }

    // Retrieves an item from the list by its 1-based index
    public String getItem(int index){
        if (index < 1 || index > items.size()){
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return items.get(index - 1);
    }

    public List<String> getItems(){
        return items;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < items.size(); i++) {
            sb.append((i + 1)).append(". ").append(items.get(i)).append("\n");
        }
        return sb.toString();
    }
}