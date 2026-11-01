#define _CRT_SECURE_NO_WARNINGS

#include <openssl/aes.h>
#include <stdio.h>
#include <memory.h>

int main()
{
	unsigned char key[] = { 0xff, 0x18, 0x89, 0xb3, 0xf1, 0xc8, 0x79, 0xba,
							0xa9, 0xa8, 0xa9, 0xa3, 0x11, 0x1e, 0xe2, 0xc7 };

	FILE* f = NULL, * fdec = NULL;
	f	 = fopen("input.enc", "rb");
	fdec = fopen("intput.dec", "wb+");

	fseek(f, 0, SEEK_END);
	unsigned long file_size = ftell(f);
	unsigned int no_aes_blocks = (unsigned int)(file_size / AES_BLOCK_SIZE);
	fseek(f, 0, SEEK_SET);

	unsigned char restored_block[AES_BLOCK_SIZE];
	unsigned char ciphertext_block[AES_BLOCK_SIZE];
	unsigned int  bytes_read = 0;

	AES_KEY aes_key;
	AES_set_decrypt_key(key, (int)(sizeof(key) * 8), &aes_key);

	unsigned int block_counter = 1;
	while (block_counter < no_aes_blocks) 
	{
		bytes_read = fread(ciphertext_block, sizeof(unsigned char), AES_BLOCK_SIZE, f);

		AES_decrypt(ciphertext_block, restored_block, &aes_key);				// decryption of ciphertext at AES data block level
		fwrite(restored_block, sizeof(unsigned char), AES_BLOCK_SIZE, fdec);	// save the restored AES data block into a file

		block_counter++;
	}

	// I am sure there is another block because of the while condition
	bytes_read = fread(ciphertext_block, sizeof(unsigned char), AES_BLOCK_SIZE, f);
	AES_decrypt(ciphertext_block, restored_block, &aes_key);

	// check the padding's value and validate the padding's integrity (all the final bytes have the specified value)
	unsigned char pad_value = restored_block[AES_BLOCK_SIZE - 1];
	if (pad_value < AES_BLOCK_SIZE)
	{
		unsigned char flag = 0; // use to confirm padding integrity; 1 means false/not verified
		for (unsigned char i = AES_BLOCK_SIZE - 1; i > AES_BLOCK_SIZE - pad_value - 1 && flag == 0; i--)
		{
			if (restored_block[i] != pad_value) {
				flag = 1;
			}
		}

		if (!flag) {
			// padding is there and we confirmed its integrity
			fwrite(restored_block, sizeof(unsigned char), AES_BLOCK_SIZE - pad_value, fdec);
		}
		else
		{
			fwrite(restored_block, sizeof(unsigned char), AES_BLOCK_SIZE, fdec);
		}
	}
	else
	{
		// can't validate integrity, treat the whole block as relevant data
		fwrite(restored_block, sizeof(unsigned char), AES_BLOCK_SIZE, fdec);
	}

	fclose(f);
	fclose(fdec);

	return 0;
}