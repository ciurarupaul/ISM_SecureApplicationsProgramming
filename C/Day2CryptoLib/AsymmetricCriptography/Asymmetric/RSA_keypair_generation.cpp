#define _CRT_SECURE_NO_WARNINGS

#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/applink.c>

int main()
{
	RSA* rsa_keypair = NULL;
	rsa_keypair = RSA_generate_key(1024, 65537, NULL, NULL);

	FILE* fpriv = fopen("RSAPrivateKey.pem", "wb+");
	PEM_write_RSAPrivateKey(fpriv, rsa_keypair, NULL, NULL, 0, NULL, NULL);

	FILE* fpub = fopen("RSAPublicKey.pem", "wb+");
	PEM_write_RSAPublicKey(fpub, rsa_keypair);

	RSA_free(rsa_keypair);

	return 0;
}