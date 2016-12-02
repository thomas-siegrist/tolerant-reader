/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import java.util.List;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;

@CustomName("Model")
public class ModelWithAnnotations {

    private Boolean someBoolean;

    @CustomName("someText")
    private String someCustomFieldName;

    @CustomName("inexistent")
    private String someInexistendCustomField;

    @CustomPath("/model/subTree/someText")
    private String subTreeText;

    @CustomPath("/model/subTree/someLong")
    private Long subTreeLong;

    @CustomPath("/model/subTree/someList")
    private List<String> subTreeList;

    @CustomPath("/model/inexistent")
    private SubTree inexistent;

    public Boolean getSomeBoolean() {
        return someBoolean;
    }

    public void setSomeBoolean(Boolean someBoolean) {
        this.someBoolean = someBoolean;
    }

    public String getSomeCustomFieldName() {
        return someCustomFieldName;
    }

    public void setSomeCustomFieldName(String someCustomFieldName) {
        this.someCustomFieldName = someCustomFieldName;
    }

    public String getSomeInexistendCustomField() {
        return someInexistendCustomField;
    }

    public void setSomeInexistendCustomField(String someInexistendCustomField) {
        this.someInexistendCustomField = someInexistendCustomField;
    }

    public String getSubTreeText() {
        return subTreeText;
    }

    public void setSubTreeText(String subTreeText) {
        this.subTreeText = subTreeText;
    }

    public Long getSubTreeLong() {
        return subTreeLong;
    }

    public void setSubTreeLong(Long subTreeLong) {
        this.subTreeLong = subTreeLong;
    }

    public List<String> getSubTreeList() {
        return subTreeList;
    }

    public void setSubTreeList(List<String> subTreeList) {
        this.subTreeList = subTreeList;
    }

    public SubTree getInexistent() {
        return inexistent;
    }

    public void setInexistent(SubTree inexistent) {
        this.inexistent = inexistent;
    }
}
