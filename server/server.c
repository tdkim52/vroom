/*
  Timothy Kim
  W01011895
  March 12, 2015
  VROOM - Server
  
  VSP Server
  
*/

// Headers //
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <ifaddrs.h>
#include <netdb.h>
#include <netinet/in.h>
#include <arpa/inet.h>

// Constants //
#define DPORT 49152
#define BUFSIZE 1024
#define BACKLOG 10

#define WILDCARD "*"
#define STDIN 0

// Prototypes //


// Program main //

int main (int argc, char *argv[])
{
  // variables
  int flag = 1;
  int sin_size;
  int sent;
  char *ipv4;
  FILE *cfile;
  char *buf = NULL;
  long fsize;
  size_t filelen;
  
  int leftsocket;
  int thissocket;
  
  struct sockaddr_in lsa;
  struct sockaddr_in tsa;
  
  struct hostent *h;
  
  // flags for arguemnts //

  
  // Scans arguments, sets corresponding variables and flags //
  
  
///////////////////////////////////////////////////////////////////////////////////////
  
  // loads coordinates.txt file to the buffer
  if ((cfile = fopen("coordinates.txt", "r")) == NULL) {
      perror("fopen");
      exit(1);
  }
  else {
      if (fseek(cfile, 0L, SEEK_END) == 0) {
	  fsize = ftell(cfile);
	  if (fsize == -1) {
	      perror("fseek");
	      exit(1);
	  }
	  buf = malloc(sizeof(char) * (fsize + 1));
	  if (fseek(cfile, 0L, SEEK_SET) != 0) {
	      perror("fseek");
	      exit(1);
	  }
	  filelen = fread(buf, sizeof(char), fsize, cfile);
	  if (filelen == 0) {
	      fputs("Error reading coordinates file", stderr);
	  }
	  else {
	      //buf[++filelen] = '\0';
	  }
      }
      fclose(cfile);
  }

  // handles client connection
  thissocket = socket(PF_INET, SOCK_STREAM, 0);
  memset(&tsa, 0, sizeof(tsa));
  tsa.sin_family = AF_INET;
  
  tsa.sin_port = htons(DPORT);
  tsa.sin_addr.s_addr = htonl(INADDR_ANY);

  struct ifaddrs * ifAddrStruct = NULL;
  struct ifaddrs * ifa = NULL;
  void * tmpAddrPtr = NULL;
  
  // displays ip of machine
  getifaddrs(&ifAddrStruct);
  for (ifa = ifAddrStruct; ifa != NULL; ifa = ifa->ifa_next) {
      if (!ifa->ifa_addr) {
	  continue;
      }
      if (ifa->ifa_addr->sa_family == AF_INET) {
	  tmpAddrPtr = &((struct sockaddr_in *)ifa->ifa_addr)->sin_addr;
	  char addressBuffer[INET_ADDRSTRLEN];
	  inet_ntop(AF_INET, tmpAddrPtr, addressBuffer, INET_ADDRSTRLEN);
	  printf("%s IP %s\n", ifa->ifa_name, addressBuffer);
      }
  }
  if (ifAddrStruct != NULL) freeifaddrs(ifAddrStruct);

  
  // prevents "address already in use" error message
  if (setsockopt(thissocket, SOL_SOCKET, SO_REUSEADDR, &flag, sizeof(int)) == -1)
  {
      perror("setsockopt");
      exit(1);
  }
  if (bind(thissocket, (struct sockaddr *)&tsa, sizeof(struct sockaddr)) < 0)
  {
      perror("bind");
      exit(1);
  }
  else {
      printf("waiting for connection on port %d...\n", DPORT);
  }
  if (listen(thissocket, BACKLOG) < 0)
  {
      perror("listen");
      exit(1);
  }
  sin_size = sizeof(struct sockaddr_in);
  if ((leftsocket = accept(thissocket, (struct sockaddr *)&lsa, &sin_size)) < 0)
  {
      perror("accept");
      exit(1);
  }
  else {
      char *ip = inet_ntoa(lsa.sin_addr);
      int port = lsa.sin_port;
      printf("Connection Established with: ");
      printf("%s:%d\n", ip, port);
      if ((sent = write(leftsocket, buf, filelen)) < 0) {
	  perror("write");
	  exit(1);
      }
      else {
	  printf("Obtaining hazards from database...\n");
	  printf("Sending coordinates file...\n");
      } 
  }
  printf("Server shutting down...\n");
  close(leftsocket);
  close(thissocket);
}
///////////////////////////////////////////////////////////////////////////////////////  

 
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  