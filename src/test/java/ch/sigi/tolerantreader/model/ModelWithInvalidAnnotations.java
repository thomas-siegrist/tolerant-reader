/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;

@CustomName("Model")
public class ModelWithInvalidAnnotations {

    private Boolean someBoolean;

    @CustomName("someText")
    @CustomPath("/model/subTree/someText")
    private String someCustomFieldName;

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

}
