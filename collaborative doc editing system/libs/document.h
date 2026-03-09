#ifndef DOCUMENT_H

#define DOCUMENT_H
/**
 * This file is the header file for all the document functions. You will be tested on the functions inside markdown.h
 * You are allowed to and encouraged multiple helper functions and data structures, and make your code as modular as possible. 
 * Ensure you DO NOT change the name of document struct.
 */

 #include <stdio.h>
 #include <stdint.h>
 #include <stdlib.h>

 typedef struct chunk{
    // TODO
    char* content;
    struct chunk* next;
} chunk;

typedef struct {
    // TODO
    uint64_t version;
    //pending is the changes made but not saved
    chunk* pending;
    chunk* saved;
} document;



// Functions from here onwards.
#endif
