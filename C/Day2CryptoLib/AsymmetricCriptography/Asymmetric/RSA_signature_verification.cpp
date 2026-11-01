#define _CRT_SECURE_NO_WARNINGS

#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/applink.c>
#include <openssl/sha.h>
#include <memory.h>
#include <stdio.h>

int main()
{
	// assume sha1 has been computed before for a clear (decrypted/readable/real) message
	unsigned char sha1[] = { 0xff, 0x71, 0x92, 0xb2, 0x9f,
							 0xfe, 0x7f, 0xa2, 0xb7, 0x0f,
							 0xef, 0x7d, 0xa2, 0xb5, 0x90,
							 0xf1, 0x7d, 0x94, 0xb3, 0xc0 };

	RSA* rsa_public_key = NULL;
	FILE* fpub = fopen("RSAPublicKey.pem", "rb");
	rsa_public_key = PEM_read_RSAPublicKey(fpub, &rsa_public_key, NULL, NULL);

	unsigned char restored[SHA_DIGEST_LENGTH];
	unsigned char* signature = (unsigned char*)malloc(RSA_size(rsa_public_key));

	FILE* fsig = fopen("mySignature.sig", "rb");
	if(signature)
		fread(signature, sizeof(unsigned char), RSA_size(rsa_public_key), fsig);

	// decrypt the signature buffer into restored by considering the exepected length of 
	// message digest and the padding used at signature generation time
	RSA_public_decrypt(RSA_size(rsa_public_key), signature, restored, rsa_public_key, RSA_PKCS1_PADDING);

	// 0 when the buffers are the same
	if (!memcmp(restored, sha1, SHA_DIGEST_LENGTH))
	{
		printf("Signature is valid!\n\n");
	}
	else
	{
		printf("Signature not validated!\n\n");
	}

	RSA_free(rsa_public_key);
	free(signature);
	fclose(fpub);
	fclose(fsig);

	return 0;
}