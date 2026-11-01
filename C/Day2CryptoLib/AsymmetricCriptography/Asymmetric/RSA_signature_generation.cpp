#define _CRT_SECURE_NO_WARNINGS

#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/applink.c>

int main()
{
	unsigned char sha1[] = { 0xff, 0x71, 0x92, 0xb2, 0x9f,
							 0xfe, 0x7f, 0xa2, 0xb7, 0x0f,
							 0xef, 0x7d, 0xa2, 0xb5, 0x90,
							 0xf1, 0x7d, 0x94, 0xb3, 0xc0 };

	RSA* rsa_private_key = NULL;
	FILE* fpriv = fopen("RSAPrivateKey.pem", "rb");
	rsa_private_key = PEM_read_RSAPrivateKey(fpriv, &rsa_private_key, NULL, NULL);

	unsigned char* signature = (unsigned char*)malloc(RSA_size(rsa_private_key));

	// message digest input is padded according to PKCS1
	RSA_private_encrypt(sizeof(sha1), sha1, signature, rsa_private_key, RSA_PKCS1_PADDING);

	FILE* fsig = fopen("mySignature.sig", "wb+");
	if(signature)
		fwrite(signature, sizeof(unsigned char), RSA_size(rsa_private_key), fsig);

	RSA_free(rsa_private_key);
	free(signature);
	fclose(fpriv);
	fclose(fsig);

	return 0;
}