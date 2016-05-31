//リバーシ思考ルーチン
//2011/09/25  by boco.hp3200.com
//--------------------------------------------------------------------------
package com.hp3200.boco.reversi;

public class ReversiAI {
    private final int WALL = -1;
    private final int BLANK = 0;
    private final int PLAYER = 1;
    private final int COM = 2;
    private final int[] MOVE = {-11, -10, -9, -1, 1, 9, 10, 11};
    private final int[] POINT_MAP = {    
            0,  0,  0, 0, 0, 0, 0,  0,  0,0,
            0,150,-20,20,10,10,20,-20,150,0,
            0,-20,-30,-5,-5,-5,-5,-30,-20,0,
            0, 20, -5,10, 5, 5,10, -5, 20,0,
            0, 10, -5, 5, 3, 3, 5, -5, 10,0,
            0, 10, -5, 5, 3, 3, 5, -5, 10,0,
            0, 20, -5,10, 5, 5,10, -5, 20,0,
            0,-20,-30,-5,-5,-5,-5,-30,-20,0,
            0,150,-20,20,10,10,20,-20,150,0,
            0,  0,  0, 0, 0, 0, 0,  0,  0,0
        };
    
    private int[] board = new int[100];
    private int bestPlace;
    private int bestDepth = 3;

    //コンストラクタ
    public ReversiAI() {
    }

    //処　理：盤を初期化する
    //引　数：startBoard……初めの盤
    //戻り値：なし
    public void setBoard(int[] startBoard) {
        for(int i=0;i<100;i++) {
            board[i] = startBoard[i];
        }
    }

    //処　理：最善手を返す
    //引　数：なし
    //戻り値：最善手
    public int getPlace() {
        return bestPlace;
    }
    
    //処　理：最善手を考える
    //引　数：myCoin……PLAYER or COM、depth……現在の深さ（１以上）
    //戻り値：最高点数
    public int think(int myCoin, int depth) {
    	int yourCoin = PLAYER;
        int i,j;
        int point;
        int bestPoint = -100000;
        int[] baseBoard = new int[100];
        
        //相手の石を設定＆盤を保存
        if(myCoin==PLAYER) yourCoin = COM;
        for(i=0;i<100;i++) {
            baseBoard[i] = board[i];
        }
        
        for(i=11;i<=88;i++) {
            if(board[i]==BLANK) {
                if(reverse(myCoin,i)==true) {
                    if(depth==bestDepth) {
                        point = evaluationFunction(myCoin);
                    } else {
                        if(isPass(yourCoin)==false) {
                            point = (-1)*think(yourCoin,depth+1);
                        } else {
                            if(isPass(myCoin)==false) {
                                point = think(myCoin,depth+1);
                            } else {
                                point = evaluationFunction(myCoin);
                            }
                        }
                    }

                    if(bestPoint<point) {
                        bestPoint = point;
                        if(depth==1) bestPlace = i;
                    }
                    for(j=0;j<100;j++) {
                        board[j] = baseBoard[j];
                    }
                }
            }
        }
        //最高点数を返す
        return bestPoint;
    }
    
    //処　理：そこに置けるなら、置いて裏返す
    //引　数：myCoin……PLAYER or COM、p……置く場所
    //戻り値：置けなかったらfalse
    boolean reverse(int myCoin, int p) {
    	int yourCoin = PLAYER;
    	int i, j, k;
    	boolean putting = false;

    	//相手の石を設定
    	if(myCoin==PLAYER) yourCoin = COM;

    	//置いて裏返す
    	if(board[p]==BLANK) {
    		for(i=0;i<8;i++) {
    			if(board[p+MOVE[i]]==yourCoin) {
    				for(j=2;j<8;j++) {
    					if(board[p+MOVE[i]*j]==myCoin) {
    						putting = true;
    						for(k=1;k<j;k++) {
    							board[p+MOVE[i]*k] = myCoin;
    						}
    						break;
    					} else if(board[p+MOVE[i]*j]==yourCoin) {
    					} else {
    						break;
    					}
    				}
    			}
    		}
    		if(putting==true) board[p] = myCoin;
    	}

    	//可否を返す
    	return putting;
    }
    
    //処　理：パスか調べる
    //引　数：myCoin……PLAYER or COM
    //戻り値：パスならtrue
    public boolean isPass(int myCoin) {
        int yourCoin = PLAYER;
        int i, j, p;
        boolean pass = true;
        
        //相手の石を設定
        if(myCoin==PLAYER) yourCoin = COM;
        
        //置けるか判断> 
        for(p=11;p<=88;p++) {
            if(board[p]==BLANK) {
                for(i=0;i<8;i++) {
                    if(board[p+MOVE[i]]==yourCoin) {
                        for(j=2;j<8;j++) {
                            if(board[p+MOVE[i]*j]==myCoin) {
                                pass = false;
                                break;
                            } else if(board[p+MOVE[i]*j]==yourCoin) {
                            } else {
                                break;
                            }
                        }
                    }
                    if(pass==false) break;
                }
            }
            if(pass==false) break;
        }
        
        //パスならtrueを返す
        return pass;
    }

    
    //処　理：評価関数
    //引　数：myCoin……PLAYER or COM
    //戻り値：点数
    public int evaluationFunction(int myCoin) {
        int yourCoin = PLAYER;
        int i;
        int point = 0;
        int count = 0;
        
        //相手の石を設定
        if(myCoin==PLAYER) yourCoin = COM;
        
      //空白を数える
        for(i=11;i<=88;i++) {
            if(board[i]==BLANK) count ++;
        }
        //点数を計算
        if(count==0) {
            //最後は数で勝負
            for(i=11;i<=88;i++) {
                if(board[i]==myCoin) point ++;
                if(board[i]==yourCoin) point --;
            }
        } else {
            //有利な場所と不利な場所がある
            for(i=11;i<=88;i++) {
                if(board[i]==myCoin) point += POINT_MAP[i];
                if(board[i]==yourCoin) point -= POINT_MAP[i];
            }
            //置ける場所は多い方が良い
            point += countPlace(myCoin)*10;
            point -= countPlace(yourCoin)*10;
        }

        //点数を返す
        return point;
    }
    
    //処　理：置ける場所を数える
    //引　数：myCoin……PLAYER or COM
    //戻り値：置ける場所数
    int countPlace(int myCoin) {
        int yourCoin = PLAYER;
        int i, j, p;
        int count = 0;
        boolean putting;

        //相手の石を設定
        if(myCoin==PLAYER) yourCoin = COM;

        //置ける場所を数える
        for(p=11;p<=88;p++) {
            putting = false;
            for(i=0;i<8;i++) {
                if(board[p+MOVE[i]]==yourCoin) {
                    for(j=2;j<8;j++) {
                        if(board[p+MOVE[i]*j]==myCoin) {
                            putting = true;
                            count ++;
                            break;
                        } else if(board[p+MOVE[i]*j]==yourCoin) {
                        } else {
                            break;
                        }
                    }
                    if(putting == true) break;
                }
            }
        }
        
        //置ける場所数を返す
        return count;
    }

    
    //処　理：読みの深さを決める
    //引　数：depth……読みの深さ
    //戻り値：なし
    public void setDepth(int depth) {
    	bestDepth = depth;
    }

}

