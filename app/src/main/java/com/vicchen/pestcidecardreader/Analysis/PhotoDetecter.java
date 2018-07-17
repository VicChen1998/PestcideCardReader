package com.vicchen.pestcidecardreader.Analysis;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.Utils;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Double.POSITIVE_INFINITY;


/**
 * 从照片中识别出样板
 * - 将bitmat类型图片转化为opencv的Mat类型
 * - 调整大小为原图的0.1倍
 * - 将图片转化为灰度图并模糊
 * - canny边缘检测得到只剩边缘的图片
 * - 霍夫线性变换并对检测结果进行判断，当结果不理想时调整参数重新检测直到符合要求
 * - 将识别出的样板区域截取出
 * - 进行透视变换将样板拉正
 */
public class PhotoDetecter {

    // 检测出的直线
    private Mat lines;

    // 分类出的水平线和垂直线
    private Vector<double[]> vertical;
    private Vector<double[]> horizontal;

    // 检测出的交点
    private Vector<Point> intersections;


    /**
     * 主检测函数
     *
     * @param photo 接受Bitmap格式的照片
     * @return 返回Mat类型已截取的样板图片
     */
    public Mat detect(Bitmap photo) {

        // 转化为mat
        Mat src = new Mat();
        Utils.bitmapToMat(photo, src);

        // 缩小
        Mat resize = new Mat();
        Imgproc.resize(src, resize, new Size(0, 0), 0.1, 0.1, Imgproc.INTER_LINEAR);

        // 灰度
        Mat gray = new Mat();
        Imgproc.cvtColor(resize, gray, Imgproc.COLOR_BGR2GRAY);

        // 模糊
        Mat blur = new Mat();
        Imgproc.blur(gray, blur, new Size(3, 3));

        // 边缘检测
        Mat canny = new Mat();
        Imgproc.Canny(gray, canny, 200, 600, 3, false);


        // 霍夫线性变换初始参数
        int threshold = 15;
        double minLineLength = 30;
        double maxLineGap = 5;

        // 最多调整10次
        for (int i = 0; i < 10; i++, threshold -= 1, minLineLength -= 1, maxLineGap += 1) {
            // 直线数量过少，直接调整参数进入下一次检测
            if (!houghLinesP(canny, threshold, minLineLength, maxLineGap))
                continue;
            // 水平线或垂直线不足
            if (!lineClassfy())
                continue;
            // 交点数不足
            if (!getAllIntersections())
                continue;

            // 以上步骤都表示参数理想，结束检测
            break;
        }

        if (lines.rows() == 0 || vertical.size() < 4 || horizontal.size() < 2 || intersections.size() < 4) {
            return new Mat(0,0, CvType.CV_8UC1);
        }


        // 计算中心点
        int sum_x = 0, sum_y = 0;
        for (Point p : intersections) {
            sum_x += p.x;
            sum_y += p.y;
        }
        Point middle = new Point(sum_x / intersections.size(), sum_y / intersections.size());

        // 计算四个角点
        Point left_top = middle.clone();
        Point left_bottom = middle.clone();
        Point right_top = middle.clone();
        Point right_bottom = middle.clone();

        for (Point p : intersections) {
            left_top = (p.x <= left_top.x && p.y <= left_top.y) ? p : left_top;
            right_top = (p.x >= right_top.x && p.y <= right_top.y) ? p : right_top;
            left_bottom = (p.x <= left_bottom.x && p.y >= left_bottom.y) ? p : left_bottom;
            right_bottom = (p.x >= right_bottom.x && p.y >= right_bottom.y) ? p : right_bottom;
        }

        Log.d("middle", middle.toString());
        Log.d("left top", left_top.toString());
        Log.d("left bottom", left_bottom.toString());
        Log.d("right top", right_top.toString());
        Log.d("right bottom", right_bottom.toString());

        // 计算ROI位置
        double left = middle.x;
        double right = middle.x;
        double top = middle.y;
        double bottom = middle.y;

        for (Point p : intersections) {
            left = p.x < left ? p.x : left;
            right = p.x > right ? p.x : right;
            top = p.y < top ? p.y : top;
            bottom = p.y > bottom ? p.y : bottom;
        }

        // 提取ROI
        Mat roi = resize.submat((int) top, (int) bottom, (int) left, (int) right);

        // 生成透视变换矩阵
        ArrayList<Point> cut_sign = new ArrayList<>();
        cut_sign.add(new Point(left_top.x - left, left_top.y - top));
        cut_sign.add(new Point(left_bottom.x - left, left_bottom.y - top));
        cut_sign.add(new Point(right_top.x - left, right_top.y - top));
        cut_sign.add(new Point(right_bottom.x - left, right_bottom.y - top));

        ArrayList<Point> warp_sign = new ArrayList<>();
        warp_sign.add(new Point(0, 0));
        warp_sign.add(new Point(0, roi.rows()));
        warp_sign.add(new Point(roi.cols(), 0));
        warp_sign.add(new Point(roi.cols(), roi.rows()));

        Mat warpMatrix = Imgproc.getPerspectiveTransform(
                Converters.vector_Point2f_to_Mat(cut_sign),
                Converters.vector_Point2f_to_Mat(warp_sign)
        );

        // 进行透视变换
        Mat sampleBoard = new Mat();
        Imgproc.warpPerspective(roi, sampleBoard, warpMatrix, roi.size());

        return sampleBoard;
    }


    /**
     * 霍夫线性变换
     * 从canny边缘检测后的图像提取出直线
     * 如果检测直线数过少则返回false由调用者修改参数重新检测
     *
     * @param canny         canny边缘检测后的Mat
     * @param threshold     累加平面的阈值参数，在投票算法中得到一定的票数才能被认为是直线的一部分，检测直线不足时减小
     * @param minLineLength 直线的最小长度，小于这一长度的将被抛弃，防止杂乱线条干扰结果，检测直线不足时减小
     * @param maxLineGap    允许同一行点与点之间连接起来的最大距离，检测直线数不足时增大
     * @return 检测结果是否理想
     */
    private boolean houghLinesP(Mat canny, int threshold, double minLineLength, double maxLineGap) {

        lines = new Mat();
        Imgproc.HoughLinesP(canny, lines, 1, Math.PI / 180, threshold, minLineLength, maxLineGap);

        Log.d("DETECTED LINES", lines.rows() + "");

        // 检测到的直线数小于6，判定为不理想
        if (lines.rows() < 6)
            return false;

        return true;
    }


    /**
     * 判断水平线或垂直线
     * lines是类的私有变量，由houghLinesP初始化
     * 计算霍夫线性变换检测出的直线的斜率，将直线区分为水平线和垂直线
     * - 斜率在±0.2之间的被认为是水平线
     * - 斜率＞5或＜-5的被认为是垂直线
     * - 其余的将被舍弃
     * 如果水平线少于2条或垂直线少于4条则认为霍夫线性变换参数不理想
     *
     * @return 检测结果是否理想
     */
    private boolean lineClassfy() {
        vertical = new Vector<>();
        horizontal = new Vector<>();

        for (int i = 0; i < lines.rows(); i++) {
            double line[] = lines.get(i, 0);
            double k = slope(line);

            Log.d("k", k + "");

            if (-0.2 < k && k < 0.2)
                horizontal.add(line);
            if (k < -5 || k > 5)
                vertical.add(line);
        }

        Log.d("HORIZONTAL", horizontal.size() + "");
        Log.d("VERTICAL", vertical.size() + "");

        if (horizontal.size() < 2)
            return false;
        if (vertical.size() < 4)
            return false;

        return true;
    }


    /**
     * 计算所有直线的交点
     * horizontal和vertical是类的私有变量，由lineClassfy生成
     * 如果计算出的交点超过图片范围则舍弃
     * 如果计算出的交点少于4个则认为霍夫线性变换参数不理想
     *
     * @return 检测结果是否理想
     */
    private boolean getAllIntersections() {
        intersections = new Vector<>();

        for (double h[] : horizontal) {
            for (double v[] : vertical) {
                Point p = countIntersection(h, v);
                // 如果交点超出图片范围则舍弃
                if (p.x > 0 && p.y > 0)
                    intersections.add(p);
            }
        }

        Log.d("INTERSECTION POINT", intersections.size() + "");

        if (intersections.size() < 4)
            return false;

        return true;
    }


    /**
     * 计算直线斜率
     * 如果直线垂直则返回POSITIVE_INFINITY
     *
     * @param line 接受double[]格式的直线计算斜率
     * @return 返回double类型的斜率
     */
    private double slope(double line[]) {
        double dy = line[1] - line[3];
        double dx = line[0] - line[2];
        dx = (dx == 0) ? 1 / POSITIVE_INFINITY : dx;
        return dy / dx;
    }

    /**
     * 计算两直线交点
     *
     * @param line1 double[]类型直线
     * @param line2 double[]类型直线
     * @return 返回两直线交点 opencv Point类型
     */
    private Point countIntersection(double line1[], double line2[]) {
        double k1 = slope(line1);
        double k2 = slope(line2);

        if (k1 == POSITIVE_INFINITY) {
            double b2 = line2[1] - k2 * line2[0];
            int x = (int) line1[0];
            int y = (int) (k2 * x + b2);
            return new Point(x, y);

        } else if (k2 == POSITIVE_INFINITY) {
            double b1 = line1[1] - k1 * line1[0];
            int x = (int) (line2[0]);
            int y = (int) (k1 * x + b1);
            return new Point(x, y);

        } else {
            double b1 = line1[1] - k1 * line1[0];
            double b2 = line2[1] - k2 * line2[0];
            int x = (int) ((b2 - b1) / (k1 - k2));
            int y = (int) (k1 * x + b1);
            return new Point(x, y);
        }
    }
}
