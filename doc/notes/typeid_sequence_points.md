# Type identifier sequence points

***Deviation from C***

SIC's grammar is not context-free (just like C's grammar isn't context-free).

For example the meaning of statement `a * b;` depends on the context:
```C
/* Here `a * b;` is a multiplication: */
{
	int a, b;
	a * b;
}

/* Here `a * b;` declares a variable named `b` which points to an integer: */
{
	typedef int a;
	a * b;
}
```

Consequently, SIC's lexer cannot convert the whole translation unit to tokens ahead of running the parser.
Instead the parser has to feed all newly found type identifiers back to the lexer,
so the lexer can decide whether a token is either an identifier or a type-identifier.

In SIC this feedback is provided at certain occurences of the following tokens: `;`, `{`, `}`.
The respective tokens are called "type identifier sequence points".

An opening curly bracket `{` starts blocks, e.g. a compound statement.
All types declared in the outer scope before the `{` are visible to the block.
Blocks are ended by `}`.

Each semi-colon (`;`) which is
  - not part of a for loop head
  - on the same block nesting depth

is a "type identifier sequence point".
That is, all types defined before the semi-colon are available after the semi-colon.

## Statements defining types in SIC

Statements defining type identifiers in SIC and C:
```C
typedef A B;
```

Statements defining type identifiers in SIC and C++ but not in C:
```C++
struct A /* { ... } */;
class C /* { ... } */;
union D /* { ... } */;
enum E /* { ... } */;
using typename F /* = ... */;
using G /* = ... */; /*< Let's just forbid this... */
/* ... and of course all the template versions of these statements ... */
```

You can prefix identifiers with keywords to make clear that they are type identifiers.
E.g. `typename A` tells the parser that `A` is a type identifier.
All keywords which are used to define types are allowed as long as they match.
So you can also use `class C`, `struct C`, `union D` and `enum E`.

SIC drops the separate namespace for struct, enum, union identifiers.
Functionnames must not collide with struct-, enum-, union-, class-names or other typenames.

`typedef`s are allowed to define a type multiple times, as long as all definitions result in the same type.

It is forbidden to define types in for loop heads, because the lack of "type identifier sequence point"
might lead to unexpected behavior.

It is forbidden to define types in expressions.
They can only be defined in declarations and in separate statements.
This is, because the lack of "type identifier sequence point"
might lead to unexpected behavior.

## Deviation from C

### No definition and usage of type identifiers in the same statement

It is forbidden to use a typename which was defined in the same typedef statement.
That's because commas (`,`) aren't "type identifier sequence points".
Therefore the following C code is not valid SIC code:
```C
typedef int A, (*F)(A); /*< ERROR: A isn't known to be a typename. */

typedef int B, (*G)(B b); /*< ERROR: B isn't known to be a typename. */
```

Instead you'd have to do the following:
```C
/* Either */
{
	typedef int A;
	typedef int (*F)(A);

	typedef int B;
	typedef int (*G)(B b);
}
/* or */
{
	typedef int A, (*F)(typename A);
	typedef int B, (*G)(typename B b);
}
```

Note that you can still define a struct and associated typedefs, as the `struct` keyword makes clear that there is a type definition:
```C
typedef struct A {
	/* ... */
} A, *PA;
```

### No function names which hide type names

Type identifiers must not collide with other identifiers:
```C
struct A {};
struct B {};

int A(void); /*< ERROR: Multiple/contradicting definitions of A. */
int B;       /*< ERROR: Multiple/contradicting definitions of B. */
```


### No type definitions in expressions

Definitions of classes, structs, unions and enums are not allowed in expressions.
They are only allowed in declarations and as separate statements.

That is, because defining a struct also defines a type identifier,
but there are no "type identifier sequence points" in expressions.

So the following is not allowed in SIC (although it is probably allowed in C99):
```C
int j = 0;
while (
	(sizeof (struct A { }) != 42)	/*< ERROR: cannot define a struct inside an expression */
	&& (sizeof(struct A) != 43)
	&& ++j < 5
) printf("sizeof(struct A): %ld\n", sizeof(struct A));

int k = 0;
while (
	(((struct B { } *)0) == 0)		/*< ERROR: cannot define a struct inside an expression */
	&& (((struct B*)0) == 0)
	&& ++k < 5
) printf("sizeof(struct B): %ld\n", sizeof(struct B));
```

Note that C++ also doesn't allow the above code (citation needed).


### No type definitions in for loop heads

If you define a type identifier in a for loop head, it is not visible in the rest of the for loop head
and it is also not visible in the loop body, if it isn't a compound statement.
This could be a deviation from C++.
SIC shall forbid defining types in for loop heads, just like C99 (subclause 6.8.5 paragraph 3) does.
Otherwise, this could lead to unexpected beavior (e.g. if the compiler finds a type in an outer scope with the same name).

```C
/* First example: */
int i = 0;
for (
	typedef char my_char_t; /*< ERROR: type definitions are not allowed in a for loop's initial declaration */
	(printf("sizeof(my_char_t*): %ld\n", sizeof(my_char_t*)) > 0) && (i < 5); /*< ERROR: `my_char_t` is not known to be a typename */
	i++
) {
	printf("... sizeof(my_char_t*): %ld\n", sizeof(my_char_t*)); /*< After `{`, my_char_t would be known to be a type */
}
/* Second example: */
int i = 0;
for (
	typedef char my_char_t; /*< Should be an error: type definitions are not allowed in a for loop's initial declaration */
	(printf("sizeof(my_char_t*): %ld\n", sizeof(my_char_t*)) > 0) && (i < 5); /*< ERROR: `my_char_t` is not known to be a typename */
	i++
) printf("... sizeof(my_char_t*): %ld\n", sizeof(my_char_t*)); /*< ERROR: As there is no `{`, `my_char_t` is not known to be a typename */
```
