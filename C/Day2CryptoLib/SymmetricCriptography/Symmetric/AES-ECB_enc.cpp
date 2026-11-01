#define _CRT_SECURE_NO_WARNINGS

#include <openssl/aes.h>
#include <stdio.h>
#include <memory.h>

int main() 
{
	unsigned char key[] = { 0xff, 0x18, 0x89, 0xb3, 0xf1, 0xc8, 0x79, 0xba,
							0xa9, 0xa8, 0xa9, 0xa3, 0x11, 0x1e, 0xe2, 0xc7 };

	FILE* f = NULL, *fenc = NULL;
	f = fopen("input.bin", "rb");
	fenc = fopen("input.enc", "wb+");

	unsigned char plaintext_block[AES_BLOCK_SIZE];
	unsigned char ciphertext_block[AES_BLOCK_SIZE];
	unsigned int  bytes_read = 0;

	AES_KEY aes_key;
	AES_set_encrypt_key(key, (int)(sizeof(key) * 8), &aes_key);

	while ((bytes_read = fread(plaintext_block, sizeof(unsigned char), AES_BLOCK_SIZE, f)) == AES_BLOCK_SIZE) 
	{
		// encrypt one single AES data block
		AES_encrypt(plaintext_block, ciphertext_block, &aes_key);
		fwrite(ciphertext_block, sizeof(unsigned char), AES_BLOCK_SIZE, fenc);
	}

	if (bytes_read > 0)
	{
		// there is a last partial block to be encrypted
		unsigned char bytes_to_fill = AES_BLOCK_SIZE - bytes_read;
		memset((unsigned char*)(plaintext_block + bytes_read), bytes_to_fill, bytes_to_fill); // casts are somewhat redundant in this case, added for clarity
		AES_encrypt(plaintext_block, ciphertext_block, &aes_key);
		fwrite(ciphertext_block, sizeof(unsigned char), AES_BLOCK_SIZE, fenc);
	}

	fclose(f);
	fclose(fenc);

	return 0;
}