#include <openssl/ec.h>
#include <openssl/obj_mac.h>
#include <openssl/sha.h>
#include <memory.h>

int main()
{
	unsigned char sha1[] = { 0xff, 0x71, 0x92, 0xb2, 0x9f,
							 0xfe, 0x7f, 0xa2, 0xb7, 0x0f,
							 0xef, 0x7d, 0xa2, 0xb5, 0x90,
							 0xf1, 0x7d, 0x94, 0xb3, 0xc0 };

	EC_KEY* ec_keypair = NULL;
	ec_keypair = EC_KEY_new_by_curve_name(NID_X9_62_prime256v1);	// specify the curve you want to use to generate the signature
	EC_KEY_generate_key(ec_keypair);								// generate the EC private key; the public one can be computed

	unsigned char* signature = (unsigned char*)malloc(80);
	unsigned int signature_length = 0;

	ECDSA_sign(0, sha1, SHA_DIGEST_LENGTH, signature, &signature_length, ec_keypair);

	FILE* fsig = fopen("ECDSA.sig", "wb+");
	if(signature)
		fwrite(signature, sizeof(unsigned char), signature_length, fsig);

	fclose(fsig);
	free(signature);
	EC_KEY_free(ec_keypair);

	return 0;
}