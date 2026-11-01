#include <openssl/sha.h>
#include <stdio.h>
#include <malloc.h>

int main()
{
	unsigned char* message = NULL;

	FILE* f = fopen("input.bin", "rb");
	fseek(f, 0, SEEK_END);

	unsigned int file_length = ftell(f);			// will return the number of bytes between the start of the file and the current position of the read-write pointer
	message = (unsigned char*)malloc(file_length);	// allocate dynamic memory for te buffer

	fseek(f, 0, SEEK_SET);
	fread(message, sizeof(unsigned char), file_length, f);

	unsigned char md[SHA_DIGEST_LENGTH];
	SHA1(message, file_length, md);		// one shot SHA1 call
	

	for (unsigned char i = 0; i < SHA_DIGEST_LENGTH; i++)
	{
		printf(" %02X", md[i]);
	}

	free(message);
	fclose(f);

	return 0;
}