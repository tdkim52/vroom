#	$Id: Makefile,v 1.4 2014/10/22 06:14:05 kimt21 Exp $

# CS 352  --  Mini Shell!
#
#   Timothy Kim
#   Modified October 21, 2014
#   CSCI 352
#   Assignment 3
#
#
#   A Makefile that compiles all the necessary sources
#   in order to build properly running msh
#   compiles on 'make' and removes on 'clean'
#

CC = gcc
CFLAGS = -g

SRCS = server.c

OBJS = ${SRCS:.c=.o}

.SUFFIXES: .c .o

.c.o: $(DEPS)
	$(CC) $(CFLAGS) -c $<

server: $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^

clean:
	@rm -f $(OBJS)
	@rm -f core
	@rm -f server
