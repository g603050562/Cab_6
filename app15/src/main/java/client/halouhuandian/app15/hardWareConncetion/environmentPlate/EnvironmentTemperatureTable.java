package client.halouhuandian.app15.hardWareConncetion.environmentPlate;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/27
 * Description: 环境板温度数据表
 * 备注：原始温度key是小数表示(查看定义:TEMPERATURE_KEYS)
 * 6928.7832
 * 6442.0112
 * 5992.9141
 * 5578.3193
 * 5195.3418
 * 4841.3589
 * 4513.9810
 * 4211.0298
 */
public class EnvironmentTemperatureTable {

    private final static int[] TEMPERATURE_KEYS = {
            69287832, 64420112, 59929141, 55783193, 51953418, 48413589, 45139810, 42110298
            , 39305215, 36706440, 34297449
            , 32063125
            , 29989675
            , 28064453
            , 26275916
            , 24613464
            , 23067400
            , 21628838
            , 20289607
            , 19042219
            , 17879797
            , 16796017
            , 15785061
            , 14841584
            , 13960662
            , 13137754
            , 12368685
            , 11649598
            , 10976941
            , 10347432
            , 9758038
            , 9205962
            , 8688615
            , 8203603
            , 7748710
            , 7321889
            , 6921238
            , 6544999
            , 6191540
            , 5859346
            , 5547016
            , 5253245
            , 4976821
            , 4716621
            , 4471599
            , 4240781
            , 4023264
            , 3818204
            , 3624818
            , 3442375
            , 3270195
            , 3107640
            , 2954121
            , 2809084
            , 2672014
            , 2542428
            , 2419877
            , 2303940
            , 2194224
            , 2090361
            , 1992007
            , 1898841
            , 1810559
            , 1726881
            , 1647540
            , 1572290
            , 1500898
            , 1433144
            , 1368825
            , 1307749
            , 1249734
            , 1194612
            , 1142223
            , 1092417
            , 1045053
            , 1000000
            , 957132
            , 916333
            , 877492
            , 840505
            , 805274
            , 771707
            , 739717
            , 709222
            , 680144
            , 652411
            , 625954
            , 600707
            , 576610
            , 553604
            , 531635
            , 510651
            , 490602
            , 471443
            , 453130
            , 435621
            , 418878
            , 402862
            , 387539
            , 372876
            , 358842
            , 345405
            , 332538
            , 320214
            , 308408
            , 297096
            , 286253
            , 275860
            , 265895
            , 256338
            , 247171
            , 238376
            , 229937
            , 221836
            , 214061
            , 206594
            , 199424
            , 192537
            , 185920
            , 179562
            , 173452
            , 167578
            , 161930
            , 156499
            , 151276
            , 146251
            , 141417
            , 136764
            , 132286
            , 127976
            , 123825
            , 119828
            , 115978
            , 112270
            , 108697
            , 105254
            , 101935
            , 98736
            , 95652
            , 92678
            , 89809
            , 87042
            , 84373
            , 81797
            , 79312
            , 76912
            , 74596
            , 72360
            , 70201
            , 68115
            , 66101
            , 64155
            , 62274
            , 60457
            , 58701
            , 57003
            , 55362
            , 53775
            , 52240
            , 50755
            , 49319
            , 47930
            , 46586
            , 45285
            , 44026
            , 42807
            , 41627
            , 40484
            , 39378
            , 38306
            , 37268
            , 36263
            , 35289
            , 34345
            , 33430
            , 32543
            , 31683
            , 30850
            , 30042
            , 29258
            , 28498
            , 27761
            , 27045
            , 26352
            , 25678
            , 25025
            , 24391
            , 23775
            , 23178
            , 22598
            , 22034
            , 21487
            , 20956
            , 20440
            , 19939
            , 19452
            , 18978
            , 18518
            , 18071
            , 17637
            , 17215
            , 16804
            , 16405
            , 16017
            , 15640
            , 15273
            , 14915
            , 14568
            , 14230
            , 13901
            , 13582
            , 13270
            , 12967
            , 12672
            , 12385
            , 12106
            , 11833
            , 11568
            , 11310
            , 11059
            , 10814
            , 10576
            , 10343
            , 10117
            , 9896
            , 9681
            , 9472
            , 9268
            , 9069
            , 8875
            , 8686
            , 8501
            , 8321
            , 8146
            , 7975
            , 7808
            , 7646
            , 7487
            , 7332
            , 7181
            , 7034
            , 6890
            , 6749
            , 6612
            , 6479
            , 6348
            , 6221
            , 6096
            , 5975
            , 5856
            , 5740
            , 5627
            , 5517
            , 5409
            , 5303
            , 5200
            , 5099
            , 5001
            , 4905
            , 4811
            , 4719
            , 4630
            , 4542
            , 4456
            , 4372
            , 4290
            , 4210
            , 4132
            , 4055
            , 3980
            , 3907
            , 3836
            , 3765
            , 3697
            , 3630
            , 3564
            , 3500
            , 3437
            , 3376
            , 3315
            , 3257
            , 3199
            , 3142
            , 3087
            , 3033
            , 2980
            , 2928
            , 2878
            , 2828
            , 2779
            , 2732
            , 2685
            , 2639
            , 2594
            , 2550
            , 2507
            , 2465
            , 2424
            , 2384
            , 2344
            , 2305
            , 2267
            , 2230
            , 2193
            , 2157
            , 2122
            , 2088
            , 2054
            , 2021
            , 1988
            , 1957
            , 1925
            , 1895
            , 1865
            , 1835
            , 1806
            , 1778
            , 1750
            , 1723
            , 1696
            , 1670
            , 1644
            , 1619
            , 1594
            , 1570
            , 1546
            , 1522
            , 1499
            , 1477
            , 1455
            , 1433
            , 1412
            , 1391
            , 1370
            , 1350
            , 1330
            , 1310
            , 1291
            , 1272
            , 1254
            , 1236
            , 1218
            , 1201
            , 1183
            , 1166
            , 1150
            , 1134
            , 1118
            , 1102
            , 1086
            , 1071
    };
    private static final int[] TEMPERATURE_VALUES = {
                    -50
                    , -49
                    , -48
                    , -47
                    , -46
                    , -45
                    , -44
                    , -43
                    , -42
                    , -41
                    , -40
                    , -39
                    , -38
                    , -37
                    , -36
                    , -35
                    , -34
                    , -33
                    , -32
                    , -31
                    , -30
                    , -29
                    , -28
                    , -27
                    , -26
                    , -25
                    , -24
                    , -23
                    , -22
                    , -21
                    , -20
                    , -19
                    , -18
                    , -17
                    , -16
                    , -15
                    , -14
                    , -13
                    , -12
                    , -11
                    , -10
                    , -9
                    , -8
                    , -7
                    , -6
                    , -5
                    , -4
                    , -3
                    , -2
                    , -1
                    , 0
                    , 1
                    , 2
                    , 3
                    , 4
                    , 5
                    , 6
                    , 7
                    , 8
                    , 9
                    , 10
                    , 11
                    , 12
                    , 13
                    , 14
                    , 15
                    , 16
                    , 17
                    , 18
                    , 19
                    , 20
                    , 21
                    , 22
                    , 23
                    , 24
                    , 25
                    , 26
                    , 27
                    , 28
                    , 29
                    , 30
                    , 31
                    , 32
                    , 33
                    , 34
                    , 35
                    , 36
                    , 37
                    , 38
                    , 39
                    , 40
                    , 41
                    , 42
                    , 43
                    , 44
                    , 45
                    , 46
                    , 47
                    , 48
                    , 49
                    , 50
                    , 51
                    , 52
                    , 53
                    , 54
                    , 55
                    , 56
                    , 57
                    , 58
                    , 59
                    , 60
                    , 61
                    , 62
                    , 63
                    , 64
                    , 65
                    , 66
                    , 67
                    , 68
                    , 69
                    , 70
                    , 71
                    , 72
                    , 73
                    , 74
                    , 75
                    , 76
                    , 77
                    , 78
                    , 79
                    , 80
                    , 81
                    , 82
                    , 83
                    , 84
                    , 85
                    , 86
                    , 87
                    , 88
                    , 89
                    , 90
                    , 91
                    , 92
                    , 93
                    , 94
                    , 95
                    , 96
                    , 97
                    , 98
                    , 99
                    , 100
                    , 101
                    , 102
                    , 103
                    , 104
                    , 105
                    , 106
                    , 107
                    , 108
                    , 109
                    , 110
                    , 111
                    , 112
                    , 113
                    , 114
                    , 115
                    , 116
                    , 117
                    , 118
                    , 119
                    , 120
                    , 121
                    , 122
                    , 123
                    , 124
                    , 125
                    , 126
                    , 127
                    , 128
                    , 129
                    , 130
                    , 131
                    , 132
                    , 133
                    , 134
                    , 135
                    , 136
                    , 137
                    , 138
                    , 139
                    , 140
                    , 141
                    , 142
                    , 143
                    , 144
                    , 145
                    , 146
                    , 147
                    , 148
                    , 149
                    , 150
                    , 151
                    , 152
                    , 153
                    , 154
                    , 155
                    , 156
                    , 157
                    , 158
                    , 159
                    , 160
                    , 161
                    , 162
                    , 163
                    , 164
                    , 165
                    , 166
                    , 167
                    , 168
                    , 169
                    , 170
                    , 171
                    , 172
                    , 173
                    , 174
                    , 175
                    , 176
                    , 177
                    , 178
                    , 179
                    , 180
                    , 181
                    , 182
                    , 183
                    , 184
                    , 185
                    , 186
                    , 187
                    , 188
                    , 189
                    , 190
                    , 191
                    , 192
                    , 193
                    , 194
                    , 195
                    , 196
                    , 197
                    , 198
                    , 199
                    , 200
                    , 201
                    , 202
                    , 203
                    , 204
                    , 205
                    , 206
                    , 207
                    , 208
                    , 209
                    , 210
                    , 211
                    , 212
                    , 213
                    , 214
                    , 215
                    , 216
                    , 217
                    , 218
                    , 219
                    , 220
                    , 221
                    , 222
                    , 223
                    , 224
                    , 225
                    , 226
                    , 227
                    , 228
                    , 229
                    , 230
                    , 231
                    , 232
                    , 233
                    , 234
                    , 235
                    , 236
                    , 237
                    , 238
                    , 239
                    , 240
                    , 241
                    , 242
                    , 243
                    , 244
                    , 245
                    , 246
                    , 247
                    , 248
                    , 249
                    , 250
                    , 251
                    , 252
                    , 253
                    , 254
                    , 255
                    , 256
                    , 257
                    , 258
                    , 259
                    , 260
                    , 261
                    , 262
                    , 263
                    , 264
                    , 265
                    , 266
                    , 267
                    , 268
                    , 269
                    , 270
                    , 271
                    , 272
                    , 273
                    , 274
                    , 275
                    , 276
                    , 277
                    , 278
                    , 279
                    , 280
                    , 281
                    , 282
                    , 283
                    , 284
                    , 285
                    , 286
                    , 287
                    , 288
                    , 289
                    , 290
                    , 291
                    , 292
                    , 293
                    , 294
                    , 295
                    , 296
                    , 297
                    , 298
                    , 299
                    , 300
            };

    /**
     * 查询温度
     *
     * @param temperatureKey
     * @return
     */
    public static int queryTemperature(int temperatureKey) {
        final int index = matchTemperatureIndex(TEMPERATURE_KEYS, 0, TEMPERATURE_KEYS.length, temperatureKey);
        return index >= 0 && index < TEMPERATURE_VALUES.length ? TEMPERATURE_VALUES[index] : index;
    }

    /**
     * 匹配温度表的正确下标，没有匹配到就取相近的下标
     *
     * @param a
     * @param fromIndex
     * @param toIndex
     * @param key
     * @return
     */
    private  static int matchTemperatureIndex(int[] a, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        final int toIndexPosition = toIndex - 1;
        int high = toIndexPosition;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key) {
                high = mid - 1;
            } else if (midVal > key) {
                low = mid + 1;
            } else {
                return mid;
            }
        }

        //左侧出边界
        if (high < fromIndex) {
            return low;
        }
        //右侧出边界
        if (low > toIndexPosition) {
            return high;
        }

        //匹配相近的值，如果相等,优先取左侧值
        return Math.abs(a[low] - key) < Math.abs(a[high] - key) ? low : high;
    }
}
