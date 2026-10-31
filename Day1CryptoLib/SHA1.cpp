#include <openssl/sha.h>	// you may also use the whole path with quotes "" instead of modifing the sln's properties
#include <stdio.h>

#define INPUT_BLOCK_LENGTH 6

/*
	--- ensure a successful build of the project
	1. Add include folder to Project properties/ C/C++ /General/Additional Include Directories
	2. Add the path for the openssl-bundle/lib/libcrypto.lib, including the name of the file into Project Properties/Linkedr/Input/Additional Dependencies

	--- ensure a successful run of the openssl-bundle application
	3. Place the .dll file ("openssl-bundle/bin/libcrypto-1_1.dll") into the same folder the .exe file was generated into
*/

int main() 
{
	unsigned char message[] = { 0xff, 0xfe, 0xfd, 0xfc, 0x00, 0x10, 0x00, 0x02, 0x3e,
								0xff, 0xfe, 0xfd, 0xfc, 0x00, 0x10, 0x00, 0x02, 0x3e,
								0xff, 0xfe, 0xfd, 0xfc, 0x00, 0x10, 0x00, 0x02, 0x3e,
								0xff, 0xfe, 0xfd, 0xfc, 0x00, 0x10, 0x00, 0x02 };

	SHA_CTX ctx;
	SHA1_Init(&ctx);	// initialization of the SHA_CTX structure (defined by opelssl crypto lib)

	unsigned short int remaining_bytes = sizeof(message);	// message is a byte array, so this is the total lenght sent to SHA1_Update
	unsigned short int current_offeset = 0;
	
	while (remaining_bytes >= INPUT_BLOCK_LENGTH)
	{
		SHA1_Update(&ctx, (const unsigned char*)(message + current_offeset), INPUT_BLOCK_LENGTH);
		remaining_bytes -= INPUT_BLOCK_LENGTH;
		current_offeset += INPUT_BLOCK_LENGTH;
	}

	// process the last partial block (if present)
	if (remaining_bytes > 0) {
		SHA1_Update(&ctx, (const unsigned char*)(message + current_offeset), remaining_bytes);
		remaining_bytes = 0;	// redundant cleanup just in case
		current_offeset = 0;
	}

	unsigned char md[SHA_DIGEST_LENGTH];	// md as in message_digest
	SHA1_Final(md, &ctx);

	for (unsigned char i = 0; i < SHA_DIGEST_LENGTH; i++)
	{
		printf(" %02X", md[i]);
	}

	return 0;
}
