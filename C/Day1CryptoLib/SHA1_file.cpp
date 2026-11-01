#include <openssl/sha.h>
#include <stdio.h>

#define _CRT_SECURE_NO_WARNINGS
#define INPUT_BLOCK_LENGTH 60

int main()
{
	unsigned char message[INPUT_BLOCK_LENGTH];

	SHA_CTX ctx;		// openssl structure to store intermediate states according to SHA1 specification
	SHA1_Init(&ctx);	// initialization of the SHA_CTX structure (defined by opelssl crypto lib)

	FILE* f = fopen("input.bin", "rb");	// permissions are related to the file system - to read or write from/to the file system; unrelated with whatever's on the file; 'b' to make sure that everything is treated as binary
	unsigned short int bytes_read = 0;

	while (bytes_read = fread(message, sizeof(unsigned char), INPUT_BLOCK_LENGTH, f))
	{
		SHA1_Update(&ctx, (const unsigned char*)message, bytes_read);
	}

	unsigned char md[SHA_DIGEST_LENGTH];
	SHA1_Final(md, &ctx);	// final SHA1 digest saved into byte array md

	for (unsigned char i = 0; i < SHA_DIGEST_LENGTH; i++)
	{
		printf(" %02X", md[i]);
	}

	fclose(f);

	return 0;
}