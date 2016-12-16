# Java Tolerant-Reader

## Why?
Please read this wonderful article from Fowler first: http://martinfowler.com/bliki/TolerantReader.html
When you think, being tolerant is not the way to go, you better don't use this code =)

When you want to write a tolerant reader, there is currently no satisfying solution in Java.
There are lots-of in functional programming languages, but not in Java.
That's why I have set up an easy-to-use solution which works, but with some limitations yet.

## Use it
```java
InputStream is = ...
Document<Model> document = JsonDocument.Builder
                .forClass(Model.class)
                .withInputStream(is)
                .build();
Model model = TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(document);
```

## Validation
### Declaration
In the Model-Class you can validate the output of the Parser at runtime with the following Annotations:
- javax.validation.constraints.NotNull
- javax.validation.constraints.Pattern

You must put the Annotations on the field that you want to validate.
```java
...
    @NotNull
    private Boolean someBoolean;

    @Pattern(regexp = "[a-zA-Z0-9.].*")
    private String someText;
...
```

### Settings
The default values, how the TolerantReader validates the output can be overriden in the Builder. When you just call .defaultSettings() it is the same as calling the following (that shows you the default values):
```java
Model model = TolerantReader.Builder
                    .defaultSettings()
                    .validate(true)
                    .validator(new DefaultValidator())
                    .build()
                    .read(document);
```

In the current state, we provide JsonDocument and XmlDocument. De code is set-up to be extensible for other classes that inherit Document to be implemented.
## Latest changes
* Refactor code, that it will be reusable for JSON-Parsing
* Write the same logic for tolerant reading JSON
* Introduce Field-Validations

## TODOs
* Performance-Improvements / Optimizations through working with Tree-Oriented Frameworks instead of XPath / JsonPath:
  * For XML: JDom
  * For Json: JsonNode

## It's OpenSource ...
Please let me know what you think about this approach or if you have some improvements, or better solutions in your mind !! And feel free to contribute via a pull-request.