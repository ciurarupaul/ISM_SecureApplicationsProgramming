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

	FILE* f = NULL, * fdec = NULL;
	f = fopen("input_CBC.enc", "rb");
	fdec = fopen("input_CBC.dec", "wb+");

	unsigned char* restored = NULL;
	unsigned char* ciphertext = NULL;
	unsigned int  bytes_read = 0;

	AES_KEY aes_key;
	AES_set_encrypt_key(key, sizeof(key) * 8, &aes_key);


	fseek(f, 0, SEEK_END);	// move the internal pointer to the end to find content length
	unsigned long ciphertext_lenght = ftell(f);
	fseek(f, 0, SEEK_SET);

	ciphertext = (unsigned char*)malloc(ciphertext_lenght);
	fread(ciphertext, sizeof(unsigned char), ciphertext_lenght, f);

	restored = (unsigned char*)malloc(ciphertext_lenght);

	AES_cbc_encrypt(ciphertext, restored, ciphertext_lenght, &aes_key, IV, AES_DECRYPT);


	// adapted from AES-ECB_dec.cpp
	unsigned long restored_length = ciphertext_lenght;
	unsigned char pad_value = restored[restored_length - 1];
	if (pad_value < AES_BLOCK_SIZE)
	{
		unsigned char flag = 0;
		for (unsigned long i = restored_length - 1; i > restored_length - pad_value - 1 && flag == 0; i--)
		{
			if (restored[i] != pad_value) {
				flag = 1;
			}
		}

		if (!flag) {
			// there is padding
			restored_length -= pad_value;
			restored = (unsigned char*)realloc(restored, restored_length);
		}
	}

	fwrite(restored, sizeof(unsigned char), restored_length, fdec);

	free(ciphertext);
	free(restored);
	fclose(f);
	fclose(fdec);

	return 0;
}