#!/bin/bash

# Compile the C program
gcc -o smallest_triangle smallest_triangle.c -lm

# Runs the tests for the searcher (C program)
for IN in tests/searcher_*.in; do
    NAME=$(basename "$IN" .in)
    echo "Testing $NAME"
    if ./smallest_triangle < "$IN" | diff - "tests/$NAME.out" >/dev/null; then
        echo "  Passed"
    else
        echo "  Failed"
    fi
    echo
done

# Runs the tests for the generator (Python program)
for IN in tests/generator_*.in; do
    NAME=$(basename "$IN" .in)
    ARGS=$(cat "$IN")
    echo "Testing $NAME"
    
    # rungen_points.py with the arguments as input & store to OUTPUT
    OUTPUT=$(python3 gen_points.py $ARGS 2>&1)
    EXPECTED_OUTPUT=$(cat "tests/$NAME.out")
    
    if [ "$OUTPUT" = "$EXPECTED_OUTPUT" ]; then
        echo "  Passed"
    else
        echo "  Failed"
    fi
    echo
done


# Runs the combined tests for both generator and searcher
for IN in tests/combined_*.in; do
    NAME=$(basename "$IN" .in)
    echo "Testing $NAME"

    # run gen_points.py and store output into a temporary file
    TEMP_FILE=$(mktemp)
    python3 gen_points.py $(cat "$IN") > "$TEMP_FILE"

    # run smallest_triangle with the temporary file as input
    ./smallest_triangle < "$TEMP_FILE" > "${TEMP_FILE}.out"

    # Compare the output to the expected output
    if diff -q "${TEMP_FILE}.out" "tests/$NAME.out" >/dev/null; then
        echo "  Passed"
    else
        echo "  Failed"
    fi

    # delete the temporary file
    rm "$TEMP_FILE" "${TEMP_FILE}.out"
    echo
done
