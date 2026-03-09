#include "sound_seg.h"

// Load a WAV file into buffer
void wav_load(const char* filename, int16_t* dest){
    FILE* file = fopen(filename, "rb");

    if(file == NULL){
        perror("Error: could not open file");
        return;
    }

    //skip first 44 bytes cause of the wav header
    fseek(file, 44, SEEK_SET);
    fread(dest, sizeof(int16_t), 8000, file);

    fclose(file);
    return;
}

// Create/write a WAV file from buffer
void wav_save(const char* fname, int16_t* src, size_t len){
    FILE* file = fopen(fname, "wb");
    
    if(file == NULL){
        perror("Error: can't open file");
        return;
    }

    //make the header, it's 44 bytes
    uint8_t header[44] = {0};
    memcpy(header, "RIFF", 4);
    //find the file size: 36 + len * sizeof(int16_t) since 16 bit per sample
    uint32_t fsize = 36 + len * sizeof(int16_t);
    memcpy(&header[4], &fsize, 4);

    memcpy(&header[8], "WAVE", 4);

    //have space after fmt: "fmt " since its 4 bytes
    memcpy(&header[12], "fmt ", 4);

    //fmt size will be 16 cause it's PCM
    uint32_t fmt_size = 16;
    memcpy(&header[16], &fmt_size, 4);

    //audio format is 1 cause it's PCM
    uint16_t format = 1;
    memcpy(&header[20], &format, 2);

    //it's mono so 1 channel
    uint16_t channel = 1;
    memcpy(&header[22], &channel, 2);

    //8000Hz sample rate
    uint32_t sample_rate = 8000;
    memcpy(&header[24], &sample_rate, 4);

    //byterate = samplerate * numchannel * bitpersample / 8
    uint32_t byte_rate = 8000 * 1 * 16 / 8;
    memcpy(&header[28], &byte_rate, 4);

    //blockalign = numchannels * bitspersample / 8
    uint16_t block_align = 1 * 16 / 8;
    memcpy(&header[32], &block_align, 2);

    //16 bits per sample
    uint16_t bps = 16;
    memcpy(&header[34], &bps, 2);

    memcpy(&header[36], "data", 4);

    //datasize = numsamples * numchannels * bitspersample / 8
    uint32_t data_size = len * 1 * 16 / 8;
    memcpy(&header[40], &data_size, 4);

    //write the header into the file, then write the audio sample into the file
    fwrite(header, 1, 44, file);
    fwrite(src, sizeof(int16_t), len, file);

    fclose(file);

    return;
}

// Initialize a new sound_seg object
struct sound_seg* tr_init() {
    struct sound_seg* obj = (struct sound_seg*)malloc(sizeof(struct sound_seg));

    if(obj == NULL){
        printf("Error: Memory allocation failed!\n");
        return NULL;
    }

    //initialise the buffer, start and length
    obj->bseg = NULL;

    return obj;
}

// Destroy a sound_seg object and free all allocated memory
void tr_destroy(struct sound_seg* obj) {
    struct buffer_seg* current = obj->bseg;

    while(current != NULL){
        struct buffer_seg* temp = current->next;
        //free buffer
        if(current->buffer != NULL){
            free(current->buffer);
        }

        free(current);
        current = temp;
    }
    free(obj);
    
    return;
}

// Return the length of the segment
size_t tr_length(struct sound_seg* seg) {
    size_t total = 0;
    struct buffer_seg* current = seg->bseg;

    while(current != NULL){
        total += current->length;
        current = current->next;
    }
    return total;
}

// Read len elements from position pos into dest
void tr_read(struct sound_seg* track, int16_t* dest, size_t pos, size_t len) {
    //check if track or the buffer is empty:
    if(track == NULL || track->bseg == NULL){
        return;
    }

    struct buffer_seg* current = track->bseg;

    //keep track of the current pos
    size_t cpos = 0;

    //find which buffer segment pos is in
    while(current != NULL && (cpos + current->length) <= pos){
        cpos += current->length;
        current = current->next;
    }

    //once we found the pos in which buffer segment copy the data to dest
    size_t copied = 0;

    while(current != NULL && copied < len){
        //start is to check where we should start reading from
        size_t start = 0;

        if(pos > cpos){
            //if on the seg that contians pos then start reading from this buf
            start = pos - cpos;
        }
        else {
            //if not (so we're in the next seg), so read from 0 idx of that segbuf
            start = 0;
        }

        //spaces is available spaces in node
        size_t spaces = current->length - start;

        //elements to copy
        size_t to_copy = 0;

        if(spaces < (len - copied)){
            //so not enough space then copy what can be copied from available space
            to_copy = spaces;
        }
        else{
            to_copy = len - copied;
        }

        memcpy(dest + copied, current->buffer + start, sizeof(int16_t) * to_copy);
        copied += to_copy;

        cpos += current->length;
        current = current->next;
    }
    return;
}

// Write len elements from src into position pos
void tr_write(struct sound_seg* track, int16_t* src, size_t pos, size_t len) {
    if(track == NULL || src == NULL){
        return;
    }

    //if there is no bufseg node, then make one
    if(track->bseg == NULL){
        struct buffer_seg* new = (struct buffer_seg*)malloc(sizeof(struct buffer_seg));

        if(new == NULL){
            printf("Error: Memory allocation failed!\n");
            return;
        }

        new->buffer = (int16_t*)malloc(len * sizeof(int16_t));

        if(new->buffer == NULL){
            printf("Error: Memory allocation failed!\n");
            return;
        }

        new->length =  len;
        new->next = NULL;
        track->bseg = new;

        memcpy(new->buffer, src, len * sizeof(int16_t));
        return;
    }

    struct buffer_seg* current = track->bseg;
    
    //total length is the total length currently
    size_t total_length = current->length;
    size_t current_pos = 0;

    //find which bufseg node pos is in
    while(pos >= total_length && current->next != NULL){
        current_pos += current->length;
        current = current->next;
        total_length += current->length;
    }

    //if can fit the whole src in the current node
    if(pos + len <= total_length){
        memcpy(current->buffer + pos, src, len * sizeof(int16_t));
    }
    else{
        //if cannot fit whole src, check if current->next exists
        if(current->next != NULL){
            size_t offset = pos - current_pos;
            size_t copied = 0;
            size_t can_copy = total_length - pos;

            memcpy(current->buffer + offset, src, can_copy);
            copied = can_copy;
            can_copy = len - can_copy;
            current = current->next;

            if(current->length < can_copy) {
                current->buffer = (int16_t*)realloc(current->buffer, can_copy * sizeof(int16_t));
                if(current->buffer == NULL){
                    printf("Error: Memory allocation failed!\n");
                    return;
                }
                current->length = can_copy;
            }

            memcpy(current->buffer, src + copied, can_copy);
            return;
        }
        else{
            //if current->next doesnt exist then just resize current bufseg
            current->buffer = realloc(current->buffer, (pos + len) * sizeof(int16_t));
            current->length = pos + len;
            memcpy(current->buffer + pos, src, len * sizeof(int16_t));
            return;
        }
    }

}

// Delete a range of elements from the track
bool tr_delete_range(struct sound_seg* track, size_t pos, size_t len) {
    //check if track or the buffer is empty:
    if(track == NULL || track->bseg == NULL){
        return false;
    }

    struct buffer_seg* current = track->bseg;

    size_t current_pos = 0;

    //find which bufseg node pos is in
    while(pos >= (current_pos + current->length) && current->next != NULL){
        current_pos += current->length;
        current = current->next;
    }

    size_t to_delete = len;

    while(current != NULL && to_delete > 0){
        size_t offset = pos - current_pos;
        size_t can_delete = 0;

        if(offset + to_delete > current->length){
            can_delete = current->length - offset;
        }
        else{
            can_delete = to_delete;
        }

        memmove(current->buffer + offset, current->buffer + offset + can_delete,
            (current->length - (offset + can_delete)) * sizeof(int16_t));
        
        current->length -= can_delete;
        to_delete -= can_delete;

        if(to_delete > 0){
            //go to next bufseg
            current_pos += current->length;
            current = current->next;
            //update the pos cause the pos shifts every deletion
            pos = current_pos;
        }
    }

    return true;

}

// Returns a string containing <start>,<end> ad pairs in target
char* tr_identify(struct sound_seg* target, struct sound_seg* ad){
    if(target == NULL || ad == NULL){
        return NULL;
    }

    //put target into an array
    size_t target_length = 0;
    struct buffer_seg* temp = target->bseg;
    while(temp != NULL){
        target_length += temp->length;
        temp = temp->next;
    }

    int16_t* target_arr = (int16_t*)malloc(target_length * sizeof(int16_t));
    if(target_arr == NULL){
        printf("Error: Memory allocation failed!\n");
        return NULL;
    }

    temp = target->bseg;
    size_t current_pos = 0;
    while(temp != NULL){
        memcpy(target_arr + current_pos, temp->buffer, temp->length * sizeof(int16_t));
        current_pos += temp->length;
        temp = temp->next;
    }

    temp = ad->bseg;
    size_t ad_length = temp->length;
    int16_t* ad_arr = (int16_t*)malloc(ad_length * sizeof(int16_t));
    if(ad_arr == NULL){
        printf("Error: Memory allocation failed!\n");
        return NULL;
    }

    memcpy(ad_arr, temp->buffer, ad_length * (sizeof(int16_t)));

    //find the autocorrelation (ad), zero delay
    //int64_t to make sure it doesn't overflow when multiply int16_t x int16_t
    int64_t autocorrelation = 0;
    for(size_t i = 0; i < ad_length; i++){
        autocorrelation += ((int64_t)ad_arr[i] * (int64_t)ad_arr[i]);
    }

    //find the cross correlation, use array to store it
    size_t cc_length = target_length - ad_length + 1;
    size_t res_size = 1;
    char* res = malloc(res_size);
    if(res == NULL){
        printf("Error: Memory allocation failed!\n");
        return NULL;
    }

    res[0] = '\0';
    
    for(size_t lag = 0; lag < cc_length; lag++){
        int64_t cc_sum = 0;
        int64_t ncc_sum = 0;

        for(size_t i = 0; i < ad_length; i++){
            cc_sum += (int64_t)ad_arr[i] * (int64_t)target_arr[i + lag];
            ncc_sum += (int64_t)target_arr[i + lag] * (int64_t)target_arr[i + lag];

        }

        //find the normalised cross correlation (ncc)
        double ncc = (double)cc_sum / sqrt((double)autocorrelation * (double)ncc_sum);

        if(ncc >= 0.95){
            size_t start = lag;
            size_t end = lag + ad_length - 1;

            char temp[50];
            int tracker = snprintf(temp, sizeof(temp), "%zu,%zu\n", start, end);

            //resize res
            res = realloc(res, res_size + tracker);
            //append the pair to res
            strcat(res, temp);
            res_size += tracker;

        }
    }

    //remove trailing newline for last pair
    if(res_size > 1) {
        res[strlen(res) - 1] ='\0';
    }

    return res;
}

// Insert a portion of src_track into dest_track at position destpos
void tr_insert(struct sound_seg* src_track,
            struct sound_seg* dest_track,
            size_t destpos, size_t srcpos, size_t len) {
    
    //find which node to insert it into in the dest track
    struct buffer_seg* current = dest_track->bseg;
    size_t current_pos = 0;

    //find which bufseg node destpos is in
    while(destpos >= (current_pos + current->length) && current->next != NULL){
        current_pos += current->length;
        current = current->next;
    }

    size_t offset = destpos - current_pos;

    //check if need to split the node or simply add before or after node
    if(offset > 0 && offset < current->length){
        //if split then create new node
        struct buffer_seg* new = (struct buffer_seg*)malloc(sizeof(struct buffer_seg));
        if(new == NULL){
            printf("Error: Memory allocation failed!\n");
            return;
        }

        new->length = current->length - offset;
        new->buffer = malloc(sizeof(int16_t) * new->length);
        if(new->buffer == NULL){
            printf("Error: Memory allocation failed!\n");
            return;
        }
        memcpy(new->buffer, current->buffer + offset, sizeof(int16_t) * new->length);


        new->next = current->next;
        current->next = new;
        current->length = offset;
    }

    //reference the source track
    struct buffer_seg* src_ref = (struct buffer_seg*)malloc(sizeof(struct buffer_seg));
    if(src_ref == NULL){
        printf("Error: Memory allocation failed!\n");
        return;
    }

    src_ref->length = len;
    src_ref->buffer = malloc(sizeof(int16_t) * len);
    if(src_ref->buffer == NULL){
        printf("Error: Memory allocation failed!\n");
        return;
    }

    memcpy(src_ref->buffer, src_track->bseg->buffer + srcpos, sizeof(int16_t) * len);
    src_ref->next = current->next;

    current->next = src_ref;

    return;
}