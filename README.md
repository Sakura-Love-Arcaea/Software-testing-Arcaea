# 工作報告

@Sakura-Love-Arcaea

-   黑箱測試（1）
-   黑箱測試（2）

## 黑箱測試（1）

### OneNoteOneHit Test

針對 `judgement.getJudgement` 對於輸出(`Judgement.Timing`)的測試
首先我們知道會有 7 種不同的 partition，分別為：`ignore`, `fastMiss`, `fastFar`, `fastPure`, `latePure`, `lateFar`, `lateMiss`

使用案例是(同時考慮邊界情況)
- `ignore`：-500, -400
- `fastMiss`：-300, -250
- `fastFar`：-200, -100
- `fastPure`：-50, -1
- `latePure`:+1, +50 
- `lateFar`：+100, +200
- `lateMiss`：+250, +300


### OneNoteTwoHit Test

針對1個音符（判定區），測試`judgement.getJudgements`如何處理2個打擊點，比較判定結果`int[]`，是次測試是必須引入`chart`以及`log`
首先我們知道有 6 種不同的 partition，分別為：`ignore`, `fastMiss`, `fastFar`, `Pure`, `lateFar`, `lateMiss`(`fastFar`, `fastPure`合併為`Pure`)

案例一共有 7C2 = 21 種組合，大概如下：

ignore開始
- ignore + ignore
  - -302, -301
- ignore + fastMiss
  - -301, -201
- ignore + fastFar
  - -301, -150
- ignore + Pure
  - -301, 0
- ignore + lateFar
  - -301, 150
- ignore + lateMiss
  - -301, 201


fastMiss開始
- fastMiss + fastMiss
  - -202, -201
- fastMiss + fastFar
  - -202, -150
- 等


## 黑箱測試（2）


### TwoNotesOneHitOverlap Test

針對2個音符（判定區），測試`judgement.getJudgements`如何處理1個打擊點，比較判定結果`int[]`，是次測試是必須引入`chart`以及`log`
測試案例是用圖解法，把後面的判定區的左邊緣(fastMiss)，往前移動觸碰到前面的區間，為一種情況
針對每種情況，打擊各種重疊的部分為一個案例

例如第三個情況`secondFastMissUnderFirstPure`
此時有三個重疊區域:
- firstPure重疊ignore
- lateFar重疊fastMiss
- lateMiss重疊fastFar



## Density Test

針對一連串很近的音符，測試`judgement.getJudgements`如何處理，並比較判定結果`int[]`
情況有：
- 全部pure打擊
- 全部fastFar打擊
- 全部lateFar打擊
- 全部pure打擊，但第一個未被擊中
- 全部pure打擊，但第一個fastFar打擊

