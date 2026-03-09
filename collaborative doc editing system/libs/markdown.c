#include "markdown.h"

//helper function
chunk* create_chunk(const char* content, size_t len){
    chunk* new = (chunk*)malloc(sizeof(chunk));
    new->content = (char*)malloc(len+1);
    strncpy(new->content, content, len);
    new->content[len] = '\0';
    new->next = NULL;

    return new;
}

chunk* split_chunk(chunk* current, size_t pos){
    size_t len = strlen(current->content);

    //create a new chunk for the rhs of the split
    chunk* new = create_chunk(current->content + pos, len - pos);
    current->content[pos] = '\0';
    new->next = current->next;
    current->next = new;

    return new;
}

chunk* copy_list(chunk* list){
    //make new chunk for the copy
    chunk* new = create_chunk(list->content, strlen(list->content));
    chunk* current = new;
    list = list->next;

    while(list != NULL){
        current->next = create_chunk(list->content, strlen(list->content));
        current = current->next;
        list = list->next;
    }
    return new;
}

document* markdown_init(){
    document *doc = (document*)malloc(sizeof(document));
    doc->version = 0;
    doc->pending = NULL;
    doc->saved = NULL;

    return doc;
}

void markdown_free(document *doc){
    chunk* current = doc->pending;

    while(current != NULL){
        chunk* temp = current->next;
        if(current->content != NULL){
            free(current->content);

        }

        free(current);
        current = temp;
    }

    //free doc->saved as well
    current = doc->saved;

    while(current != NULL){
        chunk* temp = current->next;
        if(current->content != NULL){
            free(current->content);

        }

        free(current);
        current = temp;
    }
    return;
}

void markdown_increment_version(document *doc){
    if(doc == NULL) {
        return;
    }

    //if doc->saved is not empty, free old stuff before overwriting w/ new
    if(doc->saved != NULL){
        chunk* current = doc->saved;
        //go through the list and free content
        while(current != NULL){
            chunk* temp = current->next;
            free(current->content);
            free(current);
            current = temp;
        }

        doc->saved = NULL;
    }

    //copy version 0's pending chunk list to version 1's saved
    doc->saved = copy_list (doc->pending);

    doc->version += 1;
    return;
}

int markdown_insert(document* doc, uint64_t version, size_t pos, const char* content){
    if(doc->version != version){
        return 1;
    }

    chunk* current = doc->pending;
    size_t current_pos = 0;

    //if doc is empty, then just create a new node
    if(current == NULL || pos == 0){
        chunk* new = create_chunk(content, strlen(content));

        //connect new node to the existing node chunk
        if(pos == 0){
            new->next = doc->pending;
        }

        doc->pending = new;
        return 0;
    }
    

    //find the right chunk to insert it in
    while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
        current_pos += strlen(current->content);
        current = current->next;
    }

    size_t offset = pos - current_pos;

    //check if need to split or just add before/after (no split)
    if(offset > 0 && offset < strlen(current->content)){
        //if need to split
        split_chunk(current, offset);
    }

    //now make a new node for the content we want to insert
    chunk* new = create_chunk(content, strlen(content));
    new->next = current->next;
    current->next = new;

    return 0;
}

int markdown_delete(document *doc, uint64_t version, size_t pos, size_t len){
    if(doc->version != version){
        return 1;
    }

    chunk* current = doc->pending;

    size_t current_pos = 0;

    //find which node pos is in
    while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
        current_pos += strlen(current->content);
        current = current->next;
    }

    size_t to_delete = len;

    while(current != NULL && to_delete > 0){
        size_t offset = pos - current_pos;
        size_t can_delete = 0;

        if((offset + to_delete) > strlen(current->content)){
            can_delete = strlen(current->content) - offset;
        }
        else{
            can_delete = to_delete;
        }

        memmove(current->content + offset, current->content + offset + can_delete, 
            (strlen(current->content) - (offset + can_delete)) + 1);
        to_delete -= can_delete;

        if(to_delete > 0){
            //go to next chunk
            current_pos += strlen(current->content);
            current = current->next;
            //update the pos cause pos shifts every deletion
            pos = current_pos;
        }
    }
    return 0;
}

int markdown_heading(document *doc, uint64_t version, int level, size_t pos){
    if(doc->version != version){
        return 1;
    }

    //are we inserting at start of doc? if no add \n before
    if(pos > 0){
        chunk* current = doc->pending;
        size_t current_pos = 0;

        while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
            current_pos += strlen(current->content);
            current = current->next;
        }
        
        size_t offset = pos - current_pos;
    
        //check if prev character is \n already or not
        if(offset > 0 && current->content[offset - 1] != '\n'){
            //add newline
            markdown_insert(doc, version, pos, "\n");
            pos++;
        }
    }

    char heading[5] = {'_'};
    for(int i = 0; i < level; i++){
        heading[i] = '#';
    }
    heading[level] = ' ';
    heading[level + 1] = '\0';

    markdown_insert(doc, version, pos, heading);
    
    return 0;
}

int markdown_bold(document *doc, uint64_t version, size_t start, size_t end){
    if(doc->version != version){
        return 1;
    }

    char* bold = "**";
    markdown_insert(doc, version, end, bold);
    markdown_insert(doc, version, start, bold);
    
    return 0;
}

int markdown_italic(document *doc, uint64_t version, size_t start, size_t end){
    if(doc->version != version){
        return 1;
    }

    char* italic = "*";
    markdown_insert(doc, version, end, italic);
    markdown_insert(doc, version, start, italic);
    return 0;
}

int markdown_blockquote(document *doc, uint64_t version, size_t pos){
    if(doc->version != version){
        return 1;
    }

    //since we're incrementing pos as we add \n, keep track in count
    size_t count = 0;

    //are we inserting at start of doc? if no add \n before
    if(pos > 0){
        chunk* current = doc->pending;
        size_t current_pos = 0;

        while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
            current_pos += strlen(current->content);
            current = current->next;
        }
        
        size_t offset = pos - current_pos;
    
        //check if prev character is \n already or not
        if(offset > 0 && offset <= strlen(current->content) && current->content[offset-1] != '\n'){
            //add newline
            markdown_insert(doc, version, pos, "\n");
            pos++;
            count++;
        }
    }

    char* blockquote = "> ";
    markdown_insert(doc, version, pos - count - 1, blockquote);

    return 0;
}

int markdown_ordered_list(document *doc, uint64_t version, size_t pos){
    if(doc->version != version){
        return 1;
    }

    //are we inserting at start of doc? if no add \n before
    if(pos > 0){
        chunk* current = doc->pending;
        size_t current_pos = 0;

        while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
            current_pos += strlen(current->content);
            current = current->next;
        }
        
        size_t offset = pos - current_pos;
    
        //check if prev character is \n already or not
        if(offset > 0 && current->content[offset-1] != '\n'){
            //add newline
            markdown_insert(doc, version, pos, "\n");
        }
    }

    //find how many numbered items already exist in the doc
    char* content = markdown_flatten(doc);
    int num = 0;
    char* line = strtok(content, "\n");

    //this counts no. of lines with 1. and so on
    while(line != NULL){
        int n = 0;
        if(sscanf(line, "%d. ", &n) == 1){
            num++;
        }

        line = strtok(NULL, "\n");

    }
    
    free(content);

    char no[25];
    snprintf(no, sizeof(no), "%d. ", num+1);

    //insert the '1. ' and so on
    if(strcmp(no, "1. ") == 0){
        markdown_insert(doc, version, pos, no);
    }
    else{
        markdown_insert(doc, version, pos-1, no);
    }

    return 0;
}

int markdown_unordered_list(document *doc, uint64_t version, size_t pos){
    if(doc->version != version){
        return 1;
    }
    
    //since we're incrementing pos as we add \n, keep track in count
    size_t count = 0;

    //are we inserting at start of doc? if no add \n before
    if(pos > 0){
        chunk* current = doc->pending;
        size_t current_pos = 0;

        while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
            current_pos += strlen(current->content);
            current = current->next;
        }
        
        size_t offset = pos - current_pos;
    
        //check if prev character is \n already or not
        if(offset > 0 && current->content[offset-1] != '\n'){
            //add newline
            markdown_insert(doc, version, pos, "\n");
            pos++;
            count++;
        }
    }

    char* dash = "- ";
    markdown_insert(doc, version, pos-count, dash);

    return 0;
}

int markdown_code(document *doc, uint64_t version, size_t start, size_t end){
    if(doc->version != version){
        return 1;
    }

    char* code = "`";
    markdown_insert(doc, version, end, code);
    markdown_insert(doc, version, start-1, code);

    return 0;
}

int markdown_horizontal_rule(document *doc, uint64_t version, size_t pos){
    if(doc->version != version){
        return 1;
    }

    //are we inserting at start of doc? if no add \n before
    if(pos > 0){
        chunk* current = doc->pending;
        size_t current_pos = 0;

        while(pos >= (current_pos + strlen(current->content)) && current->next != NULL){
            current_pos += strlen(current->content);
            current = current->next;
        }
        
        size_t offset = pos - current_pos;
    
        //check if prev character is \n already or not
        if(offset > 0 && current->content[offset-1] != '\n'){
            //add newline
            markdown_insert(doc, version, pos, "\n");
            pos++;
        }
    }

    char* line = "---\n";
    markdown_insert(doc, version, pos, line);

    return 0;
}

int markdown_link(document *doc, uint64_t version, size_t start, size_t end, const char *url){
    if(doc->version != version){
        return 1;
    }

    size_t len = strlen(url);
    char* link = (char*)malloc(len + 5);
    sprintf(link, "](%s)", url);

    markdown_insert(doc, version, end, link);
    markdown_insert(doc, version, start, "[");

    free(link);
    return 0;
}

void markdown_print(const document *doc, FILE *stream){
    chunk* current = doc->saved;

    while(current != NULL) {
        fprintf(stream, "%s", current->content);
        current = current->next;
    }

}

char *markdown_flatten(const document *doc){
    //find total length of whole doc
    size_t total = 0;
    chunk* current = doc->saved;

    while(current != NULL){
        total += strlen(current->content);
        current = current->next;
    }

    //res will contain the whole doc in a single string
    char* res = (char*)malloc(total + 1);

    //go through the entire doc again but this time copy the content to res
    current = doc->saved;
    size_t pos = 0;

    while(current != NULL){
        memcpy(res + pos, current->content, strlen(current->content));
        pos += strlen(current->content);
        current = current->next;
    }

    res[total] = '\0';
    return res;
}

int markdown_newline(document *doc, uint64_t version, size_t pos){

    if(doc->version != version){
        return 1;
    }

    char* newline = "\n";
    markdown_insert(doc, version, pos, newline);

    return 0;
}