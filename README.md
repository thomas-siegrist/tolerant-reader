# Java Tolerant-Reader

[![Travis Build Status](http://img.shields.io/travis/aalmiray/markdown-gradle-plugin.svg)](https://travis-ci.org/thomas-siegrist/tolerant-reader)
[![Bintray](https://api.bintray.com/packages/aalmiray/kordamp/markdown-gradle-plugin/images/download.svg)](https://bintray.com/sigi/maven/tolerant-reader)

## Why?
Please read this wonderful article from Fowler first: http://martinfowler.com/bliki/TolerantReader.html
When you think, being tolerant is not the way to go, you better don't use this code =)

When you want to write a tolerant reader, there is currently no satisfying solution in Java.
There are lots-of in functional programming languages, but not in Java.
That's why I have set up an easy-to-use solution which works, but with some limitations yet.

## Use it
**Read an XML-InputStream**
```java
InputStream is = ...
Model model = TolerantReader.readXml(is, Model.class);
```

**Read a Json-InputStream**
```java
InputStream is = ...
Model model = TolerantReader.readJson(is, Model.class);
```

### Settings
The default values, how the TolerantReader validates the output can be overriden in the Builder. When you just call .defaultSettings() it is the same as calling the following (that shows you the default values):
```java
String json = ...
Document<Model> document = JsonDocument.Builder
                .forClass(Model.class)
                .withInputString(json)
                .build();
Model model = TolerantReader.customize()
                    .validate(true)
                    .validator(new DefaultValidator())
                    .build()
                    .read(document);
```
Like this, one could also Implement Documents other than the standards (XML or Json) and pass them over to the TolerantReader, or write an own Validator in case of advanced validation requirements.

*Note: Currently, only Strings and InputStreams are supported as input.*

## Customization
### Custom model names
The Tolerant reader, by default, uses the field-names in order to build the Path to the leaf-elements. But you can override these values with a custom value. The Tolerant reader searches with the given name for the element in the XML / Json String. You can put the @CustomName Annotation over a field, or also over the class to declare how the root-element is being called (this takes only effect in XML, because Json does not explicitly declare the root elements).

```java
    @CustomName("theEffectiveNameOfTheFieldInTheInputSource")
    private String someCustomFieldNameInTheJavaModel;
```
### Custom model paths
The Tolerant reader, by default, goes through the model-class and puts together the path to find the value in the input-source. But you can explicitly override this path with a custom value. It must be a valid JsonPath / XPath Expression. The Tolerant reader searches the XML / Json String with the given path. You can put the @CustomPath Annotation over a field. When you point to a subtree, the whole subtree is parsed by using the given path to the root of the subtree. The paths can be absolute or relative. When you use relative paths you are even more tolerant, because the element could be moved within the tree and still matches your given model-class.

**An XML-Example with an absolute path:**
```java
...
    @CustomPath("/model/subTree/someText")
    private String subTreeText;
...
```

**A Json Example with an absolute path:**
```java
...
    @CustomPath("$.subTree.someText")
    private String subTreeText;
...
```

## Validation
### Declaration
In the Model-Class you can validate the output of the Parser at runtime with the following Annotations:
- ch.sigi.tolerantreader.annotation.NotNull
- jch.sigi.tolerantreader.annotation.Pattern

You must put the Annotations on the field that you want to validate.
```java
...
    @NotNull
    private Boolean someBoolean;

    @Pattern(regexp = "[a-zA-Z0-9.].*")
    private String someText;
...
```

In the current state, we provide JsonDocument and XmlDocument. De code is set-up to be extensible for other classes that inherit Document to be implemented.
## Latest changes
* Refactor code, that it will be reusable for JSON-Parsing
* Write the same logic for tolerant reading JSON
* Introduce Field-Validations
* Remove Dependency to javax.annotation by adding our own annotations for validation

## TODOs
* Performance-Improvements / Optimizations through working with Tree-Oriented Frameworks instead of XPath / JsonPath:
  * For XML: JDom
  * For Json: JsonNode

## It's OpenSource ...
Please let me know what you think about this approach or if you have some improvements, or better solutions in your mind !! And feel free to contribute via a pull-request.