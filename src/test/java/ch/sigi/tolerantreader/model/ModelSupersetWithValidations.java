/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import javax.xml.bind.annotation.XmlRootElement;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.NotNull;
import ch.sigi.tolerantreader.annotation.Pattern;

@XmlRootElement
@CustomName("Model")
public class ModelSupersetWithValidations {


    private String someAdditionalField;

    @NotNull
    private Boolean someBoolean;

    @Pattern(regexp = "[a-zA-Z0-9.].*")
    private String someText;

    private Long someLong;
    private Integer someInteger;
    private Short someShort;
    private Double someDouble;
    private Float someFloat;
    private SubTreeSuperset subTree;

    public String getSomeAdditionalField() {
        return someAdditionalField;
    }

    public void setSomeAdditionalField(String someAdditionalField) {
        this.someAdditionalField = someAdditionalField;
    }

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

    public Float getSomeFloat() {
        return someFloat;
    }

    public void setSomeFloat(Float someFloat) {
        this.someFloat = someFloat;
    }

    public Boolean getSomeBoolean() {
        return someBoolean;
    }

    public SubTreeSuperset getSubTree() {
        return subTree;
    }

    public void setSubTree(SubTreeSuperset subTree) {
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

        public ModelSupersetWithValidations build() {
            ModelSupersetWithValidations modelSuperset = new ModelSupersetWithValidations();
            modelSuperset.setSomeBoolean(model.getSomeBoolean());
            modelSuperset.setSomeDouble(model.getSomeDouble());
            modelSuperset.setSomeInteger(model.getSomeInteger());
            modelSuperset.setSomeLong(model.getSomeLong());
            modelSuperset.setSomeShort(model.getSomeShort());
            modelSuperset.setSomeText(model.getSomeText());
            modelSuperset.setSubTree(toSubTreeSuperset(model.getSubTree()));
            return modelSuperset;
        }

        private SubTreeSuperset toSubTreeSuperset(SubTree subTree) {
            SubTreeSuperset subTreeSuperset = new SubTreeSuperset();
            subTreeSuperset.setSomeNewText(subTree.getSomeText());
            subTreeSuperset.setSomeText(subTree.getSomeText());
            subTreeSuperset.setSomeLong(subTree.getSomeLong());
            subTreeSuperset.setSomeList(subTree.getSomeList());
            subTreeSuperset.setSomeOtherList(subTree.getSomeList());
            return subTreeSuperset;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ModelSupersetWithValidations that = (ModelSupersetWithValidations) o;

        if (someAdditionalField != null ? !someAdditionalField.equals(that.someAdditionalField) : that.someAdditionalField != null)
            return false;
        if (someBoolean != null ? !someBoolean.equals(that.someBoolean) : that.someBoolean != null)
            return false;
        if (someText != null ? !someText.equals(that.someText) : that.someText != null)
            return false;
        if (someLong != null ? !someLong.equals(that.someLong) : that.someLong != null)
            return false;
        if (someInteger != null ? !someInteger.equals(that.someInteger) : that.someInteger != null)
            return false;
        if (someShort != null ? !someShort.equals(that.someShort) : that.someShort != null)
            return false;
        if (someDouble != null ? !someDouble.equals(that.someDouble) : that.someDouble != null)
            return false;
        if (someFloat != null ? !someFloat.equals(that.someFloat) : that.someFloat != null)
            return false;
        return subTree != null ? subTree.equals(that.subTree) : that.subTree == null;

    }

    @Override
    public int hashCode() {
        int result = someAdditionalField != null ? someAdditionalField.hashCode() : 0;
        result = 31 * result + (someBoolean != null ? someBoolean.hashCode() : 0);
        result = 31 * result + (someText != null ? someText.hashCode() : 0);
        result = 31 * result + (someLong != null ? someLong.hashCode() : 0);
        result = 31 * result + (someInteger != null ? someInteger.hashCode() : 0);
        result = 31 * result + (someShort != null ? someShort.hashCode() : 0);
        result = 31 * result + (someDouble != null ? someDouble.hashCode() : 0);
        result = 31 * result + (someFloat != null ? someFloat.hashCode() : 0);
        result = 31 * result + (subTree != null ? subTree.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModelSuperset{" +
                "someAdditionalField='" + someAdditionalField + '\'' +
                ", someBoolean=" + someBoolean +
                ", someText='" + someText + '\'' +
                ", someLong=" + someLong +
                ", someInteger=" + someInteger +
                ", someShort=" + someShort +
                ", someDouble=" + someDouble +
                ", someFloat=" + someFloat +
                ", subTree=" + subTree +
                '}';
    }
}
