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
Model parsedModel = TolerantXmlReader.Builder
                    .defaultSettings()
                    .build()
                    .read(is, Model.class);
```

## TODOs
* Refactor code, that it will be reusable for JSON-Parsing
* Write the same logic for tolerant reading JSON
* Performance-Improvements / Optimizations
  * Consider using the sax-parser
  * Consider a way of not traveling the whole tree by using something other than xPath / jsonPath
* Introduce existing validation-frameworks in order to precicely validate the resulting Model at runtime (giving some catchy-error-messages).

Please let me know what you think about this approach or if you have some improvements, or better solutions in your mind !! And feel free to contribute via a pull-request.