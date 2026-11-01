#define _CRT_SECURE_NO_WARNINGS

#include <openssl/aes.h>
#include <stdio.h>
#include <memory.h>
#include <malloc.h>

int main()
{
	unsigned char key[] = { 0xff, 0x18, 0x89, 0xb3, 0xf1, 0xc8, 0x79, 0xba,
							0xa9, 0xa8, 0xa9, 0xa3, 0x11, 0x1e, 0xe2, 0xc7 };
	unsigned char IV[] = { 0xff, 0xff, 0x0e, 0x0e, 0x1a, 0x1b, 0x1c, 0x2d,
						   0xab, 0xcd, 0xca, 0xfe, 0x13, 0x15, 0x17, 0x31 };
	// the IV is updated between calls; if you wish to do both enc and dec in the same file, preserve its initial value

	FILE* f = NULL, * fenc = NULL;
	f = fopen("input.bin", "rb");
	fenc = fopen("input.enc", "wb+");

	unsigned char* plaintext = NULL;
	unsigned char* ciphertext = NULL;
	unsigned int  bytes_read = 0;

	AES_KEY aes_key;
	AES_set_encrypt_key(key, sizeof(key) * 8, &aes_key);

	fseek(f, 0, SEEK_END);	// move the internal pointer to the end to find content length
	unsigned long plaintext_length = ftell(f);
	fseek(f, 0, SEEK_SET);

	plaintext = (unsigned char *)malloc(plaintext_length);
	fread(plaintext, sizeof(unsigned char), plaintext_length, f);

	/*
		plaintext_length % 16		how many bytes are in the last, partial block
		(16 - that)					how many bytes are missing
		outer % 16					turns '16 missing' into 0, needed when the data alredy fills the whole block

		e.g.
		20 % 16 = 4		the last block holds 4 bytes
		16 - 4 = 12		bytes are missing.
		12 % 16 = 12	cover edge case
	*/
	unsigned long ciphertext_lenght = plaintext_length + ((AES_BLOCK_SIZE - plaintext_length % AES_BLOCK_SIZE) % AES_BLOCK_SIZE);
	ciphertext = (unsigned char*)malloc(ciphertext_lenght);

	// padding needed
	if (plaintext_length < ciphertext_lenght)
	{
		plaintext = (unsigned char*)realloc(plaintext, ciphertext_lenght);

		unsigned char bytes_to_fill = (unsigned char)(ciphertext_lenght - plaintext_length);
		memset((unsigned char*)(plaintext + plaintext_length), bytes_to_fill, bytes_to_fill);

		plaintext_length = ciphertext_lenght;
	}

	AES_cbc_encrypt(plaintext, ciphertext, plaintext_length, &aes_key, IV, AES_ENCRYPT);

	fwrite(ciphertext, sizeof(unsigned char), ciphertext_lenght, fenc);

	free(plaintext);
	free(ciphertext);
	fclose(f);
	fclose(fenc);

	return 0;
}