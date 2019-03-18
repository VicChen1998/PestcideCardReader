package com.vicchen.pestcidecardreader.Analysis;

import org.opencv.core.Mat;

/**
 * 矫正样板颜色
 * 
 * @// TODO: 18-7-14 优化颜色校正算法
 */
public class ColorAdjuster {

    /**
     * 主调整函数
     * - 计算出各通道均值
     * - 将各通道均值拉为210
     *
     * @param sampleBoard 接收Mat类型样板图片
     * @return 返回颜色校正后的图片
     */
    public Mat adjust(Mat sampleBoard) {
        // 计算各通道均值
        double total_blue = 0;
        double total_green = 0;
        double total_red = 0;

        for (int r = 0; r < sampleBoard.rows(); r++) {
            for (int c = 0; c < sampleBoard.cols(); c++) {
                double point[] = sampleBoard.get(r, c);
                total_blue += point[0];
                total_green += point[1];
                total_red += point[2];
            }
        }

        // 计算各通道转换率
        int pixels = sampleBoard.rows() * sampleBoard.cols();
        double kb = 210 / (total_blue / pixels);
        double kg = 210 / (total_green / pixels);
        double kr = 210 / (total_red / pixels);

        for (int r = 0; r < sampleBoard.rows(); r++) {
            for (int c = 0; c < sampleBoard.cols(); c++) {
                double point[] = sampleBoard.get(r, c);
                point[0] *= kb;
                point[1] *= kg;
                point[2] *= kr;

                point[0] = point[0] > 255 ? 255 : point[0];
                point[1] = point[1] > 255 ? 255 : point[1];
                point[2] = point[2] > 255 ? 255 : point[2];

                sampleBoard.put(r, c, point);
            }
        }

        return sampleBoard;
    }
}
