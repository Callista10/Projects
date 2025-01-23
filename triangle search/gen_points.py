import sys
import argparse
import random
import math

# calculates the euclidean distance of the two points
def euclidean_distance(a, b):
    return math.sqrt((a[0] - b[0]) ** 2 + (a[1] - b[1]) ** 2)

# generates the points with the parameters N, mindist and rseed (optional)
def generate_points(N, mindist, rseed=None):
    if N < 0: # if N less than zero then print the message to stderr and exit with -1
        print("N less than zero", file=sys.stderr)
        sys.exit(-1)
    elif mindist < 0 or mindist > 10: # if mindist less than zero or more than 10 then print the message to stderr and exit with -2
        print("mindist outside range", file=sys.stderr)
        sys.exit(-2)
    elif N > (10000 / (math.pi * (mindist ** 2))): # exit with -3 if N is greater than 1000/(pi x mindist^2) and print the message to stderr
        print("point saturation", file=sys.stderr)
        sys.exit(-3)

    random.seed(rseed)
    points = []

    while len(points) < N:
        point = (random.uniform(-50, 50), random.uniform(-50, 50))

        valid_point = True
        for existing_point in points:
            # if the euclidean distance between two points is less than the minimum distance, it is not a valid point
            if euclidean_distance(point, existing_point) < mindist:
                valid_point = False
                break

        if valid_point:
            points.append(point)

    # so the output of the point's coordinates are exactly 2 dp
    for point in points:
        print("{:.2f}, {:.2f}".format(point[0], point[1]))
    return 0

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("-N", type=int)
    parser.add_argument("-mindist", type=float)
    parser.add_argument("-rseed", type=int)
    args = parser.parse_args()

    # makes sure no necessary arguments are missing 
    if args.N is None or args.mindist is None:
        print("invalid arguments", file=sys.stderr)
        sys.exit(-4)

    generate_points(args.N, args.mindist, args.rseed)
