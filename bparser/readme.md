# sic.bparser - Block Parser

Parses an input text into text, blocks and string literals.
Blocks are embraced by curly brackets.
`sic.bparser` supports C++11 raw string literals.

## Why ?

### Skipping blocks
`sic.bparser` allows to defer parsing of blocks enclosed in curly brackets.
This can be useful for example in case you want to parse all function prototypes and just skip their implementation.
E.g. SICC shall understand most of C's declarations, but not all C function implementations are valid SIC code.

### Heredocs / raw strings
`sic.bparser` supports C++11 raw string literals.
These literals can be compared to heredocs of sh, perl, python etc.
Handling heredocs and their variable delimiters in the block parser makes subsequent lexing easier.
(Moreover it has to be done here, because these heredocs could contain curly brackets.)

### Preprocessor directives
`sic.bparser` understands gcc preprocessor line directives.
This allows to preprocess SIC code without losing file and line information.
`sic.bparser` removes these directives to make subsequent parsing easier.

### Robustness
If there is an error in a block, you can drop it and continue analysing the rest of the file.

### Context sensitive grammers
Blocks can serve as a context synchronization point for parser and lexer,
so all findings of the parser are known to the lexer.
C and C++ are often described using context sensitive grammers (for C and C++ we need to synchronize at statement level, though).

### Using different parsers
You can use different parsers for the code in certain blocks, as long as the respective language
opens and closes curly braces correctly and uses compatible string literals.

## Getting started

Currently the best way to get started is to load the eclipse project which can be found at [./eclipsejprojs/dev/](./eclipsejprojs/dev/).

[//]: # (TODO path to jar release file)

## Running the tests

See "Getting started".

[//]: # (TODO test automation)

## License

SIC is licensed under GPL-3.0.

## Acknowledgments

The template for this file was [PurpleBooth's README-Template.md](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2),
but I removed a lot of stuff, because of laziness.
