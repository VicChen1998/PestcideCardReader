# 农药残留检测仪
![Author](https://img.shields.io/badge/author-@vicchen-blue.svg?style=flat)

## 简介

农药残留检测仪 及 配套Android app

## 基于树莓派的农药残留检测仪
 
 * 使用PyQt构建图形界面
 * 使用uvc协议摄像头拍摄检测板图像
 * 按各点样孔位置锚点切割8个小样品图像进行分析
 * 连接嵌入式打印机打印检测结果
 * 使用sqlite存储检测结果及图像
 * 使用pywifi连接wifi，通过samba支持实验室局域网共享分析数据

### 部分功能界面展示

<table style="text-align:center">
    <tr><td>
        <h3>开始界面</h3>
        <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/homepage.jng?raw=true">
    </td></tr>
    <tr><td>
        <h3>检测界面</h3>
        <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/analyse_page.jng?raw=true">
    </td></tr>
    <tr><td>
        <h3>检测记录</h3>
        <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/record_page.jng?raw=true">
    </td></tr>
    <tr><td>
        <h3>连接wifi</h3>
        <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/wifi.jng?raw=true">
    </td></tr>
    <tr><td>
        <h3>打印结果</h3>
        <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/print.jng?raw=true">
    </td></tr>
</table>

## Android app

 * 使用手机摄像头拍摄检测板图像
 * app内置opencv
 * canny边缘检测
 * 霍夫线性变换提取ROI区域
 * 透视变换将ROI区域进行矫正
 * 进行白平衡矫正
 * 按各点样孔位置锚点切割8个小样品图像进行分析
 * 使用sqlite存储检测结果及图像

### 部分功能界面展示

<table style="text-align:center">
    <tr>
        <td>
            <h3>拍摄照片</h3>
            <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/photo_1.png?raw=true">
        </td>
        <td>
            <h3>分析结果</h3>
            <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/analyse_1.png?raw=true">
        </td>
    </tr>
    <tr>
        <td>
            <h3>拍摄照片</h3>
            <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/photo_2.png?raw=true">
        </td>
        <td>
            <h3>分析结果</h3>
            <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/analyse_3.png?raw=true">
        </td>
    </tr>
    <tr>
        <td>
            <h3>检测记录</h3>
            <img src="https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/record.png?raw=true">
        </td>
    </tr>
</table>

<!-- |拍摄照片|分析结果|
|-|-|
|![image](https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/photo_1.png?raw=true)|![image](https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/analyse_1.png?raw=true)|

|拍摄照片|分析结果|
|-|-|
|![image](https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/photo_2.png?raw=true)|![image](https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/analyse_2.png?raw=true)|

|拍摄照片|
|-|
|![image](https://github.com/VicChen1998/PestcideCardReader/blob/master/readme_imgs/record.png?raw=true)| -->
