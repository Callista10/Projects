// TODO
#include <stdio.h>
#include <math.h>
#define MAX 1000

struct Point {
    double x;
    double y;
};

double length(struct Point p1, struct Point p2) {
    return sqrt(pow(p1.x - p2.x, 2) + pow(p1.y - p2.y, 2));
}

double area(struct Point p1, struct Point p2, struct Point p3) {
    //use Heron's formula to find the area
    double a = length(p1, p2);
    double b = length(p2, p3);
    double c = length(p3, p1);
    double s = (a + b + c) / 2;
    return sqrt(s * (s - a) * (s - b) * (s - c)); 
}

int main() {
    struct Point points[MAX];
    struct Point final_points[3];
    int count = 0;
    int min_len = 1000;

    //make sure there is a limit of 1000 input accepted
    for(int i = 0; i < MAX; i++)
    {
        if(scanf("%lf, %lf", &points[count].x, &points[count].y) != 2)
        {
            break;
        }
        count++;
    }

    printf("read %d points\n", count);

    //it is definitely not a triangle if there are less than 3 points given
    if(count < 3)
    {
        for(int i = 0; i < count; i++)
        {
            printf("%.2lf, %.2lf", points[i].x, points[i].y);
            printf("\n");
        }
        printf("This is not a triangle\n");
    }
    else
    {
        for(int i = 0; i < count; i++)
        {
            for(int j = i + 1; j < count; j++)
            {
                for(int k = j + 1; k < count; k++)
                {
                    //calculate the side length
                    double side1 = length(points[i], points[j]);
                    double side2 = length(points[j], points[k]);
                    double side3 = length(points[k], points[i]);
                    double total_length = side1 + side2 + side3;
                
                    //check if the sum of the side lengths is the minimum
                    if(total_length < min_len)
                    {
                        min_len = total_length;
                        final_points[0] = points[i];
                        final_points[1] = points[j];
                        final_points[2] = points[k];
                    }
                }
            }
        }

        //print the closest points
        printf("%.2lf, %.2lf\n", final_points[0].x, final_points[0].y);
        printf("%.2lf, %.2lf\n", final_points[1].x, final_points[1].y);
        printf("%.2lf, %.2lf\n", final_points[2].x, final_points[2].y);

        double triangle_area = area(final_points[0], final_points[1], final_points[2]);

        //check if the points form a triangle
        if(triangle_area > 0.001)
        {
            printf("This is a triangle\n");
        }
        else
        {
            printf("This is not a triangle\n");
        }
    }

    return 0;
}