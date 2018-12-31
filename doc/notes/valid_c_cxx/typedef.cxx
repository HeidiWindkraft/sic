
#include <iostream>

// Known from C.
typedef int my_int_t, (*my_funcptr_t)(my_int_t), (*my_funcptr2_t)(my_int_t x);

int main() {
	using namespace std;

	// Known from C
	cout << "sizeof(my_int_t) == " << sizeof(my_int_t)
		<< "; sizeof(my_funcptr_t) == " << sizeof(my_funcptr_t)
		<< "; sizeof(my_funcptr2_t) == " << sizeof(my_funcptr2_t)
		<< endl;

	// C++ doesn't force you to put variable declarations at the begin of a block / compound statement.
	// It allows to declare variables in for loop heads.
	// As "typedef" is just a "storage class specifier", this allows to define types in for loop heads.
	// SIC will not allow this, as this can make implmenting lexer and parser more complicated.
	// See also typedef_c99.c.
	int i = 0;
	for (typedef char my_char_t; ((cout << "sizeof(my_char_t*): " << sizeof(my_char_t*) << endl) && (i < 5)); i++) {
		cout << "... sizeof(my_char_t*): " << sizeof(my_char_t*) << endl;
	}

#if 0
	/* Leads to "error: types may not be defined in ‘sizeof’ expressions" */
	int j = 0;
	while (
		(sizeof (struct A { }) != 42)
		&& (sizeof(struct A) != 43)
		&& ++j < 5
	) printf("sizeof(struct A): %ld\n", sizeof(struct A));

	/* Leads to "error: types may not be defined in casts" */
	int k = 0;
	while (
		(((struct B { } *)0) == 0)
		&& (((struct B*)0) == 0)
		&& ++k < 5
	) printf("sizeof(struct B): %ld\n", sizeof(struct B));
#endif


	return 0;
}
