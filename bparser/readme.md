# sic.bparser - Block Parser

Parses an input text into text, blocks and string literals.
Blocks are embraced by curly brackets.
sic.bparser supports C++11 raw string literals.

## Why ?
[//]: # (TODO Explain for what it can be used)

### Skipping blocks
sic.bparser allows to defer parsing of blocks enclosed in curly brackets.
This can be useful for example in case you want to parse all function prototypes and just skip their implementation.
Another example are languages where you first need to know all declarations, before you can parse the implementations.

Context-free grammars are a nice concept. But for some languages might need some extension ...

### Using different parsers
You can use different parsers for the code in certain blocks, as long as the respective language
opens and closes curly braces correctly and uses compatible string literals.

### Heredocs / raw strings
sic.bparser supports C++11 raw string literals.
These literals can be compared to heredocs of sh, perl, python etc.
Handling heredocs and their variable delimiters in the block parser makes subsequent lexing easier.
(Moreover it has to be done here, because these heredocs could contain curly brackets.)

## Getting started

This library comes as a jar file or java code.

[//]: # (TODO path to jar release file)

## Running the tests

[//]: # (TODO test automation)


## Contributing

[//]: # (TODO CONTRIBUTING.md)

## Versioning

Later versions have higher numbers.
Version numbers are big endian.
The version of sic.bparser is determined by the version of the entire sic release.

Use [SemVer](http://semver.org/) for versioning **for the API** as soon as prototyping is done.

[//]: # (TODO For the versions available, see the tags on this [repository](https://github.com/your/project/tags).)

## Authors

* **Heidi Windkraft** - *Initial work* - [HeidiWindkraft](https://github.com/HeidiWindkraft)

[//]: # (TODO See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.)

## License

This project is licensed under the GPL TODO.

## Acknowledgments

The template for this file was [PurpleBooth's README-Template.md](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2).
