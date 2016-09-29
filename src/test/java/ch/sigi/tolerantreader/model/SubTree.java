/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import java.util.List;

public class SubTree {

    private String someText;
    private Long someLong;
    private List<String> someList;

    public List<String> getSomeList() {
        return someList;
    }

    public void setSomeList(List<String> someList) {
        this.someList = someList;
    }

    public String getSomeText() {
        return someText;
    }

    public void setSomeText(String someText) {
        this.someText = someText;
    }

    public Long getSomeLong() {
        return someLong;
    }

    public void setSomeLong(Long someLong) {
        this.someLong = someLong;
    }

    @Override
    public String toString() {
        return "SubTree{" +
                "someList=" + someList +
                ", someLong=" + someLong +
                ", someText='" + someText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubTree subTree = (SubTree) o;

        if (someText != null ? !someText.equals(subTree.someText) : subTree.someText != null) {
            return false;
        }
        if (someLong != null ? !someLong.equals(subTree.someLong) : subTree.someLong != null) {
            return false;
        }
        return !(someList != null ? !someList.equals(subTree.someList) : subTree.someList != null);

    }

    @Override
    public int hashCode() {
        int result = someText != null ? someText.hashCode() : 0;
        result = 31 * result + (someLong != null ? someLong.hashCode() : 0);
        result = 31 * result + (someList != null ? someList.hashCode() : 0);
        return result;
    }

}
