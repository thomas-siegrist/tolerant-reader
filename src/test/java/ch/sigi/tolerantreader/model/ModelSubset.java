/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import javax.xml.bind.annotation.XmlRootElement;

import ch.sigi.tolerantreader.annotation.CustomName;

@XmlRootElement
@CustomName("Model")
public class ModelSubset {

    private Boolean someBoolean;
    private String someText;
    private Long someLong;
    private Integer someInteger;
    private Short someShort;
    private Double someDouble;
    private SubTreeSubset subTree;

    public void setSomeBoolean(Boolean someBoolean) {
        this.someBoolean = someBoolean;
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

    public Integer getSomeInteger() {
        return someInteger;
    }

    public void setSomeInteger(Integer someInteger) {
        this.someInteger = someInteger;
    }

    public Short getSomeShort() {
        return someShort;
    }

    public void setSomeShort(Short someShort) {
        this.someShort = someShort;
    }

    public Double getSomeDouble() {
        return someDouble;
    }

    public void setSomeDouble(Double someDouble) {
        this.someDouble = someDouble;
    }

    public Boolean getSomeBoolean() {
        return someBoolean;
    }

    public SubTreeSubset getSubTree() {
        return subTree;
    }

    public void setSubTree(SubTreeSubset subTree) {
        this.subTree = subTree;
    }

    public static final class Builder {
        private Model model;

        private Builder(Model model) {
            this.model = model;
        }

        public static Builder withModel(Model model) {
            return new Builder(model);
        }

        public ModelSubset build() {
            ModelSubset modelSubset = new ModelSubset();
            modelSubset.setSomeBoolean(model.getSomeBoolean());
            modelSubset.setSomeDouble(model.getSomeDouble());
            modelSubset.setSomeInteger(model.getSomeInteger());
            modelSubset.setSomeLong(model.getSomeLong());
            modelSubset.setSomeShort(model.getSomeShort());
            modelSubset.setSomeText(model.getSomeText());
            modelSubset.setSubTree(toSubTreeSubset(model.getSubTree()));
            return modelSubset;
        }

        private SubTreeSubset toSubTreeSubset(SubTree subTree) {
            SubTreeSubset subTreeSubset = new SubTreeSubset();
            subTreeSubset.setSomeText(subTree.getSomeText());
            subTreeSubset.setSomeLong(subTree.getSomeLong());
            return subTreeSubset;
        }

    }

    @Override
    public String toString() {
        return "Model{" +
                "someBoolean=" + someBoolean +
                ", someText='" + someText + '\'' +
                ", someLong=" + someLong +
                ", someInteger=" + someInteger +
                ", someShort=" + someShort +
                ", someDouble=" + someDouble +
                ", subTree=" + subTree +
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

        ModelSubset model = (ModelSubset) o;

        if (someBoolean != null ? !someBoolean.equals(model.someBoolean) : model.someBoolean != null) {
            return false;
        }
        if (someText != null ? !someText.equals(model.someText) : model.someText != null) {
            return false;
        }
        if (someLong != null ? !someLong.equals(model.someLong) : model.someLong != null) {
            return false;
        }
        if (someInteger != null ? !someInteger.equals(model.someInteger) : model.someInteger != null) {
            return false;
        }
        if (someShort != null ? !someShort.equals(model.someShort) : model.someShort != null) {
            return false;
        }
        if (someDouble != null ? !someDouble.equals(model.someDouble) : model.someDouble != null) {
            return false;
        }
        return !(subTree != null ? !subTree.equals(model.subTree) : model.subTree != null);

    }

    @Override
    public int hashCode() {
        int result = someBoolean != null ? someBoolean.hashCode() : 0;
        result = 31 * result + (someText != null ? someText.hashCode() : 0);
        result = 31 * result + (someLong != null ? someLong.hashCode() : 0);
        result = 31 * result + (someInteger != null ? someInteger.hashCode() : 0);
        result = 31 * result + (someShort != null ? someShort.hashCode() : 0);
        result = 31 * result + (someDouble != null ? someDouble.hashCode() : 0);
        result = 31 * result + (subTree != null ? subTree.hashCode() : 0);
        return result;
    }

}
