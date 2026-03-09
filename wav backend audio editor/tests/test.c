#include "sound_seg.h"
#include <assert.h>

void test_length_and_write(){
    struct sound_seg* s0 = tr_init();

    //Test 1: s0 should be 0 length initially
    assert(tr_length(s0) == 0);
    printf("Test 1 passed!\n");
    
    //Test 2: s0 updated length after tr_write
    int16_t buf[5] = {1, 2, 3, 4, 5};
    tr_write(s0, buf, 0, 5);
    assert(tr_length(s0) ==  5);
    printf("Test 2 passed!\n");

    //Test 3: expand s0 and check tr_length
    int16_t expand[2] = {6, 7};
    tr_write(s0, expand, 5, 2);

    assert(tr_length(s0) ==  7);
    printf("Test 3 passed!\n");

    tr_destroy(s0);
}

void test_length_and_delete(){
    struct sound_seg* s0 = tr_init();
    
    //Test 4: s0 updated length after deleting 4 elements
    int16_t buf[7] = {1, 2, 3, 4, 5, 6, 7};
    tr_write(s0, buf, 0, 7);

    tr_delete_range(s0, 1, 4);
    assert(tr_length(s0) ==  3);
    printf("Test 4 passed!\n");

    tr_destroy(s0);
}

void test_write_and_read(){
    struct sound_seg* s0 = tr_init();
    
    //Test 5: s0 check if write and read works properly
    int16_t buf[7] = {1, 2, 3, 4, 5, 6, 7};
    tr_write(s0, buf, 0, 7);

    int16_t dest[7] = {0};
    tr_read(s0, dest, 0, 7);

    for(int i = 0; i < 7; i++){
        assert(dest[i] == buf[i]);
    }

    printf("Test 5 passed!\n");

    tr_destroy(s0);
}

void test_insert(){
    struct sound_seg* s0 = tr_init();
    struct sound_seg* s1 = tr_init();

    int16_t buf0[5] = {1, 2, 3, 4, 5};
    int16_t buf1[3] = {6, 7, 8};

    tr_write(s0, buf0, 0, 5);
    tr_write(s1, buf1, 0, 3);

    //Test 6: check if insert works if s0 and s1 are plain (no prior insertion)
    tr_insert(s1, s0, 1, 0, 2);

    int16_t dest[7] = {0};
    tr_read(s0, dest, 0, 7);

    int16_t expected[7] = {1, 6, 7, 2, 3, 4, 5};

    for(int i = 0; i < 7; i++){
        assert(dest[i] == expected[i]);
    }

    printf("Test 6 passed!\n");

    tr_destroy(s0);
    tr_destroy(s1);
}

void test_identify(){
    struct sound_seg* target = tr_init();
    struct sound_seg* ad = tr_init();

    int16_t buf0[7] = {10, -19, 3, 4, -25, 18, -18};
    int16_t buf1[2] = {3, 4};

    tr_write(target, buf0, 0, 7);
    tr_write(ad, buf1, 0, 2);

    //Test 7: check if function can correctly identify ads in the target
    char* actual = tr_identify(target, ad);
    char* expected = "2,3";

    assert(strcmp(actual, expected) == 0);

    printf("Test 7 passed!\n");

    tr_destroy(target);
    tr_destroy(ad);

}

int main(){
    test_length_and_write();
    test_length_and_delete();
    test_write_and_read();
    test_insert();
    test_identify();

    return 0;
}