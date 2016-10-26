/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import java.util.List;

public class SubTreeSuperset {

    private String someNewText;
    private String someText;
    private Long someLong;
    private List<String> someList;
    private List<String> someOtherList;

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

    public String getSomeNewText() {
        return someNewText;
    }

    public void setSomeNewText(String someNewText) {
        this.someNewText = someNewText;
    }

    public List<String> getSomeOtherList() {
        return someOtherList;
    }

    public void setSomeOtherList(List<String> someOtherList) {
        this.someOtherList = someOtherList;
    }

    @Override
    public String toString() {
        return "SubTreeSuperset{" +
                "someNewText='" + someNewText + '\'' +
                ", someText='" + someText + '\'' +
                ", someLong=" + someLong +
                ", someList=" + someList +
                ", someOtherList=" + someOtherList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SubTreeSuperset that = (SubTreeSuperset) o;

        if (someNewText != null ? !someNewText.equals(that.someNewText) : that.someNewText != null)
            return false;
        if (someText != null ? !someText.equals(that.someText) : that.someText != null)
            return false;
        if (someLong != null ? !someLong.equals(that.someLong) : that.someLong != null)
            return false;
        if (someList != null ? !someList.equals(that.someList) : that.someList != null)
            return false;
        return someOtherList != null ? someOtherList.equals(that.someOtherList) : that.someOtherList == null;

    }

    @Override
    public int hashCode() {
        int result = someNewText != null ? someNewText.hashCode() : 0;
        result = 31 * result + (someText != null ? someText.hashCode() : 0);
        result = 31 * result + (someLong != null ? someLong.hashCode() : 0);
        result = 31 * result + (someList != null ? someList.hashCode() : 0);
        result = 31 * result + (someOtherList != null ? someOtherList.hashCode() : 0);
        return result;
    }
}
