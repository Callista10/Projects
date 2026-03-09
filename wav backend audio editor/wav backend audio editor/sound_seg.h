#include <stdint.h>
#include <stddef.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <stdio.h>
#include <math.h>


//making linked list for the buffer
struct buffer_seg{
    int16_t *buffer;
    size_t length;
    struct buffer_seg* next;
};

struct sound_seg {
    //TODO
    struct buffer_seg* bseg;
};

void wav_load(const char* filename, int16_t* dest);
void wav_save(const char* fname, int16_t* src, size_t len);
struct sound_seg* tr_init();
void tr_destroy(struct sound_seg* obj);
size_t tr_length(struct sound_seg* seg);
void tr_read(struct sound_seg* track, int16_t* dest, size_t pos, size_t len);
void tr_write(struct sound_seg* track, int16_t* src, size_t pos, size_t len);
bool tr_delete_range(struct sound_seg* track, size_t pos, size_t len);
char* tr_identify(struct sound_seg* target, struct sound_seg* ad);
void tr_insert(struct sound_seg* src_track, struct sound_seg* dest_track, size_t destpos, size_t srcpos, size_t len);