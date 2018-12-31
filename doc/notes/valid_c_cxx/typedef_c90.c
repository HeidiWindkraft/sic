
#include <stdio.h>

// After the comma, the typeid "my_int_t" is known to the lexer, so it can emit a type token
// for "my_int_t" the definition of "my_funcptr_t" (or the grammar just deals with this issue).
// SIC parsers are allowed to emit an error here,
//  because only ';', '{' and '}' are "type identifier sequence points".
//  Note: The semi-colons in for loop heads aren't "type identifier sequence points", see typedef_c99.c.
typedef int my_int_t, (*my_funcptr_t)(my_int_t), (*my_funcptr2_t)(my_int_t x);

int main() {
	printf("%s\n", __FILE__);
	printf("sizeof(my_int_t) == %ld; sizeof(my_funcptr_t) == %ld; sizeof(my_funcptr2_t) == %ld;\n",
		sizeof(my_int_t),
		sizeof(my_funcptr_t),
		sizeof(my_funcptr2_t));
	return 0;
}
