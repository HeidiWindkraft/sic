
#include <stdio.h>

// Known from C90.
typedef int my_int_t, (*my_funcptr_t)(my_int_t), (*my_funcptr2_t)(my_int_t x);

int main() {

	printf("%s\n", __FILE__);

	// Known from C90.
	printf("sizeof(my_int_t) == %ld; sizeof(my_funcptr_t) == %ld; sizeof(my_funcptr2_t) == %ld;\n",
		sizeof(my_int_t),
		sizeof(my_funcptr_t),
		sizeof(my_funcptr2_t));

#if 0
	// C99 doesn't force you to put variable declarations at the begin of a block / compound statement.
	// It allows to declare variables in for loop heads.
	// As "typedef" is just a "storage class specifier", this would allow to define types in for loop heads.
	// However, gcc explicitly issues "error: declaration of non-variable ‘my_char_t’ in ‘for’ loop initial declaration",
	// so this useless construct is apparently not allowed.
	// See:
	// C99 subclause 6.8.5 paragraph 3:
	//    [#3] The declaration part of a for statement shall only declare identifiers for
	//         objects having storage class auto or register.
	// SIC won't allow typedefs in for loop heads either,
	// because this can make implementing lexer and parser more complicated.
	int i;
	for (typedef char my_char_t; (printf("sizeof(my_char_t): %ld\n", sizeof(my_char_t)) > 0) && (i < 5); i++) {
		printf("... sizeof(my_char_t): %ld\n", sizeof(my_char_t));
	}
#endif

	int j = 0;
	while (
		(sizeof (struct A { }) != 42)
		&& (sizeof(struct A) != 43)
		&& ++j < 5
	) printf("sizeof(struct A): %ld\n", sizeof(struct A));

	int k = 0;
	while (
		(((struct B { } *)0) == 0)
		&& (((struct B*)0) == 0)
		&& ++k < 5
	) printf("sizeof(struct B): %ld\n", sizeof(struct B));

	return 0;
}
