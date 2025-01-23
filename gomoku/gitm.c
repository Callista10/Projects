#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>

void initializeBoard(void);
void play(void);
void endGame(int,int,char, int);
bool checkWinR(int,int,char,int,int,int);
bool checkWin(int, int, char);

unsigned char aBoard[19][19];
char alphabetCol[19] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S'};
int colNo = -1;
int rowNo = -1;
char turn[20] = "Black";
char hist[1083];
char userInput[2000];
char temp[5];
bool valid = true;

int tieC = 0;

void initializeBoard()
{
    for (int i = 0; i < 19; i++)
    {
        for (int j = 0; j < 19; j++)
        {
            aBoard[i][j] = '.';
        }
    }
}


void endGame(int rowNo, int colNo, char curChar, int c)
{
    if (tieC == 361)
    {
        printf("Wow, a tie!\n");
        printf("%s\n",hist);
        printf("Thank you for playing!\n");
        exit(0);
    }
    
    if (checkWin(rowNo, colNo, curChar) ==  true)
    {
        printf("%s wins!\n", turn);
        printf("%s\n",hist);
        printf("Thank you for playing!\n");
        exit(0);
    }
}

bool checkWin(int rowNo, int colNo, char curChar)
{
    bool leftW = checkWinR(rowNo,colNo,curChar,1,0,-1);
    if(leftW) return true;
    bool rightW = checkWinR(rowNo,colNo,curChar,1,0,1);
    if(rightW) return true;
    bool topW = checkWinR(rowNo,colNo,curChar,1,1,0);
    if(topW) return true;
    bool bottW = checkWinR(rowNo,colNo,curChar,1,-1,0);
    if(bottW) return true;
    bool diagRBW = checkWinR(rowNo,colNo,curChar,1, 1,-1);
    if(diagRBW) return true;
    bool diagRTW = checkWinR(rowNo,colNo,curChar,1,1,1);
    if(diagRTW) return true;
    bool diagLTF = checkWinR(rowNo,colNo,curChar,1,-1,1);
    if(diagLTF) return true;
    bool diagLBW = checkWinR(rowNo,colNo,curChar,1,-1,-1);
    if(diagLBW) return true;
    return false;
}

bool checkWinR(int rowNo, int colNo, char curChar, int count, int changeRow, int changeCol)
{
    int currRow = rowNo + changeRow;
    int currCol = colNo + changeCol;
    int currCount = count + 1;
    if(currRow >= 19 || currCol >= 19 || currRow < 0 || currCol < 0)
    {
        return false;
    }
    const char Char = aBoard[currRow][currCol];
    
    if (Char != curChar)
    {
        return false;
    }
    if (currCount == 5)
    {
        return true;
    }
    return checkWinR(currRow,currCol,curChar, currCount, changeRow, changeCol);
    
}

void play()
{
    do
    {
        fgets(userInput,sizeof(userInput),stdin);
        char *first;
        first = strtok(userInput, " ");
        
        
        if(strcmp(userInput, "who\n") == 0)
        {
            printf("%c\n", turn[0]);
        }
        
        else if(strcmp(userInput, "resign\n") == 0)
        {
            if(strcmp(turn, "Black") == 0)
            {
                printf("White wins!\n");
                printf("%s\n", hist);
                printf("Thank you for playing!\n");
                exit(0);
            }
            else if(strcmp(turn, "White") == 0)
            {
                printf("Black wins!\n");
                printf("%s\n", hist);
                printf("Thank you for playing!\n");
                exit(0);
            }
        }
        
        else if(strcmp(first, "place") == 0 && userInput[0] != ' ')
        {
            char str[5] = {0};
            int i = 0;
            int j = 6;
            bool alreadyPrint = false;
            while(userInput[j] != '\n')
            {
                if(i < 4)
                {
                    str[i] = userInput[j];
                    i++;
                }
                if(userInput[j] == ' ')
                {
                    alreadyPrint = true;
                    printf("Invalid!\n");
                    valid = false;
                    break;
                }
                j++;
            }
            
            strcpy(temp, str);
            char c = str[0];
            
            if((c < 'A' || c > 'Z') && (c < 'a' || c > 'z'))
            {
                if(alreadyPrint == false)
                {
                    printf("Invalid!\n");
                    alreadyPrint = true;
                }
                
            }
            else
            {
                for (int i = 0; i < 19; i++)
                {
                    if(alphabetCol[i] == c)
                    {
                        colNo = i;
                        valid = true;
                        break;
                    }
                    else{
                        valid = false;
                        
                    }
                }
            }
            
            char srowNo[10] = {0};
            int k = 0;
            int l = 1;
            
            while(str[l] != '\0')
            {
                if(((str[l] < '0' || str[l] > '9') && (str[l] < 'A' || str[l] > 'Z') && (str[l] < 'a' || str[l] > 'z')) || str[l] == ' ')
                {
                    valid = false;
                    if(alreadyPrint == false)
                    {
                        printf("Invalid!\n");
                        alreadyPrint = true;
                    }
                    
                    break;
                }
                else if(str[l] < '0' || str[l] > '9')
                {
                    valid = false;
                    
                }
                srowNo[k] = str[l];
                if(l == 1 && str[l] == '0')
                {
                    valid = false;
                    break;
                }
                k++;
                l++;
                
            }
            
            rowNo = atoi(srowNo);
            
            if ((rowNo > 19 || rowNo <= 0) || c < 'A' || c > 'Z')
            {
                valid = false;
            }
            
            rowNo = 19 - rowNo;
            
            if(valid == true)
            {
                if(strcmp(turn, "Black") == 0)
                {
                    if (aBoard[rowNo][colNo] == '.')
                    {
                        aBoard[rowNo][colNo] = '#';
                        tieC += 1;
                        strcat(hist, temp);
                        endGame(rowNo, colNo, '#', c);
                        turn[0] = 'W';
                        turn[1] = 'h';
                        turn[2] = 'i';
                        turn[3] = 't';
                        turn[4] = 'e';
                        
                        
                    }
                    else
                    {
                        if (alreadyPrint == false)
                        {
                            printf("Occupied coordinate\n");
                        }
                    }
                }
                else if(strcmp(turn, "White") == 0)
                {
                    if (aBoard[rowNo][colNo] == '.')
                    {
                        aBoard[rowNo][colNo] = 'o';
                        tieC += 1;
                        strcat(hist,temp);
                        endGame(rowNo,colNo,'o', c);
                        
                        
                        
                        turn[0] = 'B';
                        turn[1] = 'l';
                        turn[2] = 'a';
                        turn[3] = 'c';
                        turn[4] = 'k';
                    }
                    else
                    {
                        if (alreadyPrint == false)
                        {
                            printf("Occupied coordinate\n");
                        }
                        
                    }
                }
            }
            else
            {
                if (alreadyPrint == false)
                {
                    printf("Invalid coordinate\n");
                }
                
            }
            
        }
        
        else if(strcmp(userInput, "history\n") == 0)
        {
            printf("%s\n", hist);
        }
        
        else if(strcmp(userInput, "view\n") == 0)
        {
            if (rowNo == -1 && colNo == -1)
            {
                printf("J10,.................................................");
            }
            else
            {
                int parsedCol = colNo + 1;
                int parsedRow = 19 - rowNo;
                const int centerX = 1 + ((5 * parsedCol * parsedCol) + (3 * parsedCol) + 4) % 19;
                const int centerY = 1 + ((4 * parsedRow * parsedRow) + (2 * parsedRow) - 4) % 19;
                
                int nCenterX = centerX - 1;
                int nCenterY = 19 - centerY;
                const int Bx1 = nCenterX+3;
                const int By1 = nCenterY+3;
                const int Bx2 = nCenterX-3;
                const int By2 = nCenterY-3;
                int tcenterY = centerY;
                char sno[5];
                char alp = alphabetCol[centerX-1];
                sprintf(sno, "%d", tcenterY);
                printf("%c", alp);
                printf("%s,", sno);
                
                for (int i = By2; i <= By1; i++)
                {
                    for (int j = Bx2; j <= Bx1; j++)
                    {
                        if(i >= 0 && i < 19 && j >= 0 && j < 19)
                        {
                            const char fill = aBoard[i][j];
                            printf("%c", fill);
                        }
                        else
                        {
                            printf("x");
                        }
                        
                    }
                }
                
            }
            printf("\n");
            
        }

        else
        {
            if(strcmp(userInput, "term\n") != 0)
            {
                printf("Invalid!\n");
            }
            
        }
        
    } while(strcmp(userInput, "term\n") != 0);
    exit(1);
}


int main()
{
    initializeBoard();
    play();
    return 0;
}
